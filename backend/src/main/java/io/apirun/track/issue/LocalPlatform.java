package io.apirun.track.issue;

import io.apirun.base.domain.IssuesDao;
import io.apirun.base.domain.IssuesWithBLOBs;
import io.apirun.commons.constants.IssuesManagePlatform;
import io.apirun.commons.user.SessionUser;
import io.apirun.commons.utils.BeanUtils;
import io.apirun.commons.utils.SessionUtils;
import io.apirun.track.dto.DemandDTO;
import io.apirun.track.issue.domain.PlatformUser;
import io.apirun.track.request.testcase.IssuesRequest;
import io.apirun.track.request.testcase.IssuesUpdateRequest;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.UUID;

public class LocalPlatform extends AbstractIssuePlatform {

    protected String key = IssuesManagePlatform.Local.toString();

    public LocalPlatform(IssuesRequest issuesRequest) {
        super(issuesRequest);
    }

    @Override
    public List<IssuesDao> getIssue(IssuesRequest issuesRequest) {
        String projectId = issuesRequest.getProjectId();
        issuesRequest.setPlatform(IssuesManagePlatform.Local.toString());
        if (StringUtils.isNotBlank(projectId)) {
            return extIssuesMapper.getIssuesByProjectId(issuesRequest);
        }
        return extIssuesMapper.getIssuesByCaseId(issuesRequest);
    }

    @Override
    public void filter(List<IssuesDao> issues) {
    }

    @Override
    public List<DemandDTO> getDemandList(String projectId) {
        return null;
    }

    @Override
    public void addIssue(IssuesUpdateRequest issuesRequest) {
        SessionUser user = SessionUtils.getUser();
        String id = UUID.randomUUID().toString();
        IssuesWithBLOBs issues = new IssuesWithBLOBs();
        BeanUtils.copyBean(issues, issuesRequest);
        issues.setId(id);
        issues.setStatus("new");
        issues.setReporter(user.getId());
        issues.setCreateTime(System.currentTimeMillis());
        issues.setUpdateTime(System.currentTimeMillis());
        issues.setPlatform(IssuesManagePlatform.Local.toString());;
        issues.setNum(getNextNum(issuesRequest.getProjectId()));
        issuesMapper.insert(issues);

        issuesRequest.setId(id);
        handleTestCaseIssues(issuesRequest);
    }

    @Override
    public void updateIssue(IssuesUpdateRequest request) {
        handleIssueUpdate(request);
    }

    @Override
    public void deleteIssue(String id) {
        issuesMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void testAuth() {

    }

    @Override
    public List<PlatformUser> getPlatformUser() {
        return null;
    }

    @Override
    String getProjectId(String projectId) {
        return null;
    }

    public void closeIssue(String issueId) {
        IssuesWithBLOBs issues = new IssuesWithBLOBs();
        issues.setId(issueId);
        issues.setStatus("closed");
        issuesMapper.updateByPrimaryKeySelective(issues);
    }
}
