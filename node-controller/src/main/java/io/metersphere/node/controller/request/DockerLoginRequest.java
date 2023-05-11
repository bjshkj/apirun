package io.metersphere.node.controller.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DockerLoginRequest {
    private String username;
    private String password;
    private String registry;
}
