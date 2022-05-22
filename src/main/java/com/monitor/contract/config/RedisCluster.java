package com.monitor.contract.config;

import lombok.Data;

import java.util.List;

/**
 * redis集群信息
 * 
 * @author wangjupeng06199
 * @date 2022年1月5日
 */
@Data
public class RedisCluster {
	/**
	 * 节点列表
	 */
	private List<String> nodes;
}
