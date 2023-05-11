CREATE TABLE IF NOT EXISTS `api_test_task_log`
(
  `id`        bigint(20) unsigned NOT NULL auto_increment COMMENT 'ID',
  `report_id` varchar(50)  NOT NULL,
  `content` longtext,
  `part` bigint(20) DEFAULT NULL,
PRIMARY KEY (`id`),
KEY `api_test_task_log_report_id_index` (`report_id`,`part`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;