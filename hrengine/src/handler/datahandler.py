import datetime
import json
import logging
import os
import time
import traceback
from urllib.parse import urlparse

import requests
from httprunner import logger

from handler.statushandler import report_task_status
from utils import file_util


class PythonObjectEncoder(json.JSONEncoder):
    def default(self, obj):
        try:
            return super().default(self, obj)
        except TypeError:
            return str(obj)

class TestDataHandler():
    '''
    由kafka回传改成通过http协议回传的原因是 最终汇总的结果可能会很大，导致超过kafka最大message的长度
    '''

    def __init__(self, data_callback_url, report_status_url, task_id):
        self.data_callback_url = data_callback_url
        self.report_status_url = report_status_url
        self.task_id = task_id
        self.headers = {"Accept": "application/json, text/plain, */*"}

    @staticmethod
    def get_path_from_url(url):
        parseRes = urlparse(url)
        return parseRes.path

    def produceMsg(self, summary):
        # 通过http协议返回最终的统计数据
        try:
            # 仅返回需要的数据，避免宽带浪费
            testCaseResults = []
            for testCase in summary.get("details", []):

                testStepResults = []
                for testStep in testCase.get("records", []):
                    request = testStep["meta_datas_expanded"][0]["data"][0]["request"]
                    response = testStep["meta_datas_expanded"][0]["data"][0]["response"]
                    stat = testStep["meta_datas_expanded"][0]["stat"]
                    validators = testStep["meta_datas_expanded"][0]["validators"]
                    testStepInfo = {
                        "taskId": self.task_id,
                        "name": testStep["name"],
                        "path": self.get_path_from_url(request["url"]),
                        "method": request["method"],
                        "status": testStep["status"],
                        "attachment": testStep["attachment"],
                        "validateExtractor": json.dumps(validators.get("validate_extractor"), separators=(',', ':'), ensure_ascii=False, cls=PythonObjectEncoder),
                        "responseInfo": {
                            "url": response.get("url", ""),
                            "headers": response.get("headers"),
                            "responseOk": response.get("ok", ""),
                            "statusCode": response.get("status_code", ""),
                            "statusMsg": response.get("reason", ""),
                            "cookies": json.dumps(response.get("cookies"), separators=(',', ':'), ensure_ascii=False, cls=PythonObjectEncoder),
                            "encoding": response.get("encoding"),
                            "contentType": response.get("content_type"),
                            "body": response.get("body", "")
                        },
                        "requestInfo": {
                            "url": request.get("url"),
                            "method": request.get("method"),
                            "headers": request.get("headers"),
                            "body": request.get("body", "")
                        },
                        "responseTimeMs": stat.get("response_time_ms"),
                        "elapsedMs": stat.get("elapsed_ms"),
                        "contentSize": stat.get("content_size")
                    }
                    testStepResults.append(testStepInfo)

                testCaseInfo = {
                    "taskId": self.task_id,
                    "name": testCase["name"],
                    "startTime": datetime.datetime.fromtimestamp(testCase["time"]["start_at"]).strftime("%Y-%m-%dT%H:%M:%S.%f")[0:-3],
                    "success": testCase["success"],
                    "stepStatInfo": {
                        "total": testCase["stat"]["total"],
                        "failures": testCase["stat"]["failures"],
                        "errors": testCase["stat"]["errors"],
                        "skipped": testCase["stat"]["skipped"],
                        "expectedFailures": testCase["stat"]["expectedFailures"],
                        "unexpectedSuccesses": testCase["stat"]["unexpectedSuccesses"],
                        "successes": testCase["stat"]["successes"]
                    },
                    "duration": testCase["time"]["duration"],
                    "in": json.dumps(testCase["in_out"]["in"], separators=(',', ':'), ensure_ascii=False, cls=PythonObjectEncoder),
                    "out": json.dumps(testCase["in_out"]["out"], separators=(',', ':'), ensure_ascii=False, cls=PythonObjectEncoder),
                    "testStepResults": testStepResults
                }
                testCaseResults.append(testCaseInfo)

            message = {
                "taskId": self.task_id,
                "startTime": datetime.datetime.fromtimestamp(summary["time"]["start_at"]).strftime("%Y-%m-%dT%H:%M:%S.%f")[0:-3],
                "finishTime": datetime.datetime.fromtimestamp(summary["time"]["start_at"] + summary["time"]["duration"]).strftime("%Y-%m-%dT%H:%M:%S.%f")[0:-3],
                "stepStatInfo": {
                    "total": summary["stat"]["teststeps"]["total"],
                    "failures": summary["stat"]["teststeps"]["failures"],
                    "errors": summary["stat"]["teststeps"]["errors"],
                    "skipped": summary["stat"]["teststeps"]["skipped"],
                    "expectedFailures": summary["stat"]["teststeps"]["expectedFailures"],
                    "unexpectedSuccesses": summary["stat"]["teststeps"]["unexpectedSuccesses"],
                    "successes": summary["stat"]["teststeps"]["successes"]
                },
                "testCaseStatInfo": {
                    "total": summary["stat"]["testcases"]["total"],
                    "failures": summary["stat"]["testcases"]["fail"],
                    "success": summary["stat"]["testcases"]["success"],
                },
                "duration": summary["time"]["duration"],
                "platform": json.dumps(summary["platform"], separators=(',', ':'), ensure_ascii=False, cls=PythonObjectEncoder),
                "testCaseResults": testCaseResults
            }

            parmas_message = json.dumps(message, separators=(',', ':'), ensure_ascii=False, cls=PythonObjectEncoder)


            path = 'data/{}.txt'.format(self.task_id)
            if not os.path.exists("data/"):
                os.mkdir("data")
            file_util.setFileContent(path, parmas_message)
            time.sleep(1)
            files = {'result': open(path, 'rb')}
            respond = requests.post(self.data_callback_url, files=files, headers=self.headers, verify=False, timeout=100)
            # os.remove(path)
            success = False
            if respond.status_code == 200:
                print(respond)
                resJson = respond.json()
                success = resJson["success"]
                logger.get_logger().info("回传结果{}".format("成功" if success else "失败"))
                print(respond.content)
                report_task_status(self.report_status_url, self.task_id, "DONE")
            else:
                logger.get_logger().info("回传结果失败，原因：接口访问异常，http response code: {}".format(respond.status_code))
                report_task_status(self.report_status_url, self.task_id, "ERROR")
        except Exception as e:
            logging.info(e)
            logger.get_logger().info("回传结果失败，原因：接口访问异常，异常描述: {}".format(e))
            # msg = traceback.format_exc()
            # logger.get_logger().info(msg)
            report_task_status(self.report_status_url, self.task_id, "ERROR")
