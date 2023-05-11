package io.metersphere.node.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DockerClientController {

    @GetMapping("/status")
    public String getStatus() {
        return "OK";
    }
}
