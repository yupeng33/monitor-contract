package com.monitor.contract.job;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.monitor.contract.mapper.MonitorMapper;
import com.monitor.contract.model.entity.Monitor;
import com.monitor.contract.common.enums.MonitorStatusEnum;
import com.monitor.contract.service.MonitorService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @program: monitor-contract
 * @description:
 * @author: WEIPENG
 * @create: 2022-05-18 23:31
 **/
@Slf4j
@Component
public class MonitorHandle {

    @Resource
    private MonitorService monitorService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Async
    @Scheduled(cron = "0/10 * * * * ?")
    public void monitor() {
        List<Monitor> monitorList = monitorService.list(new LambdaQueryWrapper<Monitor>().eq(Monitor::getStatus, MonitorStatusEnum.UNMONITOR.getStatus()));
        monitorList.parallelStream().forEach(monitor -> {
            String key = monitor.getChainId() + "_" + monitor.getMonitorAddr();
            if (!lock(key)) {
                return;
            }

            try {
                log.info("Monitor 【{}】 开始执行", key);
                monitor.setStatus(MonitorStatusEnum.MONITORING.getStatus());
                monitorService.updateById(monitor);

                monitorService.monitorOfStep(monitor);
            } catch (Exception ex) {
                log.error("Monitor【{}】 执行报错，报错原因如下：", key, ex);
                monitor.setStatus(MonitorStatusEnum.UNMONITOR.getStatus());
                monitorService.updateById(monitor);
            } finally {
                log.info("Monitor 【{}】 执行结束", key);
                monitor.setStatus(MonitorStatusEnum.UNMONITOR.getStatus());
                monitorService.updateById(monitor);
                unLock(key);
            }
        });
    }

    public Boolean lock(String lockKey) {
        String lockValue = stringRedisTemplate.opsForValue().get(lockKey);
        if ("1".equals(lockValue)) {
            log.info("Monitor【{}】 正在执行", lockKey);
            return false;
        }
        stringRedisTemplate.opsForValue().set(lockKey, "1");
        return true;
    }

    public void unLock(String lockKey) {
        stringRedisTemplate.opsForValue().set(lockKey, "0");
    }

}
