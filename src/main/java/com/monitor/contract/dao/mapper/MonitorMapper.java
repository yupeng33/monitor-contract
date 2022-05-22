package com.monitor.contract.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.monitor.contract.model.entity.Monitor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.List;

@Mapper
public interface MonitorMapper extends BaseMapper<Monitor> {
    List<Monitor> selectUnEndMonitor();


    void updateLastByAddr(@Param("blockNumber") BigInteger blockNumber, @Param("contractAddress") String contractAddress);
    void updateEndByAddr(@Param("blockNumber") BigInteger blockNumber, @Param("contractAddress") String contractAddress, @Param("txHash") String txHash);

    void updateStatusById(@Param("status") Integer status, @Param("id") Long id);

    void unFrozenMonitor(Long id);
}
