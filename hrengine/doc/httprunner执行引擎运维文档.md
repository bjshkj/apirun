# Httprunner执行引擎运维文档

`` 编写：longhui@360.cn 
``

## 一. 项目存在原因：
需要支持httprunner脚本的调试和执行；
为了隔绝、并行和执行环境个性化，使用docker方式运行；

## 二. 使用描述
### 1. 引擎入口：
app.py

### 2. 引擎启动命令描述：

python  app.py 

    -tid task_id_11111   # 引擎当前执行任务的id，在结果回传和日志回传时，与具体数据对应，在数据解析时，才能和任务关联上；
    -rpath E:\work\接口测试平台\data\crminterface.git-copy  # 测试用例yaml文件和公共库函数的目录，要求与httprunner要求的目录层级保持一致；参看 https://v2.httprunner.org/prepare/project-structure/
    -kbserver 10.208.251.93:39092   # kafka_bootstrap_servers：日志和测试数据回传都使用kafka消息队列，所以需要传入连接IP
    -ktlog testrobot_httprunner_log_test  # kafka_topics_log：日志回传的kafka消息队列topic
    -dcurl http://127.0.0.1:8081/api/httprunner/result/summary  #  data_callback_url：测试数据回传服务的访问连接，url
    -d debug  #  debug_mode：是否是debug模式, 打开debug模式填写 'debug'
    -cpath testcases/staging/sales/bk/stage_acCustomerManage.yml  #  exec_case_path：case执行范围，可以是目录，也可以是指定的一个yml文件， 相对 test_dir的路径就行
    -cpath testcases/staging/sales/wc/stage_wc_Audit.yml 
    -epath env\staging.env    # dot_env_path：使用的测试环境文件路径，相对 test_dir的路径就行


### 3. docker镜像构建
a) 镜像基于python3-base镜像；
b) httprunner执行引擎：基于httprunner 2.5.7进行改造，主要
   增加一个kafka log handler(为了采集httprunner执行过程中的日志)；
   执行完后，调用服务端接口（dcurl参数）返回执行结果，这里仅返回需要的数据，结构进行修改；
c) docker构建Dockerfile过程：
    镜像Dockerfile文件等信息在 hrengine/docker/qhttprunner中
    
    ```shell
       cd hrengine/docker/qhttprunner
       # 通用python3环境docker镜像
       cd  python3-base/
       sh  punish.sh
    
       # httprunner docker镜像
       # 将src目录拷贝到 qhttprunner-base/ 之下
       cp -r src ./qhttprunner-base/
       sh  punish.sh
       
    注意：引擎修改后，需要重新导出当前环境的python依赖列表 src/requirement.txt
    导出命令，参考：pip freeze > requirements.txt
    
### 4. httprunner用例执行注意事项：
    用例文件所处的根目录需要包含httprunner项目的python依赖文件requirement.txt
    


