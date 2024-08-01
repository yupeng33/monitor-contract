package com.monitor.contract.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * lp信息表
 * </p>
 *
 * @author peng
 * @since 2024-07-26
 */
@Getter
@Setter
@TableName("lp_info")
public class LpInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Integer chainId;

    private String lpAddr;

    private String token0Addr;

    private String token1Addr;

    private BigInteger fee;

    private Long startBlockNumber;

    private Instant createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Instant modifyTime;


}
