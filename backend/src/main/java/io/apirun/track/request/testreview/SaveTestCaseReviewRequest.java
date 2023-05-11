package io.apirun.track.request.testreview;

import io.apirun.base.domain.TestCaseReview;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SaveTestCaseReviewRequest extends TestCaseReview {
    private List<String> projectIds;
    private List<String> userIds;
}
