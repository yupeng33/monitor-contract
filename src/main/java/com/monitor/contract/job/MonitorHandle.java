package com.monitor.contract.job;

import com.monitor.contract.dao.mapper.MonitorMapper;
import com.monitor.contract.model.entity.Monitor;
import com.monitor.contract.model.enums.ContractTypeEnum;
import com.monitor.contract.model.enums.MonitorStatusEnum;
import com.monitor.contract.service.MonitorService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
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
    private MonitorMapper monitorMapper;
    @Resource
    private MonitorService monitorService;
    @Resource
    private RedissonClient redissonClient;

    @Async
    @Scheduled(cron = "0/10 * * * * ?")
    public void monitor() {
        List<Monitor> monitorList = monitorMapper.selectUnEndMonitor();
        monitorList.parallelStream().forEach(e -> {
            String key = e.getChain().toUpperCase() + e.getAddr();
            if (!tryFairLock(key)) {
                log.info("Monitor【{}】 正在执行", key);
                return;
            }

            try {
                log.info("Monitor 【{}】 开始执行", key);
                monitorMapper.updateStatusById(MonitorStatusEnum.MONITORING.getStatus(), e.getId());
                monitorService.monitorOfStep(e);
            } catch (Exception ex) {
                monitorMapper.updateStatusById(MonitorStatusEnum.UNMONITOR.getStatus(), e.getId());
                log.error("Monitor【{}】 执行报错，报错原因如下：", key, ex);
            } finally {
                log.info("Monitor 【{}】 执行结束", key);
                monitorMapper.unFrozenMonitor(e.getId());
                unFairLock(key);
            }
        });
    }

    public Boolean tryFairLock(String lockKey) {
        RLock lock = redissonClient.getFairLock(lockKey);
        try {
            return lock.tryLock(10, 60 * 60, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error("尝试获取锁【{}】失败", lockKey);
            e.printStackTrace();
        }
        return Boolean.FALSE;
    }

    public boolean unFairLock(String lockKey) {
        try {
            RLock lock = redissonClient.getFairLock(lockKey);
            if (null != lock && lock.isHeldByCurrentThread()) {
                lock.unlock();
                return true;
            }
        } catch (Exception e) {
            log.error("解锁【{}】失败", lockKey);
            e.printStackTrace();
        }
        return false;
    }

}
