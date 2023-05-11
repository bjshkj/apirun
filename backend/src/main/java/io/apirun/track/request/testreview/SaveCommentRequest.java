package io.apirun.track.request.testreview;

import io.apirun.base.domain.TestCaseComment;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SaveCommentRequest extends TestCaseComment {
     private String reviewId;

}
