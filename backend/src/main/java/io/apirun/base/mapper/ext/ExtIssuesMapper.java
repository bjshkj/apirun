package io.apirun.base.mapper.ext;

import io.apirun.base.domain.Issues;
import io.apirun.base.domain.IssuesDao;
import io.apirun.track.request.testcase.IssuesRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtIssuesMapper {

    List<IssuesDao> getIssuesByCaseId(@Param("request") IssuesRequest issuesRequest);

    List<IssuesDao> getIssuesByProjectId(@Param("request") IssuesRequest issuesRequest);

    List<IssuesDao> getRelateIssues(@Param("request") IssuesRequest request);

    Issues getNextNum(String projectId);
}
