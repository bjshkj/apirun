package io.metersphere.node.util;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import io.metersphere.node.controller.request.DockerLoginRequest;
import org.apache.commons.lang.StringUtils;

import java.util.Collections;
import java.util.List;

public class DockerClientService {

    /**
     * 连接docker服务器
     *
     * @return
     */
    public static DockerClient connectDocker() {
        return DockerClientBuilder.getInstance().build();
    }

    public static DockerClient connectDocker(DockerLoginRequest request) {
        if (StringUtils.isBlank(request.getRegistry()) || StringUtils.isBlank(request.getUsername()) || StringUtils.isBlank(request.getPassword())) {
            return connectDocker();
        }
        DefaultDockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withRegistryUrl(request.getRegistry())
                .withRegistryUsername(request.getUsername())
                .withRegistryPassword(request.getPassword())
                .build();
        return DockerClientBuilder.getInstance(config).build();
    }

    /**
     * 创建容器
     *
     * @param client
     * @return
     */
    public static CreateContainerResponse createContainers(DockerClient client, String containerName, String imageName,
                                                           HostConfig hostConfig, String... env) {
        CreateContainerResponse container = client.createContainerCmd(imageName)
                .withName(containerName)
                .withHostConfig(hostConfig)
                .withEnv(env)
                .exec();
        return container;
    }


    /**
     * 启动容器
     *
     * @param client
     * @param containerId
     */
    public static void startContainer(DockerClient client, String containerId) {
        client.startContainerCmd(containerId).exec();
    }

    /**
     * 停止容器
     *
     * @param client
     * @param containerId
     */
    public static void stopContainer(DockerClient client, String containerId) {
        client.stopContainerCmd(containerId).exec();
    }

    /**
     * 删除容器
     *
     * @param client
     * @param containerId
     */
    public static void removeContainer(DockerClient client, String containerId) {
        client.removeContainerCmd(containerId)
                .withForce(true)
                .withRemoveVolumes(true)
                .exec();
    }

    /**
     * 容器是否存在
     *
     * @param client
     * @param containerId
     */
    public static int existContainer(DockerClient client, String containerId) {
        List<Container> list = client.listContainersCmd()
                .withShowAll(true)
                .withIdFilter(Collections.singleton(containerId))
                .exec();
        return list.size();
    }

}
