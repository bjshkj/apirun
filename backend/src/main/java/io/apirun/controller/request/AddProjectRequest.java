package io.apirun.controller.request;

import io.apirun.base.domain.Project;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddProjectRequest extends Project {
    private String protocal;
}
