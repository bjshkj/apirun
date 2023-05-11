import argparse
import os
import sys
import traceback

from httprunner import logger

from exceptions import EngineException
from handler.datahandler import TestDataHandler
from handler.loghandler import KafkaLoggingHandler
from qhttprunner import QHttprunner
from handler.statushandler import report_task_status

if __name__ == "__main__":

    # 从命令行获取执行参数, test_dir: 按固定格式将httprunner需要的数据，全部准备好
    # python app.py task_id<str> test_dir<path> kafka_bootstrap_servers<str> kafka_topics_log<str> data_callback_url<str> debug_mode<str> exec_case_path<str> dot_env_path<str>
    parser = argparse.ArgumentParser('传入参数：app.py')
    parser.add_argument('-tid','--test_id', required=True, help='引擎当前执行任务的id，在结果回传和日志回传时，与具体数据对应，在数据解析时，才能和任务关联上')
    parser.add_argument('-rpath','--root_path', required=True, help='测试用例yaml文件和公共库函数的目录，要求与httprunner要求的目录层级保持一致')
    parser.add_argument('-cpath','--exec_case_path', action="append", required=True, help='日志和测试数据回传都使用kafka消息队列，所以需要传入连接IP')
    parser.add_argument('-epath','--dot_env_path', required=True, help='日志回传的kafka消息队列topic')
    parser.add_argument('-d','--debug_mode', required=False, default='debug', help='测试数据回传服务的访问连接，url')
    parser.add_argument('-dcurl','--data_callback_url', required=True, help='是否是debug模式, 打开debug模式填写 "debug"')
    parser.add_argument('-kbserver','--kafka_bootstrap_servers', required=True, help='case执行范围，可以是目录，也可以是指定的一个yml文件， 相对 rpath的路径就行')
    parser.add_argument('-ktlog','--kafka_topics_log', required=True, help='使用的测试环境文件路径，相对 rpath的路径就行')
    parser.add_argument('-rsurl', '--report_status_url', required=True, help='测试上报任务状态的访问连接，url')

    args = parser.parse_args()
    task_id = args.test_id
    test_root_dir = args.root_path
    kafka_bootstrap_servers = args.kafka_bootstrap_servers
    kafka_topics_log = args.kafka_topics_log
    data_callback_url = args.data_callback_url
    report_status_url = args.report_status_url

    # 创建kafka log消息队列，为了实现测试日志实时展示
    kafka_logger_handler = KafkaLoggingHandler(kafka_bootstrap_servers, kafka_topics_log, task_id)
    logger.get_logger().addHandler(kafka_logger_handler)
    logger.get_logger().info("===================  init log kafka handler success ===================")
    # 是否是以debug模式启动httprunner，debug_mode=debug
    debug_mode = False
    if args.debug_mode == "debug":
        debug_mode = True
        logger.get_logger().info("===================  start engine by debug mode. ===================")

    # 当前执行用例目录或者yaml文件路径
    exec_case_paths = []
    if isinstance(args.exec_case_path, (list)):
        exec_case_paths = args.exec_case_path
    else:
        exec_case_paths = [args.exec_case_path]

    dot_env_path = None
    if args.dot_env_path is not None:
        dot_env_path = args.dot_env_path


    test_data_handler = TestDataHandler(data_callback_url, report_status_url, task_id)
    # test_data_handler.produceMsg("ok")
    logger.get_logger().info("===================  init data kafka handler success ===================")

    try:
        debugtalk_path = "{}/debugtalk.py".format(test_root_dir)
        # 工作目录的问题：将debugtalk_path置为当前工作空间
        os.chdir(os.path.dirname(debugtalk_path))
    except Exception as e:
        logger.get_logger().info("===================  debugtalk 模块导入异常  ===================")
        elines = traceback.format_exception(*sys.exc_info())
        logger.get_logger().error("\r\n".join(elines))
        report_task_status(report_status_url, task_id, "ERROR")
        raise EngineException("debugtalk 模块导入异常.")

    logger.get_logger().info("===================  init debugtalk exec env success  ===================")

    for exec_case_path in exec_case_paths:
        if not os.path.exists(exec_case_path):
            logger.get_logger().info("===================  引擎启动异常：执行case路径不存在。 path:{}  ===================".format(exec_case_path))
            elines = traceback.format_exception(*sys.exc_info())
            logger.get_logger().error("\r\n".join(elines))
            report_task_status(report_status_url, task_id, "ERROR")
            raise EngineException("引擎启动异常：执行case路径不存在。 path:{}".format(exec_case_path))

    runner = None
    try:
        # 做不到实时测试数据回传
        runner = QHttprunner(
            failfast=False,
            save_tests=False,
            log_level="DEBUG" if debug_mode else "INFO",
            log_file="./test.log"
        )
        logger.get_logger().info("===================  init debugtalk exec env success  ===================")
    except Exception as e:
        logger.get_logger().info("===================  init httprunner fail  ===================")
        elines = traceback.format_exception(*sys.exc_info())
        logger.get_logger().error("\r\n".join(elines))
        report_task_status(report_status_url, task_id, "ERROR")
        raise EngineException("init httprunner fail.")

    logger.get_logger().info("===================  httprunner 开始执行 case path: {}  ===================".format(",".join(exec_case_paths)))
    try:
        summary = runner.run(exec_case_paths, dot_env_path=dot_env_path)
    except Exception as e:
        logger.get_logger().info("===================  httprunner 执行异常  ===================")
        elines = traceback.format_exception(*sys.exc_info())
        logger.get_logger().info("\r\n".join(elines))
        report_task_status(report_status_url, task_id, "ERROR")
        raise EngineException(" httprunner exec fail. err:{}".format(e.__class__.__name__))

    logger.get_logger().info("===================  httprunner 执行结束  ===================")
    # 使kafka传回数据，数据存储，summary 结构数据，查看报告再填充模板
    test_data_handler.produceMsg(summary)
    logger.get_logger().info("httprunner end")
