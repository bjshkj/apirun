CREATE TABLE IF NOT EXISTS node_register_info(
	node_id VARCHAR(50) PRIMARY KEY  COMMENT 'node id',
	ip VARCHAR(50) COMMENT 'ip address of the node',
	secret_key VARCHAR(100) COMMENT 'secret  Key',
	max_load int(10) COMMENT 'the max count of task',
	type VARCHAR(50) COMMENT 'the type of engine',
	node_status VARCHAR(25) COMMENT 'docker node status, RUNNABLE(可运行)\BUSY(node忙碌，不能再接收任务)\ERROR(-node节点故障)',
	status_msg VARCHAR(200) COMMENT 'the stutus message',
	total_tasks int(10) COMMENT 'the count of task executing',
	create_time bigint(13) COMMENT 'the time of the node create',
	update_time bigint(13) COMMENT 'the time of the node update'
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;
