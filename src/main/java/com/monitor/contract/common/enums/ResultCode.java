package com.monitor.contract.common.enums;


import com.monitor.contract.common.IResultCode;

/**
 * @Description: 返回状态枚举类
 * @Author Created by yan.x on 2020-01-08 .
 **/
public enum ResultCode implements IResultCode {

    SUCCESS(200, "Success"),
    FAILURE(500, "Fail"),
    SC_FAILURE(500, "Server call fail"),

    // 用户模块
    INVITE_CODE_NOT_EXIST(30001, "Invite code not exist"),
    CANNOT_BIND_SELF(30002, "Can't bind self"),
    CANNOT_BIND_CHILD(30003, "Can't bind child"),
    DEVICE_NUMBER_ERROR(30004, "Device number is error"),
    INVALID_AREA(30005, "Invalid area"),
    DISABLE_LOGIN(30006, "Disable login"),
    INVALID_PRIVATE_KEY(30007, "Invalid privateKey"),
    INVALID_PASSWORD(30008, "Invalid password"),
    INVALID_GOOGLE_CODE(30009, "Invalid googleCode"),
    PRIVATE_KEY_EXIST(30010, "PrivateKey is exist"),
    INVALID_CAPTCHA(30011, "Invalid captcha"),
    ILLEGAL_DATA(30012, "Illegal requests for data"),
    DATA_EXPIRED(30013, "Data expired"),

    // 钱包模块
    WITHDRAW_BALANCE_NOT_ENOUGH(31001, "balance is not enough"),
    CONVERT_BALANCE_NOT_ENOUGH(31002, "balance is not enough"),
    DEFI_BALANCE_NOT_ENOUGH(31003, "balance is not enough"),
    CONVERT_AMOUNT_LESS_LIMIT(31004, "The convert amount is less than the limit amount"),
    ;


    private final int code;
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
