CREATE TABLE `monitor` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
  `monitor_addr` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '监控的LP合约地址',
  `chain_id` int NOT NULL COMMENT '链ID',
  `status` int NOT NULL DEFAULT '0' COMMENT '监听器状态：0-未监听；1-监听中；2-禁止监听',
  `last_block_number` int NOT NULL COMMENT '上次监听的块高',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modify_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_chain_addr` (`chain_id`,`monitor_addr`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='监控表';

INSERT INTO monitor
(monitor_addr, chain_id, status, last_block_number, create_time, modify_time)
VALUES('0x46A15B0b27311cedF172AB29E4f4766fbE7F4364', 56, 0, 38586290, '2024-07-25 23:27:03', '2024-07-25 23:27:03');
INSERT INTO monitor
(monitor_addr, chain_id, status, last_block_number, create_time, modify_time)
VALUES('0x46A15B0b27311cedF172AB29E4f4766fbE7F4364', 42161, 2, 224139720, '2024-07-28 12:58:05', '2024-07-28 12:58:05');


CREATE TABLE `lp_dict` (
  `id` int NOT NULL AUTO_INCREMENT,
  `chain_id` int DEFAULT NULL,
  `name` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `value` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '0-失效；1-生效',
  `remark` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb3;

INSERT INTO lp_dict
(chain_id, name, value, status, remark)
VALUES(NULL, 'operator_addr', '0xa4f8ec24AA815700C68209620f549f5d45ffdF73', 1, '读合约使用的账户，无gas消耗');
INSERT INTO lp_dict
(chain_id, name, value, status, remark)
VALUES(56, 'cake_pie_staked', '0xb47b790076050423888cde9EBB2D5Cb86544F327', 1, 'BSC链上CakePie质押合约');
INSERT INTO lp_dict
(chain_id, name, value, status, remark)
VALUES(56, 'pancake_native_staked', '0x556B9306565093C855AEA9AE92A594704c2Cd59e', 1, 'BSC链上原生Pancake质押合约');
INSERT INTO lp_dict
(chain_id, name, value, status, remark)
VALUES(NULL, 'zero_addr', '0x0000000000000000000000000000000000000000', 1, '零地址');
INSERT INTO lp_dict
(chain_id, name, value, status, remark)
VALUES(56, 'confirm_block_size', '12', 1, 'BSC链确认区块高度');
INSERT INTO lp_dict
(chain_id, name, value, status, remark)
VALUES(56, 'block_size', '1000', 1, 'BSC链监听的区块间隔步长');


CREATE TABLE `lp_info` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `chain_id` int NOT NULL,
  `lp_addr` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `token_id` bigint NOT NULL,
  `token0_addr` varchar(64) NOT NULL,
  `token1_addr` varchar(64) NOT NULL,
  `fee` int NOT NULL,
  `start_block_number` bigint NOT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modify_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='lp信息表';

INSERT INTO lp_info
(chain_id, lp_addr, token_id, token0_addr, token1_addr, fee, start_block_number, create_time, modify_time)
VALUES(56, '0x12197d7a4fE2d67F9f97ae64D82A44c24B7Ad407', 1308667, '0x4aae823a6a0b376De6A78e74eCC5b079d38cBCf7', '0x7130d2A12B9BCbFAe4f2634d864A1Ee1Ce3Ead9c', 500, 38539495, '2024-07-26 02:24:40', '2024-07-26 02:24:40');
INSERT INTO lp_info
(chain_id, lp_addr, token_id, token0_addr, token1_addr, fee, start_block_number, create_time, modify_time)
VALUES(42161, '0x04a35d7920f2f2ff9faa447ff8cfad47fc7ced2b', 35314, '0x3647c54c4c2C65bC7a2D63c0Da2809B399DBBDC0', '0xaFAfd68AFe3fe65d376eEC9Eab1802616cFacCb8', 500, 224139727, '2024-07-28 13:54:15', '2024-07-28 13:54:15');
INSERT INTO lp_info
(chain_id, lp_addr, token_id, token0_addr, token1_addr, fee, start_block_number, create_time, modify_time)
VALUES(56, '0x7c6cc4d67c920e4b86d46ea125f69b410afdaf61', 1287411, '0x4aae823a6a0b376De6A78e74eCC5b079d38cBCf7', '0x53E63a31fD1077f949204b94F431bCaB98F72BCE', 500, 39921826, '2024-07-28 13:54:15', '2024-07-28 13:54:15');


CREATE TABLE `lp_position` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `chain_id` int NOT NULL,
  `user_addr` varchar(64) NOT NULL,
  `monitor_addr` varchar(64) NOT NULL,
  `lp_addr` varchar(64) NOT NULL,
  `token_id` bigint NOT NULL,
  `token0_amount` decimal(32,18) NOT NULL DEFAULT '0.000000000000000000',
  `token1_amount` decimal(32,18) NOT NULL DEFAULT '0.000000000000000000',
  `staked_status` tinyint NOT NULL DEFAULT '0' COMMENT '质押状态：0-未质押；1-原生质押；2-代理质押',
  `staked_addr` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '质押的合约地址',
  `staked_proxy_addr` varchar(64) DEFAULT NULL COMMENT '质押的代理合约地址',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modify_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `lp_record` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `chain_id` int DEFAULT NULL,
  `tx_hash` varchar(127) DEFAULT NULL,
  `user_addr` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `monitor_addr` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `lp_addr` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `token_id` bigint NOT NULL,
  `token0_amount` decimal(32,18) NOT NULL DEFAULT '0.000000000000000000',
  `token1_amount` decimal(32,18) NOT NULL DEFAULT '0.000000000000000000',
  `type` int NOT NULL DEFAULT '0' COMMENT 'lp变动类型：0-添加流动性；1-移除流动性；2-质押LP；3-取消质押LP；4-普通转账',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modify_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;