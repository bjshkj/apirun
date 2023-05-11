package io.apirun.dto;

import io.apirun.base.domain.Project;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectDTO extends Project {
    private String workspaceName;
    private String createUserName;
}
