import requests
import logging
from httprunner import logger

def report_task_status(url, task_id, task_status):
    params = {
        "id": task_id,
        "taskStatus": task_status,
    }
    header = {
        "Accept": "application/json, text/plain, */*",
        "Content-Type": "application/json;charset=UTF-8"
    }
    respond = requests.post(url, json=params, headers=header, verify=False, timeout=10)
    try:
        if respond.status_code == 200:
            resJson = respond.json()
            success = resJson["success"]
            logger.get_logger().info("上报任务结束状态{}".format("成功" if success else "失败"))
            print(respond.content)
        else:
            logger.get_logger().info("上报任务结束状态失败，原因：接口访问异常，http response code: {}".format(respond.status_code))
    except Exception as e:
        logging.info(e)
        logger.get_logger().info("上报任务结束状态失败，原因：接口访问异常，异常描述: {}".format(e))