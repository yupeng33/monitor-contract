package com.monitor.contract.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

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
@TableName("lp_record")
public class LpRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String txHash;
    private Integer chainId;

    private String userAddr;

    private String monitorAddr;

    private String lpAddr;

    private BigInteger tokenId;

    private BigDecimal token0Amount;

    private BigDecimal token1Amount;

    /**
     * lp变动类型：0-添加流动性；1-移除流动性；2-质押LP；3-取消质押LP
     */
    private Integer type;

    private Instant createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Instant modifyTime;


}
