package com.monitor.contract.controller;

import com.monitor.contract.common.Result;
import com.monitor.contract.service.DataService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/monitor")
public class DataController {

    @Resource
    private DataService dataService;

    @GetMapping("/api")
    public Result<String> getData(@RequestParam("chainId") Integer chainId,
                                  @RequestParam("lpAddress") String lpAddress,
                                  @RequestParam(value = "poolAddress", required = false) String poolAddress) {
        return Result.success(dataService.getData(chainId, lpAddress, poolAddress));
    }
}
