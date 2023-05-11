package io.apirun.gitlab.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.apirun.gitlab.models.GitlabAccessLevel;
import io.apirun.gitlab.models.GitlabUser;

public abstract class GitlabAbstractMember extends GitlabUser {

    public static final String URL = "/members";

    @JsonProperty("access_level")
    private int accessLevel;

    public GitlabAccessLevel getAccessLevel() {
        return GitlabAccessLevel.fromAccessValue(accessLevel);
    }

    public void setAccessLevel(GitlabAccessLevel accessLevel) {
        this.accessLevel = accessLevel.accessValue;
    }

}
