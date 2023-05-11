package io.metersphere.node.service;

import com.alibaba.fastjson.JSONObject;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.WaitContainerResultCallback;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.InvocationBuilder;
import io.metersphere.api.jmeter.utils.MSException;
import io.metersphere.node.dto.TestTask;
import io.metersphere.node.http.QHttpResponse;
import io.metersphere.node.util.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.File;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class HttpRunnerOperateService {
    //映射目录
    private static final String PATH = "/opt/qhttprunner/test";
    private static final String HOST_PATH = "/home/docker/qhttprunner/";
    private static final String TASK_END_URL = "/httpRunnerTask/httpRunnerTaskResultStatus";

    @Value("${backend.address}")
    private String address;

    @Resource
    private KafkaProducer kafkaProducer;

    public void startContainer(TestTask testTask, String host_Directory, String[] hosts) {
        String testId = testTask.getTaskId();
        LogUtil.info("Receive start container request, test id: {}", testId);
        // 检查kafka连通性
        String bootstrapServers = testTask.getKafkaDto().getBootstrapServers();
        checkKafka(bootstrapServers);
        //连接docker
        DockerClient dockerClient = DokcerHttpRunnerService.connectDocker();

        String containerImage = testTask.getImage();
        // 查找镜像是否存在
        searchImage(dockerClient, containerImage);
        // 检查容器是否存在
        checkContainerExists(dockerClient, testTask.getTaskId());
        // 启动测试
        startContainer(testTask, dockerClient, testTask.getTaskId(), containerImage, host_Directory, hosts);
    }

    /**
     *
     * @param testTask 任务类
     * @param dockerClient
     * @param testId 任务id
     * @param containerImage docker镜像名
     * @param host_Directory docker宿主机目录
     * @param hosts
     */
    private void startContainer(TestTask testTask, DockerClient dockerClient, String testId, String containerImage, String host_Directory, String[] hosts) {
        // 创建 hostConfig
        HostConfig hostConfig = HostConfig.newHostConfig();
        //hostConfig.withNetworkMode("host");
        //添加host参数
        hostConfig.withExtraHosts(hosts);
        //挂载宿主机目录映射到容器目录
        hostConfig.withBinds(new Bind(host_Directory, new Volume(PATH)));

        String[] envs = getEnvs(testTask);
        String containerId = DokcerHttpRunnerService.createContainers(dockerClient, testId, containerImage, hostConfig, envs).getId();
        LogUtil.info("Container create started containerId: " + containerId);

        DokcerHttpRunnerService.startContainer(dockerClient, containerId);

        String topic = testTask.getKafkaDto().getLogTopic();
        String reportId = testTask.getTaskId();
        //等待容器结果回调
        dockerClient.waitContainerCmd(containerId)
                .exec(new WaitContainerResultCallback() {
                    @Override
                    public void onComplete() {
                        // 清理文件夹
                        try {
                            if (DokcerHttpRunnerService.existContainer(dockerClient, containerId) > 0) {
                                DokcerHttpRunnerService.removeContainer(dockerClient, containerId);
                                //清理代码仓库
                                FileUtil.deleteFile(new File(HOST_PATH + testId));
                                FileUtil.deleteFile(new File(HOST_PATH + testId + ".zip"));
                                LogUtil.info("清理代码仓库完成");
                            }
                            // 上传结束消息
                            String[] contents = new String[]{reportId, "none", "0", "Remove container completed"};
                            String log = StringUtils.join(contents, " ");
                            //kafkaProducer.sendMessage(topic, log);
                            LogUtil.info("Remove container completed: " + containerId);
                        } catch (Exception e) {
                            LogUtil.error("Remove container error: ", e);
                        }
                        LogUtil.info("completed....");
                        //taskEndState(testTask.getTaskId(), String.valueOf(TaskStatus.SUCCESS));
                    }
                });

        dockerClient.logContainerCmd(containerId)
                .withFollowStream(true)
                .withStdOut(true)
                .withStdErr(true)
                .withTailAll()
                .exec(new InvocationBuilder.AsyncResultCallback<Frame>() {
                    @Override
                    public void onNext(Frame item) {
                        String log = new String(item.getPayload()).trim();
                        String oomMessage = "There is insufficient memory for the Java Runtime Environment to continue.";
                        if (StringUtils.contains(log, oomMessage)) {
                            // oom 退出
                            String[] contents = new String[]{reportId, "none", "0", oomMessage};
                            String message = StringUtils.join(contents, " ");
                            //kafkaProducer.sendMessage(topic, message);
                        }
                        LogUtil.info(log);
                    }
                });
    }

    private void checkContainerExists(DockerClient dockerClient, String testId) {
        List<Container> list = dockerClient.listContainersCmd()
                .withShowAll(true)
                .withStatusFilter(Arrays.asList("created", "restarting", "running", "paused", "exited"))
                .withNameFilter(Collections.singletonList(testId))
                .exec();
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(container -> DokcerHttpRunnerService.removeContainer(dockerClient, container.getId()));
        }
    }

    private void checkKafka(String bootstrapServers) {
        String[] servers = StringUtils.split(bootstrapServers, ",");
        try {
            for (String s : servers) {
                String[] ipAndPort = s.split(":");
                //1,建立tcp
                String ip = ipAndPort[0];
                int port = Integer.parseInt(ipAndPort[1]);
                try (
                        Socket soc = new Socket()
                ) {
                    soc.connect(new InetSocketAddress(ip, port), 1000); // 1s timeout
                }
            }
        } catch (Exception e) {
            LogUtil.error(e);
            throw new RuntimeException("Failed to connect to Kafka");
        }
    }

    /**
     * 传入启动httprunner引擎需要的参数
     *
     * @param testTask
     * @return
     */
    private String[] getEnvs(TestTask testTask) {
        Map<String, String> env = new HashMap<>();
        env.put("kafka_bootstrap_servers", testTask.getKafkaDto().getBootstrapServers());
        env.put("kafka_topics_log", testTask.getKafkaDto().getLogTopic());
        env.put("task_id", testTask.getTaskId());
        env.put("debug_mode", testTask.getDebugMode());
        env.put("exec_case_paths", testTask.getExecCasePaths().replace(",",":"));
        env.put("data_callback_url", testTask.getDataCallbackUrl());
        env.put("dot_env_path", testTask.getDotEnvPath());
        env.put("test_dir", PATH);
        env.put("report_status_url", getReportTaskStatusUrl());
        return env.keySet().stream().map(k -> k + "=" + env.get(k)).toArray(String[]::new);
    }

    private void searchImage(DockerClient dockerClient, String imageName) {
        // image
        List<Image> imageList = dockerClient.listImagesCmd().exec();
        if (CollectionUtils.isEmpty(imageList)) {
            throw new RuntimeException("Image List is empty");
        }
        List<Image> collect = imageList.stream().filter(image -> {
            String[] repoTags = image.getRepoTags();
            if (repoTags == null) {
                return false;
            }
            for (String repoTag : repoTags) {
                if (repoTag.equals(imageName)) {
                    return true;
                }
            }
            return false;
        }).collect(Collectors.toList());

        if (collect.size() == 0) {
            throw new RuntimeException("Image Not Found: " + imageName);
        }
    }

    public void stopContainer(String testId) {
        LogUtil.info("Receive stop container request, test: {}", testId);
        DockerClient dockerClient = DokcerHttpRunnerService.connectDocker();

        // container filter
        List<Container> list = dockerClient.listContainersCmd()
                .withShowAll(true)
                .withStatusFilter(Collections.singletonList("running"))
                .withNameFilter(Collections.singletonList(testId))
                .exec();
        // container stop
        list.forEach(container -> DokcerHttpRunnerService.removeContainer(dockerClient, container.getId()));
    }

    public List<Container> taskStatus(String testId) {
        DockerClient dockerClient = DokcerHttpRunnerService.connectDocker();
        List<Container> containerList = dockerClient.listContainersCmd()
                .withStatusFilter(Arrays.asList("created", "restarting", "running", "paused", "exited"))
                .withNameFilter(Collections.singletonList(testId))
                .exec();
        // 查询执行的状态
        return containerList;
    }

    public String logContainer(String testId) {
        LogUtil.info("Receive logs container request, test: {}", testId);
        DockerClient dockerClient = DokcerHttpRunnerService.connectDocker();

        // container filter
        List<Container> list = dockerClient.listContainersCmd()
                .withShowAll(true)
                .withStatusFilter(Collections.singletonList("running"))
                .withNameFilter(Collections.singletonList(testId))
                .exec();

        StringBuilder sb = new StringBuilder();
        if (list.size() > 0) {
            try {
                dockerClient.logContainerCmd(list.get(0).getId())
                        .withFollowStream(true)
                        .withStdOut(true)
                        .withStdErr(true)
                        .withTailAll()
                        .exec(new InvocationBuilder.AsyncResultCallback<Frame>() {
                            @Override
                            public void onNext(Frame item) {
                                sb.append(new String(item.getPayload()).trim()).append("\n");
                            }
                        }).awaitCompletion(100, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                LogUtil.error(e);
            }
        }
        return sb.toString();
    }

    /**
     * 上报任务结束状态
     * @param taskId    任务id
     * @param taskStatus 任务的状态 SUCCESS/执行成功 FAILURE/执行失败 ERROR/7-平台失败
     * @return
     */
    private void taskEndState(String taskId, String taskStatus){
        JSONObject object = new JSONObject();
        object.put("id", taskId);
        object.put("taskStatus", taskStatus);
        String params = object.toString();
        QHttpResponse qHttpResponse = HttpUtil.postJson(String.format("%s"+TASK_END_URL,address),params,null);
        if(qHttpResponse != null){
            String msg = String.valueOf(qHttpResponse.getJsonNode().get("message"));
            LogUtil.info("通知服务器任务结束，服务器返回消息：" + msg);
        }else {
            MSException.throwException("通知服务器任务接收接口出现异常");
        }
    }

    private String getReportTaskStatusUrl(){
        return String.format("%s/httpRunnerTask/httpRunnerTaskResultStatus",address);
    }

}
