CREATE TABLE `df_device_info` (
  `id` varchar(32) NOT NULL COMMENT '主键id',
  `device_name` varchar(32) NOT NULL COMMENT '设备名称',
  `device_code` varchar(32) NOT NULL COMMENT '设备编码',
  `api_key` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '秘钥',
  `limit_money` bigint(32) DEFAULT NULL COMMENT '限额',
  `balance` bigint(32) DEFAULT NULL COMMENT '余额',
  `grouping_code` varchar(32) DEFAULT NULL COMMENT '分组编码',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态：1：正常；2：禁用',
  `cleared_time` datetime DEFAULT NULL COMMENT '清零时间',
  `create_by` varchar(32) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(32) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `device_code_key` (`device_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='设备信息';

CREATE TABLE `df_device_user` (
  `id` varchar(32) NOT NULL COMMENT '主键id',
  `device_code` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '设备id',
  `user_name` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '商户id',
  `create_by` varchar(32) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(32) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `device_user` (`device_code`,`user_name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='商户关联设备';

