package io.apirun.gitlab.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.apirun.gitlab.models.GitlabProjectAccessLevel;

public class GitlabPermission {

    @JsonProperty("project_access")
    private GitlabProjectAccessLevel projectAccess;

    @JsonProperty("group_access")
    private GitlabProjectAccessLevel groupAccess;

    public GitlabProjectAccessLevel getProjectAccess() {
        return projectAccess;
    }

    public GitlabProjectAccessLevel getProjectGroupAccess() {
        return groupAccess;
    }
}
