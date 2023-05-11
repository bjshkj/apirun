package io.apirun.track.request.testcase;

import io.apirun.base.domain.TestCaseNode;
import io.apirun.track.dto.TestCaseNodeDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DragNodeRequest extends TestCaseNode {

    List<String> nodeIds;
    TestCaseNodeDTO nodeTree;
}
