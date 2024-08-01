package com.monitor.contract.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RecordTypeEnum {
    // lp变动类型：0-添加流动性；1-移除流动性；2-质押LP；3-取消质押LP；4-普通转账
    INCREASE_LP(0),
    DECREASE_LP(1),
    STAKED_LP(2),
    UNSTAKED_LP(3),
    TRANSFER_LP(4),
    ;

    private final Integer type;
}
