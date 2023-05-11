package io.apirun.track.request.testplancase;

import io.apirun.base.domain.TestCaseReviewTestCase;
import io.apirun.track.request.testreview.QueryCaseReviewCondition;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TestReviewCaseBatchRequest extends TestCaseReviewTestCase {
    private String reviewId;
    private List<String> ids;
    private QueryCaseReviewCondition condition;
}
