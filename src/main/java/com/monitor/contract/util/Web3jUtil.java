package com.monitor.contract.util;

import lombok.extern.slf4j.Slf4j;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import java.net.Proxy;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
public class Web3jUtil {

    public static Map<String, Web3j> chainMap = new HashMap<>();

    public static void addWeb3j(String chain, String url) {
        HttpService httpClient = new HttpService(url, okHttpClient, false);
        chainMap.put(chain.toUpperCase(), Web3j.build(httpClient));
    }

    public static Web3j getWeb3j(String chain) {
        return chainMap.get(chain.toUpperCase(Locale.ROOT));
    }

    public static void getAllWeb3j() {
        chainMap.entrySet().stream().forEach(e -> {
            log.info("chian = {}, web3j = {}", e.getKey(), e.getValue());
        });
    }

    private static OkHttpClient.Builder builder = new OkHttpClient.Builder()
            .retryOnConnectionFailure(true)
            //连接超时
            .connectTimeout(60, TimeUnit.SECONDS)
            //读取超时
            .readTimeout(90, TimeUnit.SECONDS)
            //写超时
            .writeTimeout(60, TimeUnit.SECONDS);

    private static OkHttpClient okHttpClient = new OkHttpClient(builder);

}
