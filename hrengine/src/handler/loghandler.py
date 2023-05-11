import datetime
import json
import logging
import time

from kafka import KafkaProducer
from kafka.errors import KafkaError


class KafkaLoggingHandler(logging.Handler):

    def __init__(self, kafka_bootstrap_servers, kafka_topics, task_id, **kwargs):
        logging.Handler.__init__(self)
        self.kafka_topic_name = kafka_topics
        self.producer = KafkaProducer(bootstrap_servers=kafka_bootstrap_servers)
        self.task_id = task_id
        self.key = "log-{}".format(task_id)

    @staticmethod
    def on_send_success(record_metadata):
        #如果消息成功写入Kafka，broker将返回RecordMetadata对象（包含topic，partition和offset）；
        print("Success:[{0}] send success".format(record_metadata))

    @staticmethod
    def on_send_error(excp):
        #相反，broker将返回error。这时producer收到error会尝试重试发送消息几次，直到producer返回error。
        print("INFO" + "Fail:send fail cause:{0}".format(excp))

    def emit(self, record):
        # 忽略kafka的日志，以免导致无限递归。
        if 'kafka' in record.name:
            return

        try:
            # 将日志和任务id匹配起来，服务端才能关联上
            msg = json.dumps(
                {
                    "taskId":self.task_id,
                    "time": datetime.datetime.fromtimestamp(record.created).strftime("%Y-%m-%d %H:%M:%S"),
                    "msg": record.msg,
                    "loggerName": record.name,
                    "level": record.levelname,
                    "path": record.pathname,
                    "lineno":record.lineno
                }
            )
            # kafka生产者，发送消息到broker。
            future = self.producer.send(topic=self.kafka_topic_name, value=bytes(msg, encoding = "utf8"), key=bytes(self.key, encoding = "utf8"))
            record_metadata = future.get(timeout=10)
            self.on_send_success(record_metadata)
        except KafkaError as e:
            self.on_send_error(e)
        except (KeyboardInterrupt, SystemExit):
            self.producer.close()
            raise
        except Exception:
            self.handleError(record)