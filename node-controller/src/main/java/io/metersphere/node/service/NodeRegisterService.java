package io.metersphere.node.service;

import com.alibaba.fastjson.JSONObject;
import io.metersphere.api.config.KafkaProperties;
import io.metersphere.api.jmeter.utils.MSException;
import io.metersphere.node.constants.EngineType;
import io.metersphere.node.constants.NodeStatus;
import io.metersphere.node.http.QHttpResponse;
import io.metersphere.node.util.HostAddress;
import io.metersphere.node.util.HttpUtil;
import io.metersphere.node.util.LogUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class NodeRegisterService {

    @Value("${maxLoad}")
    private String maxLoad;

    @Value("${backend.address}")
    private String address;

    @Resource
    private KafkaProperties kafkaProperties;

    /**
     * node节点注册
     * @return
     */
    public String nodeRegister(){
        String url = String.format("%s/httpruner/nodeRegister",address);
        //构造请求参数
        JSONObject object = new JSONObject();
        object.put("nodeId", HostAddress.getLocalIP().replace(".",""));
        object.put("ip", HostAddress.getLocalIP());
        object.put("maxLoad", maxLoad);
        object.put("type", EngineType.HttpRunner);
        object.put("nodeStatus", NodeStatus.RUNNABLE);
        object.put("statusMsg", "");
        String params = object.toString();
        QHttpResponse response = HttpUtil.postJson(url,params,null);
        String flag = "false";
        if (response != null){
            flag = String.valueOf(response.getJsonNode().get("success"));
            //节点注册成功后得到topic，topic应该存到哪里？
            if (flag.equals("true")){
                String taskTopic = String.valueOf(response.getJsonNode().get("data").get("taskTopic"));
                String bootstrapServers = String.valueOf(response.getJsonNode().get("data").get("bootstrapServers"));
                kafkaProperties.setTopic(taskTopic);
                kafkaProperties.setBootstrapServers(bootstrapServers);
                LogUtil.info("节点注册成功");
            }else {
                String msg = String.valueOf(response.getJsonNode().get("message"));
                LogUtil.error(msg);
            }
        }else {
            //MSException.throwException("节点注册请求接口异常");
            LogUtil.error("节点注册请求接口异常或服务器没有启动");
        }
        return flag;
    }

    /**
     * 心跳上报控制
     */
    @Scheduled(fixedRate = 1000)
    public void hearDetection(){
        String url = String.format("%s/httpruner/nodeHearbeat",address);
        JSONObject object = new JSONObject();
        object.put("nodeId", HostAddress.getLocalIP().replace(".",""));
        object.put("nodeStatus", NodeStatus.RUNNABLE);
        object.put("statusMsg", "");
        String params = object.toString();
        QHttpResponse response = HttpUtil.postJson(url,params,null);
        if (response != null){
            String flag = String.valueOf(response.getJsonNode().get("success"));
            //LogUtil.info("节点心跳状态：" + flag);
        }else {
            LogUtil.error("与服务器连接断开");
            MSException.throwException("与服务器连接断开");
        }
    }
}
