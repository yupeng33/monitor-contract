package com.monitor.contract.config;

import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({Web3jConfig.class})
@AutoConfigureOrder(Integer.MAX_VALUE)
public class Web3jAutoConfiguration {

    @Bean
    public Web3jStarter web3jStarter() {
        return new Web3jStarter();
    }
}
