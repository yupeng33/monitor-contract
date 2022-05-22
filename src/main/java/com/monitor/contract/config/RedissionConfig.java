package com.monitor.contract.config;

import com.alibaba.fastjson.JSON;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.List;

/**
 * redission配置
 * 
 * @author wangjupeng06199
 * @date 2022年1月5日
 */
@Configuration
@EnableConfigurationProperties(RedissionConfigProperties.class)
public class RedissionConfig {
	/**
	 * 日志
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(RedissionConfig.class);
	/**
	 * redis配置属性
	 */
	@Resource
	private RedissionConfigProperties configProperties;
	/**
	 * redis配置前缀
	 */
	private static final String REDISSON_PREFIX = "redis://";

	/**
	 * redission客户端
	 * 
	 * @return
	 */
	@Bean
	public RedissonClient redissonClient() {
		Config config = new Config();
		List<String> redissonNodes = configProperties.getCluster().getNodes();
		String[] urls = new String[redissonNodes.size()];
		for (int i = 0; i < redissonNodes.size(); i++) {
			urls[i] = REDISSON_PREFIX + redissonNodes.get(i);
		}
		String password = configProperties.getPassword();
		config.useClusterServers().addNodeAddress(urls).setPassword(password).setConnectTimeout(60000)
				.setPingConnectionInterval(600000).setTimeout(60000);
		String urlStr = JSON.toJSONString(urls);
		try {

			LOGGER.info("RedissonClient init redis url:[{}] successed.", urlStr);
			return Redisson.create(config);
		} catch (Exception e) {
			String errorMsg = String.format("RedissonClient init redis url:[{}], Exception:", urlStr);
			LOGGER.error(errorMsg, e);
			return null;
		}
	}
}
