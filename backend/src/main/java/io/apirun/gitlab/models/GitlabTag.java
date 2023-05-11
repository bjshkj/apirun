package io.apirun.gitlab.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.apirun.gitlab.models.GitlabCommit;
import io.apirun.gitlab.models.GitlabRelease;

public class GitlabTag {

    public final static String URL = "/repository/tags";

    @JsonProperty("commit")
    private GitlabCommit commit;

    @JsonProperty("release")
    private GitlabRelease release;

    @JsonProperty("name")
    private String name;

    @JsonProperty("message")
    private String message;

    public GitlabCommit getCommit() {
        return commit;
    }

    public void setCommit(GitlabCommit commit) {
        this.commit = commit;
    }

    public GitlabRelease getRelease() {
        return release;
    }

    public void setRelease(GitlabRelease release) {
        this.release = release;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
