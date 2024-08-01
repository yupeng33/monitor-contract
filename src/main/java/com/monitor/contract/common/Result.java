package com.monitor.contract.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.monitor.contract.common.enums.ResultCode;
import com.monitor.contract.common.exception.BizException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Description: 返回对象
 * @Author Created by yan.x on 2019-06-18 .
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("code")
    private int code;

    @JsonProperty("message")
    private String message;

    @JsonProperty("data")
    private T data;

    public static <T> Result<T> success(T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
    }

    public static Result error(BizException e) {
        return new Result<>(e.getCode(), e.getMessage(), null);
    }
}
