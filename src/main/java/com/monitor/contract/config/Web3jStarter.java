package com.monitor.contract.config;

import com.monitor.contract.util.Web3jUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Locale;

@Slf4j
public class Web3jStarter implements InitializingBean {

    @Autowired
    Web3jConfig web3jConfig;

    @Override
    public void afterPropertiesSet() {
        if (web3jConfig == null || web3jConfig.chains == null) {
            log.error("web3j is not configured.");
            return;
        }

        web3jConfig.chains.forEach(Web3jUtil::addWeb3j);
    }
}
