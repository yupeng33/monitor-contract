package com.monitor.contract.common.exception;


import com.monitor.contract.common.IResultCode;
import com.monitor.contract.common.enums.ResultCode;

/**
 * @Description: 公共异常类
 * @Author Created by yan.x on 2019-07-27 .
 **/
public class BizException extends AbstractException {

    private static final IResultCode resultCode = ResultCode.FAILURE;

    public BizException() {
        super(resultCode);
    }

    public BizException(String message) {
        super(resultCode, message);
    }

    public BizException(Throwable cause) {
        super(cause);
    }

    public BizException(IResultCode resultCode) {
        super(resultCode.getCode(), resultCode.getMessage());
    }

    public BizException(IResultCode resultCode, Throwable cause) {
        super(resultCode, cause);
    }

    public BizException(String message, Throwable cause) {
        super(cause, message);
    }

    public BizException(Integer code, String message) {
        super(code, message);
    }

    public BizException(Integer code, String message, Throwable cause) {
        super(code, message, cause);
    }

    public BizException(Integer code, String pattern, Object... params) {
        super(code, pattern, params);
    }

    public BizException(IResultCode resultCode, Object... params) {
        super(resultCode, params);
    }

    public BizException(String pattern, Object... params) {
        super(pattern, params);
    }

    public BizException(Throwable cause, String pattern, Object... params) {
        super(cause, pattern, params);
    }

    public BizException(Throwable cause, IResultCode resultCode, Object... params) {
        super(cause, resultCode, params);
    }
}
