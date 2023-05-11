#!/bin/bash
pip3 install -r ${test_dir}/requirement.txt

# 分隔执行用例路径列表
path_strs=${exec_case_paths}

OLD_IFS="$IFS"
IFS=":"

case_paths=($path_strs)

IFS="$OLD_IFS"

# 拼接执行命令参数
args="-tid ${task_id} -rpath ${test_dir} -kbserver ${kafka_bootstrap_servers} -ktlog ${kafka_topics_log} -dcurl ${data_callback_url} -d ${debug_mode} -epath ${dot_env_path} -rsurl ${report_status_url}"
# 加上执行用例路径
for cpath in ${case_paths[@]}
do
   args="$args -cpath $cpath"
done

echo $args

python3 /opt/qhttprunner/src/app.py $args
