package com.monitor.contract.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * redission配置属性
 * 
 * @author wangjupeng06199
 * @date 2022年1月5日
 */
@ConfigurationProperties(prefix = "spring.redis")
@Getter
@Setter
public class RedissionConfigProperties {
	/**
	 * 库序列
	 */
	private Integer database;
	/**
	 * 密码
	 */
	private String password;
	/**
	 * 集群信息
	 */
	private RedisCluster cluster;

}
