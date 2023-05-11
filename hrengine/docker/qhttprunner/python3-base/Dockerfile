FROM centos:8
LABEL maintainer="lijiafeng"
# 设置时区变量
ENV TZ Asia/Shanghai
RUN set -ex \
    # 预安装所需组件
    && yum install -y wget tar libffi-devel zlib-devel bzip2-devel openssl-devel ncurses-devel sqlite-devel readline-devel tk-devel gcc make initscripts \
    && yum install -y python3 python3-devel \ 
    && ln -sf /usr/bin/python3 /usr/bin/python 
# 更新pip版本
RUN pip3 install --upgrade pip
