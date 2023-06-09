package io.apirun.track.dto;

import io.apirun.base.domain.TestCaseWithBLOBs;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class TestCaseDTO extends TestCaseWithBLOBs {

    private String maintainerName;
    private String apiName;
    private String performName;
    private String lastResultId;
    private String projectName;

    private List<String> caseTags = new ArrayList<>();
}
