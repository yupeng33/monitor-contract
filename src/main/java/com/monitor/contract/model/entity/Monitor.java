package com.monitor.contract.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

/**
 * @program: monitor-contract
 * @description:
 * @author: WEIPENG
 * @create: 2022-05-17 13:15
 **/
@Data
@NoArgsConstructor
@TableName("monitor")
public class Monitor {
    @TableId("id")
    private Long id;
    @TableField("addr")
    private String addr;
    @TableField("chain")
    private String chain;
    @TableField("start_block_number")
    private BigInteger startBlockNumber;
    @TableField("last_block_number")
    private BigInteger lastBlockNumber;
    @TableField("end_block_number")
    private BigInteger endBlockNumber;
    @TableField("end_tx_hash")
    private String endTxHash;
    @TableField("status")
    private Integer status;
    @TableField("type")
    private Integer type;

    public Monitor(String chain, String addr, BigInteger startBlockNumber, BigInteger lastBlockNumber) {
        this.chain = chain;
        this.addr = addr;
        this.startBlockNumber = startBlockNumber;
        this.lastBlockNumber = lastBlockNumber;
    }
}
