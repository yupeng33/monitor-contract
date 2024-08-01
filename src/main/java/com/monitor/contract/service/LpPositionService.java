package com.monitor.contract.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.monitor.contract.model.entity.LpPosition;
import com.monitor.contract.mapper.LpPositionMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author peng
 * @since 2024-07-26
 */
@Service
public class LpPositionService extends ServiceImpl<LpPositionMapper, LpPosition> {

    public LpPosition getOnePosition(Integer chainId, String fromAddr, BigInteger tokenId) {
        return getOne(new LambdaQueryWrapper<LpPosition>()
                .eq(LpPosition::getChainId, chainId)
                .eq(LpPosition::getUserAddr, fromAddr)
                .eq(LpPosition::getTokenId, tokenId));
    }
}
