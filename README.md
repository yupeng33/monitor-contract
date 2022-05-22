# monitor-contract
合约监听

业务逻辑：
1.定时任务启动后，从数据库查询当前需要监听的 链+合约+上次截止监听的块高
2.使用redisson对每个监听器增加分布式锁，多线程监听每个合约
3.监听到工厂合约生成了订单合约，则新增一个订单合约监听器
4.监听到订单合约的状态为2或者3，则该订单合约监听器停止监听


注意：
1.yml文件中需要配置好web3j对应的url
2.数据库里预置需要监听的工厂监听器和订单监听器信息
3.OrderStatusEnum和com.monitor.contract.service.MonitorService.updateOrderStatus方法需要根据业务进行补充，搜索 TODO 注释
4.定时任务（EnableScheduling）和多线程（EnableAsync）的代码都是spring工具，如有需要请自行更换
