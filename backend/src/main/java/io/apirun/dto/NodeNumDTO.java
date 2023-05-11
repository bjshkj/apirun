package io.apirun.dto;

import io.apirun.base.domain.TestCaseNode;
import lombok.Data;

@Data
public class NodeNumDTO extends TestCaseNode {
    private Integer caseNum;
}
