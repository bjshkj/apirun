ALTER TABLE api_test_step_result
    MODIFY COLUMN `status_code` VARCHAR(10);

ALTER TABLE api_test_step_result
    MODIFY COLUMN `res_time_ms` VARCHAR(50);

ALTER TABLE api_test_step_result
    MODIFY COLUMN `elapsed_ms` VARCHAR(50);

ALTER TABLE api_test_step_result
    MODIFY COLUMN `content_size` VARCHAR(50);

ALTER TABLE httprunner_task
    MODIFY COLUMN `exec_case_paths` longtext;

-- 操作日志增加普通索引
ALTER TABLE `operating_log` ADD INDEX oper_module_index ( `oper_time`,`oper_type`,`oper_path` );
ALTER TABLE `operating_log` ADD INDEX oper_module_index2 ( `project_id`,`oper_time`,`oper_path` );
ALTER TABLE `operating_log` ADD INDEX oper_group3_index ( `project_id`);
ALTER TABLE `operating_log` ADD INDEX oper_module_index4 ( `oper_user`,`oper_time`);

-- project增加普通索引
ALTER TABLE `project` ADD INDEX project_id_index (`id` `workspace_id`,`create_time`);