package io.apirun.track.request.testcase;

import io.apirun.controller.request.BaseQueryRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class IssuesRequest extends BaseQueryRequest {
    private String title;
    private String content;
    private String projectId;
    private String testCaseId;
    private List<String> tapdUsers;
    /**
     * zentao bug 处理人
     */
    private String zentaoUser;
    /**
     * zentao bug 影响版本
     */
    private List<String> zentaoBuilds;

    /**
     * issues id
     */
    private String id;
    private String caseId;
    private String platform;
    private String customFields;
    private List<String> testCaseIds;
}
