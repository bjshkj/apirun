package io.apirun.track.service;

import com.alibaba.fastjson.JSON;
import io.apirun.base.domain.TestCaseReportTemplate;
import io.apirun.base.domain.TestCaseReportTemplateExample;
import io.apirun.base.mapper.TestCaseReportTemplateMapper;
import io.apirun.commons.exception.MSException;
import io.apirun.commons.utils.SessionUtils;
import io.apirun.i18n.Translator;
import io.apirun.log.utils.ReflexObjectUtil;
import io.apirun.log.vo.DetailColumn;
import io.apirun.log.vo.OperatingLogDetails;
import io.apirun.log.vo.system.SystemReference;
import io.apirun.track.request.testCaseReport.QueryTemplateRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(rollbackFor = Exception.class)
public class TestCaseReportTemplateService {

    @Resource
    TestCaseReportTemplateMapper testCaseReportTemplateMapper;

    public List<TestCaseReportTemplate> listTestCaseReportTemplate(QueryTemplateRequest request) {
        TestCaseReportTemplateExample example = new TestCaseReportTemplateExample();
        TestCaseReportTemplateExample.Criteria criteria1 = example.createCriteria();
        TestCaseReportTemplateExample.Criteria criteria2 = example.createCriteria();
        if (StringUtils.isNotBlank(request.getName())) {
            criteria1.andNameLike("%" + request.getName() + "%");
            criteria2.andNameLike("%" + request.getName() + "%");
        }
        if (StringUtils.isNotBlank(request.getWorkspaceId())) {
            criteria1.andWorkspaceIdEqualTo(request.getWorkspaceId());
        }
        if (request.getQueryDefault() != null) {
            criteria2.andWorkspaceIdIsNull();
            example.or(criteria2);
        }
        return testCaseReportTemplateMapper.selectByExample(example);
    }

    public TestCaseReportTemplate getTestCaseReportTemplate(String id) {
        return testCaseReportTemplateMapper.selectByPrimaryKey(id);
    }

    public void addTestCaseReportTemplate(TestCaseReportTemplate testCaseReportTemplate) {
        testCaseReportTemplate.setId(UUID.randomUUID().toString());
        checkCaseReportTemplateExist(testCaseReportTemplate);
        testCaseReportTemplateMapper.insert(testCaseReportTemplate);
    }

    public void editTestCaseReportTemplate(TestCaseReportTemplate testCaseReportTemplate) {
        checkCaseReportTemplateExist(testCaseReportTemplate);
        testCaseReportTemplateMapper.updateByPrimaryKeyWithBLOBs(testCaseReportTemplate);
    }

    private void checkCaseReportTemplateExist(TestCaseReportTemplate testCaseReportTemplate) {
        TestCaseReportTemplateExample example = new TestCaseReportTemplateExample();
        example.createCriteria()
                .andNameEqualTo(testCaseReportTemplate.getName())
                .andWorkspaceIdEqualTo(SessionUtils.getCurrentWorkspaceId())
                .andIdNotEqualTo(testCaseReportTemplate.getId());
        if (testCaseReportTemplateMapper.selectByExample(example).size() > 0) {
            MSException.throwException(Translator.get("test_case_report_template_repeat"));
        }
    }

    public int deleteTestCaseReportTemplate(String id) {
        return testCaseReportTemplateMapper.deleteByPrimaryKey(id);
    }

    public String getLogDetails(String id) {
        TestCaseReportTemplate templateWithBLOBs = testCaseReportTemplateMapper.selectByPrimaryKey(id);
        if (templateWithBLOBs != null) {
            List<DetailColumn> columns = ReflexObjectUtil.getColumns(templateWithBLOBs, SystemReference.issueFieldColumns);
            OperatingLogDetails details = new OperatingLogDetails(JSON.toJSONString(templateWithBLOBs.getId()), null, templateWithBLOBs.getName(), templateWithBLOBs.getCreateUser(), columns);
            return JSON.toJSONString(details);
        }
        return null;
    }
}
