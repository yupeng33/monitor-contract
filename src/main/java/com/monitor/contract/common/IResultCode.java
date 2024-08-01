package com.monitor.contract.common;


import java.io.Serializable;

/**
 * @Author Created by yan.x on 2019-05-03 .
 **/
public interface IResultCode extends Serializable {
    /**
     * 状态码
     *
     * @return
     */
    int getCode();

    /**
     * 信息
     *
     * @return
     */
    String getMessage();
}
