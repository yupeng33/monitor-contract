CREATE TABLE `monitor` (
                           `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
                           `addr` varchar(100) NOT NULL COMMENT '监控的合约地址',
                           `chain` varchar(100) NOT NULL COMMENT '链名称，全大写',
                           `end_block_number` int(11) DEFAULT NULL COMMENT '结束监听的块高',
                           `start_block_number` int(11) NOT NULL COMMENT '开始监听的块高',
                           `status` int(2) NOT NULL DEFAULT '0' COMMENT '监听器状态：0-未监听；1-监听中；2-监听结束',
                           `end_tx_hash` varchar(100) DEFAULT NULL COMMENT '结束监听时的txHash',
                           `last_block_number` int(11) NOT NULL COMMENT '上次监听的块高',
                           `type` int(2) NOT NULL DEFAULT '1' COMMENT '合约类型：0-工厂合约；1-订单合约',
                           PRIMARY KEY (`id`),
                           UNIQUE KEY `uniq_chain_addr` (`chain`,`addr`)
) ENGINE=InnoDB AUTO_INCREMENT=62 DEFAULT CHARSET=utf8mb4 COMMENT='监控表';

INSERT INTO test.monitor (addr,end_block_number,`chain`,start_block_number,status,end_tx_hash,last_block_number,`type`) VALUES
('0x7fE0af13c42b8C73f11069D919A476B36a955f43',null,'BSC',19374876,0,'null',19374876,0);