package io.apirun.api.dto.definition;

import io.apirun.track.dto.TreeNodeDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiModuleDTO extends TreeNodeDTO<ApiModuleDTO> {
    private String protocol;
}
