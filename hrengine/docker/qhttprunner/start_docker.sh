#/usr/bin/env bash

docker run  \
--env kafka_bootstrap_servers=10.208.251.93:39092  \
--env kafka_topics_log=testrobot_httprunner_log_test  \
--env task_id=task_id_docker_2222   \
--env test_dir=/opt/qhttprunner/test/data  \
--env debug_mode=debug  \
--env exec_case_paths=testcases/staging/sales/bk/stage_acCustomerManage.yml:testcases/staging/sales/wc/stage_wc_Audit.yml  \
--env data_callback_url=http://127.0.0.1:8081/api/httprunner/result/summary  \
--env dot_env_path=env/staging.env  \
--add-host crm.360.cn:10.203.3.77 \
--add-host sales.crm.360.cn:10.203.3.77 \
--add-host api.crm.360.cn:10.203.3.77 \
-itd -v /home/docker/qhttprunner:/opt/qhttprunner/test --name test-httprunner  testrobot/qhttprunner:v2.0