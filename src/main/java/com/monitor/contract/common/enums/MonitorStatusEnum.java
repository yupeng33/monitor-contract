package com.monitor.contract.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MonitorStatusEnum {
    // 监听器状态：0-未监听；1-监听中；2-禁止监听
    UNMONITOR(0),
    MONITORING(1),
    NO_MONITORING(2);

    private final Integer status;
}
