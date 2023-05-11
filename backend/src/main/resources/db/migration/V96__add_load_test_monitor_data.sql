CREATE TABLE IF NOT EXISTS `load_test_monitor_data` (
    `id` bigint(20) unsigned NOT NULL auto_increment COMMENT 'auto increment id',
    `report_id` varchar(50) NOT NULL COMMENT '性能测试任务id',
    `target` varchar(64) NOT NULL COMMENT '监控目标',
    `target_type` varchar(64) NOT NULL COMMENT '监控目标类型，是服务器，还是某类数据库，还是施压服务器',
    `pointer_type` varchar(50) NOT NULL COMMENT '性能指标类型',
    `number_ical_unit` varchar(50) NOT NULL COMMENT '性能数数字单位',
    `monitor_name` varchar(255) NOT NULL COMMENT '监控数据名',
    `monitor_data` LONGTEXT NOT NULL COMMENT '性能数据',
    `time_stamp` LONGTEXT NOT NULL COMMENT '性能数据打点时间戳',
    `start_time` bigint(20) NOT NULL COMMENT '开始时间',
    `end_time` bigint(20) NOT NULL COMMENT '结束时间',
    `target_label` varchar(50) NULL,
    PRIMARY KEY (`id`)
    )
    ENGINE=InnoDB
    DEFAULT CHARSET=utf8mb4;