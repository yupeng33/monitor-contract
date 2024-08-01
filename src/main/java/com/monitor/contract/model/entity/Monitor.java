package com.monitor.contract.model.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.time.Instant;

/**
 * @program: monitor-contract
 * @description:
 * @author: WEIPENG
 * @create: 2022-05-17 13:15
 **/
@Data
@TableName("monitor")
public class Monitor {
    @TableId("id")
    private Long id;
    private String monitorAddr;
    private Integer chainId;
    private BigInteger lastBlockNumber;
    private Integer status;
    private Instant createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Instant modifyTime;


}
