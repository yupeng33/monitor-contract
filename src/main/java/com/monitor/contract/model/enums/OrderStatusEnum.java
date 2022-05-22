package com.monitor.contract.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public enum OrderStatusEnum {
    // 订单状态
    // TODO: 需要项目方补充
    CANCLE(2),
    WITHDRAW(3);

    private Integer status;

    public static List<Integer> getEndStatus() {
        List<Integer> endStatus = new ArrayList<>();
        for (OrderStatusEnum value : values()) {
            endStatus.add(value.getStatus());
        }
        return endStatus;
    }
}
