ARG IMAGE_PREFIX=metersphere

FROM alpine:3.13
LABEL maintainer="support@fit2cloud.com"

# 环境变量
ENV JMETER_VERSION "5.4.1"
ENV KAFKA_BACKEND_LISTENER_VERSION "1.0.4"
ENV DUBBO_JMETER_PLUGIN_VERSION "2.7.7"
ENV JMETER_HOME /opt/jmeter
ENV PATH $PATH:$JMETER_HOME/bin
ENV SSL_DISABLED true
ENV TESTS_DIR /test
ENV JAVA_VERSION "8"
ENV FORMAT_MASSAGES_PATTERN_DISABLE_LOOKUPS "true"

#定义时区参数
ENV TZ=Asia/Shanghai

# 执行构建脚本
ADD build-jmeter.sh /build-jmeter.sh
RUN chmod +x /build-jmeter.sh
RUN ./build-jmeter.sh

#
ADD log4j2.xml $JMETER_HOME/bin/log4j2.xml
ADD jmeter.properties $JMETER_HOME/bin/jmeter.properties

EXPOSE 60000

ADD run-test.sh /run-test.sh
RUN chmod +x /run-test.sh \
    && mkdir /test \
    && mkdir /jmeter-log

WORKDIR /jmeter-log/
ENTRYPOINT /run-test.sh