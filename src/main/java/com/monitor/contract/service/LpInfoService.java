package com.monitor.contract.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.monitor.contract.model.entity.LpInfo;
import com.monitor.contract.mapper.LpInfoMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * lp信息表 服务实现类
 * </p>
 *
 * @author peng
 * @since 2024-07-26
 */
@Service
public class LpInfoService extends ServiceImpl<LpInfoMapper, LpInfo> {

}
