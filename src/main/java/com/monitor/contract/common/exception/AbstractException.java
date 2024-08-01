package com.monitor.contract.common.exception;





import com.monitor.contract.common.IResultCode;
import com.monitor.contract.common.enums.ResultCode;

import java.text.MessageFormat;


/**
 * @Description: 基础异常类，不可被实例化
 * @Author Created by yan.x on 2019-05-27 .
 **/
public abstract class AbstractException extends RuntimeException {
    private static final long serialVersionUID = 8247610319171014183L;

    private Integer code = ResultCode.FAILURE.getCode();

    public AbstractException() {
        super(ResultCode.FAILURE.getMessage());
    }

    public AbstractException(String message) {
        super(message);
    }

    public AbstractException(Throwable cause) {
        super(cause);
    }

    public AbstractException(IResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
    }

    public AbstractException(IResultCode resultCode, Throwable cause) {
        super(resultCode.getMessage(), cause);
        this.code = resultCode.getCode();
    }

    public AbstractException(IResultCode resultCode, String message) {
        super(message);
        this.code = resultCode.getCode();
    }

    public AbstractException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public AbstractException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public AbstractException(String pattern, Object... params) {
        super(MessageFormat.format(pattern, params));
    }

    public AbstractException(IResultCode resultCode, Object... params) {
        super(MessageFormat.format(resultCode.getMessage(), params));
        this.code = resultCode.getCode();
    }

    public AbstractException(Throwable cause, String pattern, Object... params) {
        super(MessageFormat.format(pattern, params), cause);
    }

    public AbstractException(Throwable cause, IResultCode resultCode, Object... params) {
        super(MessageFormat.format(resultCode.getMessage(), params), cause);
        this.code = resultCode.getCode();
    }

    public AbstractException(Integer code, String pattern, Object... params) {
        super(MessageFormat.format(pattern, params));
        this.code = code;
    }

    public AbstractException(Integer code, Throwable cause, String pattern, Object... params) {
        super(MessageFormat.format(pattern, params), cause);
        this.code = code;
    }

    public Integer getCode() {
        return this.code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}