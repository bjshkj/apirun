package io.apirun.dto;

import io.apirun.base.domain.Group;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupDTO extends Group {
    private String scopeName;
}
