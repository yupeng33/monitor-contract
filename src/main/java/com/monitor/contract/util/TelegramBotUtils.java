package com.monitor.contract.util;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;

public class TelegramBotUtils {

    private static final String url = "https://api.telegram.org/bot7397741686:AAHpBd4IlgC2NPK-9jYuoA35diKwRxY7EOs/sendMessage";
    private static final String chainId = "1843722292";


    // curl -X POST "https://api.telegram.org/bot7157177257:AAE1Io8AAzJcXM5kRGFWkiSPoM37EQLUWAQ/sendMessage"
    // -d "chat_id=-1002210989943&text=my sample text"
    public static void sendMsg(String msg) {
        TelegramBotReq req = new TelegramBotReq();
        req.setChatId(chainId);
        req.setText(msg);
        HttpUtil.post(url, JSON.toJSONString(req));
    }


}
