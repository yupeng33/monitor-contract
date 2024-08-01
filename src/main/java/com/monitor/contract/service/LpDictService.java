package com.monitor.contract.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.monitor.contract.mapper.LpDictMapper;
import com.monitor.contract.model.entity.LpDict;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author peng
 * @since 2024-07-28
 */
@Service
public class LpDictService extends ServiceImpl<LpDictMapper, LpDict> {

//    public List<BigDecimal> getAllocationRebate() {
//        return Arrays.stream(getOne(new LambdaQueryWrapper<LpDict>()
//                        .eq(LpDict::getName, "allocation_rebate")).getValue()
//                        .split(",")).map(BigDecimal::new)
//                .collect(Collectors.toList());
//    }

    public String getOperatorAddr() {
        return getOne(new LambdaQueryWrapper<LpDict>().eq(LpDict::getName, "operator_addr")).getValue();
    }

    public String getCakePieStaked(Integer chainId) {
        return getOne(new LambdaQueryWrapper<LpDict>().eq(LpDict::getName, "cake_pie_staked").eq(LpDict::getChainId, chainId)).getValue();
    }

    public String getPancakeNativeStaked(Integer chainId) {
        return getOne(new LambdaQueryWrapper<LpDict>().eq(LpDict::getName, "pancake_native_staked").eq(LpDict::getChainId, chainId)).getValue();
    }

    public String getZeroAddr() {
        return getOne(new LambdaQueryWrapper<LpDict>().eq(LpDict::getName, "zero_addr")).getValue();
    }

    public BigInteger getConfirmBlockSize(Integer chainId) {
        return new BigInteger(getOne(new LambdaQueryWrapper<LpDict>().eq(LpDict::getName, "confirm_block_size").eq(LpDict::getChainId, chainId)).getValue());
    }

    public BigInteger getBlockSize(Integer chainId) {
        return new BigInteger(getOne(new LambdaQueryWrapper<LpDict>().eq(LpDict::getName, "block_size").eq(LpDict::getChainId, chainId)).getValue());
    }
}
