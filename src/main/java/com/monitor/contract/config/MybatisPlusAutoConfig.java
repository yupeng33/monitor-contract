
package com.monitor.contract.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@MapperScan("com.monitor.contract.dao.mapper")
@EnableTransactionManagement
public class MybatisPlusAutoConfig {
}
