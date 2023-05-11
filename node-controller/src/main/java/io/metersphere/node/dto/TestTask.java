package io.metersphere.node.dto;

import lombok.Data;

@Data
public class TestTask {
    private String nodeId;          //执行任务的node唯一标识，每个node只获取自己nodeId的任务，其他丢弃
    private String projectId;       //项目id
    private String gitVersion;      //项目版本
    private KafkaDto kafkaDto;      //kafka信息
    private String taskId;          //测试任务id
    private String image;           //docker运行的镜像
    private String codeVersion;     //将代码仓库版本作为执行用例版本
    private String debugMode;      //默认true，表示开启debug模式
    private String execCasePaths;   //当前任务指定执行范围，执行测试用例目录或者yml文件路径，相对于debugtalk.py的相对路径
    private String dataCallbackUrl;
    private String dotEnvPath;      //执行用例使用的环境配置文件相对dubugtalk.py的路径
    private String hostsPath;       //执行用例使用的hosts配置文件相对debugtalk.py的路径
}
