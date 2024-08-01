package com.monitor.contract.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.monitor.contract.model.entity.Monitor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.List;

@Mapper
public interface MonitorMapper extends BaseMapper<Monitor> {
}
