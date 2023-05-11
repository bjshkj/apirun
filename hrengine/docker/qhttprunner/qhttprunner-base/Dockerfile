FROM python3:base
LABEL maintainer="lijiafeng"

ENV HOME_WORK /opt/qhttprunner

# 加入qhttprunner的项目依赖
RUN  mkdir -p ${HOME_WORK} && \
     chmod +x ${HOME_WORK}
	 
ADD run.sh ${HOME_WORK}/
ADD src/ ${HOME_WORK}/src/

RUN pip3 install -r ${HOME_WORK}/src/requirement.txt

ENTRYPOINT ["bash", "/opt/qhttprunner/run.sh"]
