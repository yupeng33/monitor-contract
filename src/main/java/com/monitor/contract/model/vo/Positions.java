package com.monitor.contract.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Positions {
    private BigInteger tokenId;
    private String token0Addr;
    private String token1Addr;
    private BigInteger fee;

    public Positions(BigInteger tokenId) {
        this.tokenId = tokenId;
    }
}
