package io.apirun.track.service;

import com.alibaba.fastjson.JSON;
import io.apirun.base.domain.*;
import io.apirun.base.mapper.TestCaseCommentMapper;
import io.apirun.base.mapper.TestCaseMapper;
import io.apirun.base.mapper.TestCaseReviewMapper;
import io.apirun.base.mapper.UserMapper;
import io.apirun.base.mapper.ext.ExtTestCaseCommentMapper;
import io.apirun.commons.constants.NoticeConstants;
import io.apirun.commons.exception.MSException;
import io.apirun.commons.utils.SessionUtils;
import io.apirun.dto.BaseSystemConfigDTO;
import io.apirun.i18n.Translator;
import io.apirun.log.utils.ReflexObjectUtil;
import io.apirun.log.vo.DetailColumn;
import io.apirun.log.vo.OperatingLogDetails;
import io.apirun.log.vo.track.TestCaseReviewReference;
import io.apirun.notice.sender.NoticeModel;
import io.apirun.notice.service.NoticeSendService;
import io.apirun.service.SystemParameterService;
import io.apirun.track.dto.TestCaseCommentDTO;
import io.apirun.track.request.testreview.SaveCommentRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class TestCaseCommentService {
    @Resource
    TestCaseReviewMapper testCaseReviewMapper;
    @Resource
    private TestCaseCommentMapper testCaseCommentMapper;
    @Resource
    private TestCaseMapper testCaseMapper;
    @Resource
    private ExtTestCaseCommentMapper extTestCaseCommentMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private NoticeSendService noticeSendService;
    @Resource
    private SystemParameterService systemParameterService;

    public void saveComment(SaveCommentRequest request) {
        TestCaseComment testCaseComment = new TestCaseComment();
        testCaseComment.setId(request.getId());
        testCaseComment.setAuthor(SessionUtils.getUser().getId());
        testCaseComment.setCaseId(request.getCaseId());
        testCaseComment.setCreateTime(System.currentTimeMillis());
        testCaseComment.setUpdateTime(System.currentTimeMillis());
        testCaseComment.setDescription(request.getDescription());
        testCaseCommentMapper.insert(testCaseComment);
        TestCaseWithBLOBs testCaseWithBLOBs;
        testCaseWithBLOBs = testCaseMapper.selectByPrimaryKey(request.getCaseId());

        // 发送通知
        User user = userMapper.selectByPrimaryKey(testCaseWithBLOBs.getMaintainer());
        BaseSystemConfigDTO baseSystemConfigDTO = systemParameterService.getBaseInfo();
        List<String> userIds = new ArrayList<>();
        userIds.add(testCaseWithBLOBs.getMaintainer());//用例维护人
        String context = getReviewContext(testCaseComment, testCaseWithBLOBs);
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("maintainer", user.getName());
        paramMap.put("testCaseName", testCaseWithBLOBs.getName());
        paramMap.put("description", request.getDescription());
        paramMap.put("url", baseSystemConfigDTO.getUrl());
        paramMap.put("id", request.getReviewId());
        NoticeModel noticeModel = NoticeModel.builder()
                .context(context)
                .relatedUsers(userIds)
                .subject(Translator.get("test_review_task_notice"))
                .mailTemplate("ReviewComments")
                .paramMap(paramMap)
                .event(NoticeConstants.Event.COMMENT)
                .build();
        noticeSendService.send(NoticeConstants.TaskType.REVIEW_TASK, noticeModel);
    }

    public List<TestCaseCommentDTO> getCaseComments(String caseId) {
        return extTestCaseCommentMapper.getCaseComments(caseId);
    }

    public void deleteCaseComment(String caseId) {
        TestCaseCommentExample testCaseCommentExample = new TestCaseCommentExample();
        testCaseCommentExample.createCriteria().andCaseIdEqualTo(caseId);
        testCaseCommentMapper.deleteByExample(testCaseCommentExample);
    }

    private String getReviewContext(TestCaseComment testCaseComment, TestCaseWithBLOBs testCaseWithBLOBs) {
        User user = userMapper.selectByPrimaryKey(testCaseComment.getAuthor());
        Long startTime = testCaseComment.getCreateTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String start = null;
        String sTime = String.valueOf(startTime);
        if (!sTime.equals("null")) {
            start = sdf.format(new Date(Long.parseLong(sTime)));
        }
        String context = "";
        context = "测试评审任务通知：" + user.getName() + "在" + start + "为" + "'" + testCaseWithBLOBs.getName() + "'" + "添加评论:" + testCaseComment.getDescription();
        return context;
    }

    public void delete(String commentId) {
        checkCommentOwner(commentId);
        testCaseCommentMapper.deleteByPrimaryKey(commentId);
    }

    public void edit(SaveCommentRequest request) {
        checkCommentOwner(request.getId());
        testCaseCommentMapper.updateByPrimaryKeySelective(request);
    }

    private void checkCommentOwner(String commentId) {
        TestCaseComment comment = testCaseCommentMapper.selectByPrimaryKey(commentId);
        if (!StringUtils.equals(comment.getAuthor(), SessionUtils.getUser().getId())) {
            MSException.throwException(Translator.get("check_owner_comment"));
        }
    }

    public String getLogDetails(String id) {
        TestCaseComment caseComment = testCaseCommentMapper.selectByPrimaryKey(id);
        if (caseComment != null) {
            TestCase review = testCaseMapper.selectByPrimaryKey(caseComment.getCaseId());
            List<DetailColumn> columns = ReflexObjectUtil.getColumns(caseComment, TestCaseReviewReference.commentReviewColumns);
            OperatingLogDetails details = new OperatingLogDetails(JSON.toJSONString(caseComment.getId()), review.getProjectId(), caseComment.getDescription(), caseComment.getAuthor(), columns);
            return JSON.toJSONString(details);
        }
        return null;
    }
}
