package com.monitor.contract.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StakedStatusEnum {
    // 质押状态：0-未质押；1-原生质押；2-代理质押
    UN_STAKED(0),
    NATIVE_STAKED(1),
    PROXY_STAKED(2);

    private final Integer status;
}
