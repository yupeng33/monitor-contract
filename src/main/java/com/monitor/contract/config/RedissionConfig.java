package com.monitor.contract.config;

import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * redission配置
 *
 * @author wangjupeng06199
 * @date 2022年1月5日
 */
@Slf4j
@Configuration
public class RedissionConfig {
	/**
	 * 日志
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(RedissionConfig.class);
	/**
	 * redis配置前缀
	 */
	private static final String REDISSON_PREFIX = "redis://";

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    @Value("${spring.redis.database}")
    private int database;

	/**
	 * redission客户端
	 *
	 * @return
	 */
	@Bean
	public RedissonClient redissonClient() {
		Config config = new Config();
        config.useSingleServer().setAddress(REDISSON_PREFIX + host + ":" + port).setDatabase(database);
		try {
            RedissonClient redissonClient = Redisson.create(config);
            log.info("Init redissonClient success");
            return redissonClient;
        } catch (Exception e) {
			LOGGER.error("RedissonClient init redis error, Exception:\n", e);
			return null;
		}
	}

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
        stringRedisTemplate.setConnectionFactory(connectionFactory);
        return stringRedisTemplate;
    }
}
