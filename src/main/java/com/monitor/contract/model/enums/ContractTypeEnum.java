package com.monitor.contract.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ContractTypeEnum {
    // 合约类型
    FACTORY(0),
    ORDER(1);

    private Integer type;
}
