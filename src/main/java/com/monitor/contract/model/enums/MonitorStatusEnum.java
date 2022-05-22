package com.monitor.contract.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MonitorStatusEnum {
    // 监听器状态：0-未监听；1-监听中；2-监听结束
    UNMONITOR(0),
    MONITORING(1),
    FINISHMONITOR(2);

    private Integer status;
}
