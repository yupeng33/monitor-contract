package com.monitor.contract.util;

import lombok.Data;

@Data
public class TelegramBotReq {
    private String chatId;
    private String text;
}
