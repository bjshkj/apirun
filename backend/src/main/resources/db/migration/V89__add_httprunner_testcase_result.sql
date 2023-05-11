CREATE TABLE IF NOT EXISTS `api_test_case_summary`
(
  `id`              bigint(20) unsigned auto_increment COMMENT 'ID',
  `task_id`         varchar(50) NOT NULL COMMENT 'task ID',
  `idx`             TINYINT unsigned NOT NULL default 0 COMMENT 'index',
  `name`            varchar(255) NOT NULL COMMENT 'test case name',
  `start_time`      DATETIME  NOT NULL COMMENT 'start run time',
  `success`         TINYINT(1) DEFAULT NULL COMMENT 'boolean, success or fail',
  `duration`        DOUBLE unsigned DEFAULT NULL COMMENT 'exec time, seconds',
  `ins`              longtext COMMENT 'input param',
  `out`             longtext COMMENT 'out vars',
  `step_total`      TINYINT unsigned default 0 comment 'total step count in testcase',
  `step_failures`   TINYINT unsigned default 0 comment 'failures step count',
  `step_errors`     TINYINT unsigned default 0 comment 'error step count',
  `step_skipped`    TINYINT unsigned default 0 comment 'skipped step count',
  `step_expected_failures` TINYINT unsigned default 0 comment 'expected failures step count',
  `step_unexpected_successes` TINYINT unsigned default 0 comment 'unexpected successes step count',
  `step_successes` TINYINT unsigned default 0 comment 'successes step count',
  `create_time`    DATETIME  NOT NULL default CURRENT_TIMESTAMP COMMENT 'Update timestamp',
PRIMARY KEY (`id`),
UNIQUE KEY `id_idx` (`id`, `idx`)
) ENGINE = InnoDB
DEFAULT CHARSET = utf8mb4;
