package com.monitor.contract.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.monitor.contract.common.enums.StakedStatusEnum;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;

/**
 * <p>
 * 
 * </p>
 *
 * @author peng
 * @since 2024-07-26
 */
@Getter
@Setter
@TableName("lp_position")
public class LpPosition implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Integer chainId;

    private String userAddr;

    private String monitorAddr;

    private String lpAddr;

    private BigInteger tokenId;

    private BigDecimal token0Amount;

    private BigDecimal token1Amount;

    private Integer stakedStatus;

    private String stakedAddr;

    private String stakedProxyAddr;

    private Instant createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Instant modifyTime;

    public static LpPosition init(String userAddr, Integer chainId, String monitorAddr, String lpAddr, BigInteger tokenId) {
        LpPosition lpPosition = new LpPosition();
        lpPosition.setUserAddr(userAddr);
        lpPosition.setChainId(chainId);
        lpPosition.setMonitorAddr(monitorAddr);
        lpPosition.setLpAddr(lpAddr);
        lpPosition.setTokenId(tokenId);
        lpPosition.setToken0Amount(BigDecimal.ZERO);
        lpPosition.setToken1Amount(BigDecimal.ZERO);
        lpPosition.setStakedStatus(StakedStatusEnum.UN_STAKED.getStatus());
        return lpPosition;
    }


}
