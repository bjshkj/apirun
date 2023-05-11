package io.apirun.track.dto;

import io.apirun.base.domain.TestCaseComment;
import lombok.Data;

@Data
public class TestCaseCommentDTO extends TestCaseComment {
    private String authorName;
}
