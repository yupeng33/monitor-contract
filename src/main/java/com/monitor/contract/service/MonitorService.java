package com.monitor.contract.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.monitor.contract.common.enums.MonitorStatusEnum;
import com.monitor.contract.common.enums.RecordTypeEnum;
import com.monitor.contract.common.enums.StakedStatusEnum;
import com.monitor.contract.common.exception.BizException;
import com.monitor.contract.mapper.MonitorMapper;
import com.monitor.contract.model.entity.LpInfo;
import com.monitor.contract.model.entity.LpPosition;
import com.monitor.contract.model.entity.LpRecord;
import com.monitor.contract.model.entity.Monitor;
import com.monitor.contract.model.vo.Positions;
import com.monitor.contract.util.Web3jUtil;
import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.*;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class MonitorService extends ServiceImpl<MonitorMapper, Monitor> {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public static final int CURRENCY_DECIMAL = 18;

    // IncreaseLiquidity(uint256,uint128,uint256,uint256)
    public static final String INCREASE_LIQUIDITY_TOPIC = EventEncoder.encode(
            new Event("IncreaseLiquidity", Arrays.asList(
                new TypeReference<Uint256>() {},
                new TypeReference<Uint128>() {},
                new TypeReference<Uint256>() {},
                new TypeReference<Uint256>() {})));

    // DecreaseLiquidity(uint256,uint128,uint256,uint256)
    public static final String DECREASE_LIQUIDITY_TOPIC = EventEncoder.encode(
            new Event("DecreaseLiquidity", Arrays.asList(
                new TypeReference<Uint256>() {},
                new TypeReference<Uint128>() {},
                new TypeReference<Uint256>() {},
                new TypeReference<Uint256>() {})));

    // Transfer(address,address,uint256)
    public static final String TRANSFER_TOPIC = EventEncoder.encode(
            new Event("Transfer", Arrays.asList(
                new TypeReference<Address>() {},
                new TypeReference<Address>() {},
                new TypeReference<Uint256>() {})));

    @Resource
    private LpInfoService lpInfoService;
    @Resource
    private LpPositionService lpPositionService;
    @Resource
    private LpDictService lpDictService;
    @Resource
    private LpRecordService lpRecordService;

    public void monitorOfStep(Monitor monitor) {
        EthFilter ethFilter;
        BigInteger confirmBlockSize = lpDictService.getConfirmBlockSize(monitor.getChainId());
        BigInteger blockSize = lpDictService.getBlockSize(monitor.getChainId());

        while (true) {
//            BigInteger startBlock = new BigInteger("38568057");
//            BigInteger endBlock = new BigInteger("38568158");
            BigInteger startBlock = monitor.getLastBlockNumber().add(BigInteger.ONE);
            BigInteger endBlock = getBlockNumber(monitor.getChainId());

            if (endBlock.subtract(startBlock).compareTo(confirmBlockSize) <= 0) {
                break;
            } else if (endBlock.subtract(startBlock).compareTo(confirmBlockSize.add(blockSize)) < 0) {
                endBlock = endBlock.subtract(confirmBlockSize).add(BigInteger.ONE);
            } else {
                endBlock = startBlock.add(blockSize);
            }

            Monitor latestMonitor = getById(monitor.getId());
            if (MonitorStatusEnum.NO_MONITORING.getStatus().equals(latestMonitor.getStatus())) {
                return;
            }

            ethFilter = initFilter(Collections.singletonList(monitor.getMonitorAddr()),
                    DefaultBlockParameter.valueOf(startBlock),
                    DefaultBlockParameter.valueOf(endBlock));
            log.info("monitor for {} contract {} start {} to {}", monitor.getChainId(), monitor.getMonitorAddr(), startBlock, endBlock);
            monitorContract(monitor, ethFilter);
        }
    }

    private BigInteger getBlockNumber(Integer chainId) {
        try {
            return Web3jUtil.getWeb3j(chainId).ethBlockNumber().send().getBlockNumber();
        } catch (IOException e) {
            throw new BizException("getLatestBlockNumber fail");
        }
    }

    private EthFilter initFilter(List<String> addr, DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        // startBlock为闭区间，endBlock为开区间
        return new EthFilter(startBlock, endBlock, addr).addOptionalTopics(
                INCREASE_LIQUIDITY_TOPIC,
                DECREASE_LIQUIDITY_TOPIC,
                TRANSFER_TOPIC
        );
    }

    private void monitorContract(Monitor monitor, EthFilter filter) {
        Disposable disposable = null;
        disposable = Web3jUtil.getWeb3j(monitor.getChainId())
                .ethLogFlowable(filter)
                .subscribeOn(Schedulers.io())
                .timeout(10, TimeUnit.SECONDS, Flowable.empty()) // 如果30秒内没有新事件，触发超时
                .subscribe(contractLog -> {
                        // 处理LP余额变化
                        processPosition(monitor, contractLog);
                    }, error -> {
                        // 记录报错日志
                        log.error("Error occurred: " + error.getMessage());
                    }, () -> {
                        // 完成或取消订阅时调用
                        log.info("Subscription completed or canceled.");
                    });

        while (!disposable.isDisposed()) {
            try {
                log.info("Monitor【{}】运行中", monitor.getChainId() + "_" + monitor.getMonitorAddr());
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // 恢复中断状态
                throw new RuntimeException(e);
            }
        }
    }

    private void processPosition(Monitor monitor, Log contractLog) {
        String eventTopic = contractLog.getTopics().get(0);
        if (INCREASE_LIQUIDITY_TOPIC.equalsIgnoreCase(eventTopic)) {
            dealIncreaseEvent(monitor, contractLog);
        } else if (DECREASE_LIQUIDITY_TOPIC.equalsIgnoreCase(eventTopic)) {
            dealDecreaseEvent(monitor, contractLog);
        } else if (TRANSFER_TOPIC.equalsIgnoreCase(eventTopic)) {
            dealTransferEvent(monitor, contractLog);
        }

        monitor.setLastBlockNumber(contractLog.getBlockNumber());
        updateById(monitor);
    }

    private void dealIncreaseEvent(Monitor monitor, Log contractLog) {
        // 获取添加流动性后的tokenId
        BigInteger tokenId = hex2Dec(contractLog.getTopics().get(1));
        log.info("dealIncreaseLP tokenId = {}, blockNumber is {}, hash is {}", tokenId, contractLog.getBlockNumber(), contractLog.getTransactionHash());

        // 获取tokenId对应的基础信息
        Positions positions = getPositions(monitor, tokenId);
        if (positions == null) {
            return;
        }

        // 获取数据库已配置好的LpInfo
        LpInfo lpInfo = findLpByPositions(monitor, positions);
        if (lpInfo == null) {
            log.info("dealIncreaseLP tokenId = {} is not monitor lp", tokenId);
            return;
        }

        // 根据hash获取该交易，主要用于获取operatorAddress
        TransactionReceipt transactionReceipt = getTransByHash(monitor.getChainId(), contractLog.getTransactionHash());
        String fromAddr = transactionReceipt.getFrom();
        log.info("fromAddr is {}", fromAddr);

        // 获取数据库里operatorAddress对应的Lp仓位
        LpPosition lpPosition = lpPositionService.getOnePosition(monitor.getChainId(), fromAddr, tokenId);
        if (lpPosition == null) {
            lpPosition = LpPosition.init(fromAddr, monitor.getChainId(), monitor.getMonitorAddr(), lpInfo.getLpAddr(), tokenId);
        }

        // 解析data，获取amount0和amount1
        String data = contractLog.getData();
        if (data.startsWith("0x")) {
            data = data.substring(2);
        }
        log.info("data is {}", data);
        List<String> eventDataList = splitDataIntoChunks(data);
        BigDecimal amount0 = new BigDecimal(hex2Dec(eventDataList.get(1))).divide(new BigDecimal("10").pow(CURRENCY_DECIMAL), 18, RoundingMode.HALF_UP);
        BigDecimal amount1 = new BigDecimal(hex2Dec(eventDataList.get(2))).divide(new BigDecimal("10").pow(CURRENCY_DECIMAL), 18, RoundingMode.HALF_UP);
        log.info("amount0 is {}, amount1 is {}", amount0, amount1);

        // 更新数据库里operatorAddress对应的Lp仓位
        lpPosition.setToken0Amount(lpPosition.getToken0Amount().add(amount0));
        lpPosition.setToken1Amount(lpPosition.getToken1Amount().add(amount1));
        lpPositionService.saveOrUpdate(lpPosition);
        saveLpRecord(contractLog.getTransactionHash(), lpPosition, amount0, amount1, RecordTypeEnum.INCREASE_LP);
    }

    private void dealDecreaseEvent(Monitor monitor, Log contractLog) {
        // 获取添加流动性后的tokenId
        BigInteger tokenId = hex2Dec(contractLog.getTopics().get(1));
        log.info("dealDecreaseEvent tokenId = {}, blockNumber is {}, hash is {}", tokenId, contractLog.getBlockNumber(), contractLog.getTransactionHash());

        // 获取数据库已有仓位
        LpPosition lpPosition = findPositionByTokenId(monitor, tokenId);
        if (lpPosition == null) {
            log.info("dealDecreaseLP tokenId = {} is not monitor lp", tokenId);
            return;
        }

        // 根据hash获取该交易，主要用于获取operatorAddress
        TransactionReceipt transactionReceipt = getTransByHash(monitor.getChainId(), contractLog.getTransactionHash());
        String fromAddr = transactionReceipt.getFrom();

        if (!lpPosition.getUserAddr().equalsIgnoreCase(fromAddr)) {
            throw new BizException("owner of lpPosition is error, db is " + lpPosition.getUserAddr() + ", chain is " + fromAddr);
//            throw new BizException(fromAddr + " lpPosition is null, history scan missing data");
        }

        // 解析data，获取amount0和amount1
        String data = contractLog.getData();
        if (data.startsWith("0x")) {
            data = data.substring(2);
        }
        log.info("data is {}", data);
        List<String> eventDataList = splitDataIntoChunks(data);
        BigDecimal amount0 = new BigDecimal(hex2Dec(eventDataList.get(1))).divide(new BigDecimal("10").pow(CURRENCY_DECIMAL), 18, RoundingMode.HALF_UP);
        BigDecimal amount1 = new BigDecimal(hex2Dec(eventDataList.get(2))).divide(new BigDecimal("10").pow(CURRENCY_DECIMAL), 18, RoundingMode.HALF_UP);
        log.info("amount0 is {}, amount1 is {}", amount0, amount1);

        // 移除流动性后fromAddr对应的仓位归零
        lpPosition.setToken0Amount(lpPosition.getToken0Amount().subtract(amount0));
        lpPosition.setToken1Amount(lpPosition.getToken1Amount().subtract(amount1));
        lpPositionService.updateById(lpPosition);
        saveLpRecord(contractLog.getTransactionHash(), lpPosition, BigDecimal.ZERO, BigDecimal.ZERO, RecordTypeEnum.DECREASE_LP);
    }

    private void dealTransferEvent(Monitor monitor, Log contractLog) {
        String transferFrom = contractLog.getTopics().get(1);
        transferFrom = "0x" + transferFrom.substring(26);
        String transferTo = contractLog.getTopics().get(2);
        transferTo = "0x" + transferTo.substring(26);

        BigInteger tokenId = hex2Dec(contractLog.getTopics().get(3));
        log.info("dealTransferEvent tokenId = {}, blockNumber is {}, hash is {}", tokenId, contractLog.getBlockNumber(), contractLog.getTransactionHash());
        if (isZeroAddr(transferFrom) || isZeroAddr(transferTo)) {
            log.info("dealTransferEvent tokenId is {} isZeroAddr, ignore", tokenId);
            return;
        }

        // 获取tokenId对应的基础信息
        Positions positions = getPositions(monitor, tokenId);
        if (positions == null) {
            return;
        }

        // 获取数据库已配置好的LpInfo
        LpInfo lpInfo = findLpByPositions(monitor, positions);
        if (lpInfo == null) {
            log.info("dealTransferLP tokenId = {} is not monitor lp", tokenId);
            return;
        }

        // 根据hash获取该交易，主要用于获取operatorAddress
        TransactionReceipt transactionReceipt = getTransByHash(monitor.getChainId(), contractLog.getTransactionHash());
        String fromAddr = transactionReceipt.getFrom();

        boolean isTransfer = !isNativeAddr(monitor.getChainId(), transferFrom) && !isNativeAddr(monitor.getChainId(), transferTo)
                && !isProxyAddr(monitor.getChainId(), transferFrom) && !isProxyAddr(monitor.getChainId(), transferTo);
        if (isTransfer) {
            // 处理普通转账
            dealTransferLP(contractLog, monitor, transferFrom, transferTo, tokenId, lpInfo);
            return;
        }

        if (isNativeAddr(monitor.getChainId(), transferFrom) || isNativeAddr(monitor.getChainId(), transferTo)) {
            dealStaked(contractLog, monitor, fromAddr, transferFrom, transferTo, tokenId);
        }
    }

    private void dealTransferLP(Log contractLog, Monitor monitor, String transferFrom, String transferTo, BigInteger tokenId, LpInfo lpInfo) {
        // 获取数据库里FromAddress对应的Lp仓位
        LpPosition lpPosition = lpPositionService.getOne(new LambdaQueryWrapper<LpPosition>()
                .eq(LpPosition::getUserAddr, transferFrom)
                .eq(LpPosition::getTokenId, tokenId));
        if (lpPosition == null) {
            throw new BizException(transferFrom + " lpPosition is null, history scan missing data");
        }

        BigDecimal token0Amount = lpPosition.getToken0Amount();
        BigDecimal token1Amount = lpPosition.getToken1Amount();

        // 转移后fromAddr对应的仓位归零
        lpPosition.setToken0Amount(BigDecimal.ZERO);
        lpPosition.setToken1Amount(BigDecimal.ZERO);
        lpPositionService.updateById(lpPosition);

        // 获取数据库里ToAddress对应的Lp仓位
        lpPosition = lpPositionService.getOne(new LambdaQueryWrapper<LpPosition>()
                .eq(LpPosition::getUserAddr, transferTo)
                .eq(LpPosition::getTokenId, tokenId));
        if (lpPosition == null) {
            lpPosition = LpPosition.init(transferTo, monitor.getChainId(), monitor.getMonitorAddr(), lpInfo.getLpAddr(), tokenId);
        }

        // 更新数据库里operatorAddress对应的Lp仓位
        lpPosition.setToken0Amount(lpPosition.getToken0Amount().add(token0Amount));
        lpPosition.setToken1Amount(lpPosition.getToken1Amount().add(token1Amount));
        lpPositionService.updateById(lpPosition);
        saveLpRecord(contractLog.getTransactionHash(), lpPosition, token0Amount, token1Amount, RecordTypeEnum.TRANSFER_LP);
    }

    private void dealStaked(Log contractLog, Monitor monitor, String fromAddr, String transferFrom, String transferTo, BigInteger tokenId) {
        // 获取数据库里FromAddress对应的Lp仓位
        LpPosition lpPosition = lpPositionService.getOne(new LambdaQueryWrapper<LpPosition>()
                .eq(LpPosition::getUserAddr, fromAddr)
                .eq(LpPosition::getTokenId, tokenId));
        if (lpPosition == null) {
            throw new BizException(fromAddr + " lpPosition is null, history scan missing data");
        }

        boolean isProxyStaked = isProxyAddr(monitor.getChainId(), transferFrom) || isProxyAddr(monitor.getChainId(), transferTo);
        // 质押修改状态
        Integer stakedStatus = isNativeAddr(monitor.getChainId(), transferFrom) ?
                StakedStatusEnum.UN_STAKED.getStatus() : isProxyStaked ?
                StakedStatusEnum.PROXY_STAKED.getStatus() : StakedStatusEnum.NATIVE_STAKED.getStatus();
        lpPosition.setStakedStatus(stakedStatus);

        // 记录代理质押和原生质押的合约地址
        if (isNativeAddr(monitor.getChainId(), transferFrom)) {
            if (isProxyStaked) {
                lpPosition.setStakedProxyAddr(null);
            }
            lpPosition.setStakedAddr(null);
            lpPositionService.updateById(lpPosition);
            saveLpRecord(contractLog.getTransactionHash(), lpPosition, lpPosition.getToken0Amount(), lpPosition.getToken1Amount(), RecordTypeEnum.UNSTAKED_LP);
        } else if (isNativeAddr(monitor.getChainId(), transferTo)) {
            if (isProxyStaked) {
                lpPosition.setStakedProxyAddr(transferFrom);
            }
            lpPosition.setStakedAddr(transferTo);
            lpPositionService.updateById(lpPosition);
            saveLpRecord(contractLog.getTransactionHash(), lpPosition, lpPosition.getToken0Amount(), lpPosition.getToken1Amount(), RecordTypeEnum.STAKED_LP);
        }

    }

    private Positions getPositions(Monitor monitor, BigInteger tokenId) {
        //        uint96 nonce,
        //        address operator,
        //        address token0,
        //        address token1,
        //        uint24 fee,
        //        int24 tickLower,
        //        int24 tickUpper,
        //        uint128 liquidity,
        //        uint256 feeGrowthInside0LastX128,
        //        uint256 feeGrowthInside1LastX128,
        //        uint128 tokensOwed0,
        //        uint128 tokensOwed1
        Function positionsFunc = new Function(
                "positions", // 函数名
                Collections.singletonList(new Uint256(tokenId)), // 输入参数
                Arrays.asList( // 输出参数类型
                        new TypeReference<Uint96>() {},
                        new TypeReference<Address>() {},
                        new TypeReference<Address>() {},
                        new TypeReference<Address>() {},
                        new TypeReference<Uint24>() {},
                        new TypeReference<Int24>() {},
                        new TypeReference<Int24>() {},
                        new TypeReference<Uint128>() {},
                        new TypeReference<Uint256>() {},
                        new TypeReference<Uint256>() {},
                        new TypeReference<Uint128>() {},
                        new TypeReference<Uint128>() {}
                ));

        // 调用合约方法
        try {
            String encodedFunction = FunctionEncoder.encode(positionsFunc);
            EthCall ethCall = Web3jUtil.getWeb3j(monitor.getChainId()).ethCall(
                    Transaction.createEthCallTransaction(lpDictService.getOperatorAddr(), monitor.getMonitorAddr(), encodedFunction),
                    DefaultBlockParameterName.LATEST
            ).send();

            if (ethCall.getError() != null && ethCall.getError().getMessage().contains("Invalid token ID")) {
                log.info("tokenId {} has been burned", tokenId);
                return null;
            }

            List<Type> results = FunctionReturnDecoder.decode(ethCall.getValue(), positionsFunc.getOutputParameters());
            String token0Addr = (String) results.get(2).getValue();
            String token1Addr = (String) results.get(3).getValue();
            BigInteger fee = (BigInteger) results.get(4).getValue();

            return new Positions(tokenId, token0Addr, token1Addr, fee);
        } catch (Exception e) {
            log.error("getPositions {} {} fail\n", monitor.getChainId(), tokenId, e);
            throw new BizException("getPositions fail");
        }
    }

    private LpInfo findLpByPositions(Monitor monitor, Positions positions) {
        List<LpInfo> lpInfoList = lpInfoService.list(new LambdaQueryWrapper<LpInfo>().eq(LpInfo::getChainId, monitor.getChainId()));
        Optional<LpInfo> lpInfoOp = lpInfoList.stream()
                .filter(e -> e.getToken0Addr().equalsIgnoreCase(positions.getToken0Addr())
                        && e.getToken1Addr().equalsIgnoreCase(positions.getToken1Addr())
                        && e.getFee().equals(positions.getFee()))
                .findFirst();
        return lpInfoOp.orElse(null);
    }

    private LpPosition findPositionByTokenId(Monitor monitor, BigInteger tokenId) {
        return lpPositionService.getOne(new LambdaQueryWrapper<LpPosition>()
                .eq(LpPosition::getChainId, monitor.getChainId())
                .eq(LpPosition::getMonitorAddr, monitor.getMonitorAddr())
                .eq(LpPosition::getTokenId, tokenId));
    }

    private TransactionReceipt getTransByHash(Integer chainId, String txHash) {
        try {
            return Web3jUtil.getWeb3j(chainId).ethGetTransactionReceipt(txHash).send().getResult();
        } catch (Exception e) {
            throw new BizException("Hash {} can't get trans", txHash);
        }
    }

    private BigInteger hex2Dec(String hexNum) {
        if (hexNum.startsWith("0x")) {
            hexNum = hexNum.substring(2);
        }
        return new BigInteger(hexNum, 16);
    }

    private List<String> splitDataIntoChunks(String data) {
        List<String> chunks = new ArrayList<>();
        int length = data.length();
        int numChunks = (int) Math.ceil((double) length / 64);

        for (int i = 0; i < numChunks; i++) {
            int start = i * 64;
            int end = Math.min(start + 64, length);
            String chunk = data.substring(start, end);
            chunks.add(chunk);
        }

        return chunks;
    }

    public void saveLpRecord(String txHash, LpPosition lpPosition, BigDecimal amount0, BigDecimal amount1, RecordTypeEnum recordTypeEnum) {
        LpRecord lpRecord = new LpRecord();
        BeanUtils.copyProperties(lpPosition, lpRecord);
        lpRecord.setId(null);
        lpRecord.setTxHash(txHash);
        lpRecord.setType(recordTypeEnum.getType());
        lpRecord.setToken0Amount(amount0);
        lpRecord.setToken1Amount(amount1);
        lpRecordService.save(lpRecord);
    }

    public Boolean isProxyAddr(Integer chainId, String address) {
        return address.equalsIgnoreCase(lpDictService.getCakePieStaked(chainId));
    }

    public Boolean isNativeAddr(Integer chainId, String address) {
        return address.equalsIgnoreCase(lpDictService.getPancakeNativeStaked(chainId));
    }

    public Boolean isZeroAddr(String address) {
        return address.equalsIgnoreCase(lpDictService.getZeroAddr());
    }

}

