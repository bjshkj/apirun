package io.apirun.gitlab.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.apirun.gitlab.models.GitlabUser;

public class GitlabSession extends GitlabUser {

    public static final String URL = "/session";

    @JsonProperty("private_token")
    private String privateToken;

    @Override
    public String getPrivateToken() {
        return privateToken;
    }

    @Override
    public void setPrivateToken(String privateToken) {
        this.privateToken = privateToken;
    }

}
