package com.monitor.contract.service;

import com.monitor.contract.dao.mapper.MonitorMapper;
import com.monitor.contract.model.entity.Monitor;
import com.monitor.contract.model.enums.ContractTypeEnum;
import com.monitor.contract.model.enums.MonitorStatusEnum;
import com.monitor.contract.model.enums.OrderStatusEnum;
import com.monitor.contract.util.Web3jUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.EthBlockNumber;
import rx.Subscription;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @program: hoogle-crawler-java
 * @description:
 * @author: WEIPENG
 * @create: 2022-05-16 10:28
 **/
@Slf4j
@Service
public class MonitorService {

    @Resource
    MonitorMapper monitorMapper;

    public void monitorOfStep(Monitor monitor) {
        BigInteger endBlock = getBlockNumber(monitor.getChain());
        BigInteger startBlock = monitor.getLastBlockNumber();
        BigInteger step = new BigInteger("100");

        assert endBlock != null;
        int num = Integer.parseInt(String.valueOf(endBlock.subtract(startBlock).divide(step)));
        EthFilter ethFilter;
        boolean isFactory = ContractTypeEnum.FACTORY.getType().equals(monitor.getType());

        for (int i = 0; i < num; i++) {
            ethFilter = initFilter(monitor.getAddr(),
                    DefaultBlockParameter.valueOf(startBlock),
                    DefaultBlockParameter.valueOf(startBlock.add(step)),
                    isFactory);
            log.info("monitor for contract {} start {} to {}", monitor.getAddr(), startBlock, startBlock.add(step));
            monitorContract(monitor.getAddr(), isFactory, monitor.getChain(), ethFilter);

            startBlock = startBlock.add(step);
            Monitor latestMonitor = monitorMapper.selectById(monitor.getId());
            if (MonitorStatusEnum.FINISHMONITOR.getStatus().equals(latestMonitor.getStatus())) {
                return;
            }
            monitorMapper.updateLastByAddr(startBlock, monitor.getAddr());
        }
    }

    private BigInteger getBlockNumber(String chain) {
        EthBlockNumber send;
        try {
            send = Web3jUtil.getWeb3j(chain.toUpperCase()).ethBlockNumber().send();
            return send.getBlockNumber();
        } catch (IOException e) {
            log.warn("请求区块链信息异常 >> 区块数量", e);
        }
        return null;
    }

    private EthFilter initFilter(String addr, DefaultBlockParameter startBlock, DefaultBlockParameter endBlock, Boolean isFactory) {
        Event event = isFactory ?
                new Event("createGuaranteeContract", Arrays.asList(
                        new TypeReference<Address>() {},
                        new TypeReference<Address>() {},
                        new TypeReference<Address>() {},
                        new TypeReference<Address>() {},
                        new TypeReference<Uint256>() {}),
                        Collections.emptyList()) :
                new Event("stateChange", Arrays.asList(
                        new TypeReference<Address>() {},
                        new TypeReference<Uint8>() {}),
                        Collections.emptyList());

        return new EthFilter(startBlock, endBlock, addr).addOptionalTopics(EventEncoder.encode(event));
    }

    private void monitorContract(String addr, Boolean isFactory, String chain, EthFilter filter) {
        AtomicReference<Integer> status = new AtomicReference<>(999);
        Web3j web3j = Web3jUtil.getWeb3j(chain.toUpperCase());
        Subscription subscribe = web3j.ethLogObservable(filter).subscribe(contractLog -> {

            BigInteger blockNumber = contractLog.getBlockNumber();
            String txHash = contractLog.getTransactionHash();
            String data = contractLog.getData();

            if (Boolean.TRUE.equals(isFactory)) {
                String orderAddress = "0x" + data.substring(26, 66);
                monitorMapper.insert(new Monitor(chain, orderAddress, blockNumber, blockNumber));
                log.info("createOrder = {}, blockNumber = {}", orderAddress, blockNumber);
            } else {
                if (data.startsWith("0x")) {
                    data = data.substring(2);
                }
                status.set(Integer.valueOf(decodeHex(data.substring(64))));
                log.info("orderAddress = {}, status = {}, blockNumber = {}", addr, status, blockNumber);

                // TODO: doSomeThing()
                if (OrderStatusEnum.getEndStatus().contains(status.get())) {
                    monitorMapper.updateEndByAddr(blockNumber, addr, txHash);
                    log.info("Monitor for order 【{}】 结束", addr);
                }
            }
        });
        subscribe.unsubscribe();
    }

    private String decodeHex(String hexNum) {
        if (!hexNum.startsWith("0x")) {
            return hexNum;
        }

        int idx = 0;
        for (int i = 0; i < hexNum.length(); i++) {
            char c = hexNum.charAt(i);
            if (c != '0' && c != 'x') {
                idx = i;
                break;
            }
        }
        Long num = Long.parseLong(hexNum.substring(idx), 16);
        return num.toString();
    }

}

