package io.apirun.track.controller;

import io.apirun.base.domain.TestCaseReportTemplate;
import io.apirun.commons.constants.OperLogConstants;
import io.apirun.log.annotation.MsAuditLog;
import io.apirun.track.request.testCaseReport.QueryTemplateRequest;
import io.apirun.track.service.TestCaseReportTemplateService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RequestMapping("/case/report/template")
@RestController
public class TestCaseReportTemplateController {

    @Resource
    TestCaseReportTemplateService testCaseReportTemplateService;

    @PostMapping("/list")
    public List<TestCaseReportTemplate> list(@RequestBody QueryTemplateRequest request) {
        return testCaseReportTemplateService.listTestCaseReportTemplate(request);
    }

    @GetMapping("/get/{id}")
    public TestCaseReportTemplate get(@PathVariable String id) {
        return testCaseReportTemplateService.getTestCaseReportTemplate(id);
    }

    @PostMapping("/add")
    @MsAuditLog(module = "workspace_template_settings", type = OperLogConstants.CREATE, title = "#testCaseReportTemplate.name",sourceId = "#testCaseReportTemplate.id")
    public void add(@RequestBody TestCaseReportTemplate testCaseReportTemplate) {
        testCaseReportTemplateService.addTestCaseReportTemplate(testCaseReportTemplate);
    }

    @PostMapping("/edit")
    @MsAuditLog(module = "workspace_template_settings", type = OperLogConstants.UPDATE, title = "#testCaseReportTemplate.name",sourceId = "#testCaseReportTemplate.id")
    public void edit(@RequestBody TestCaseReportTemplate testCaseReportTemplate) {
        testCaseReportTemplateService.editTestCaseReportTemplate(testCaseReportTemplate);
    }

    @PostMapping("/delete/{id}")
    @MsAuditLog(module = "workspace_template_settings", type = OperLogConstants.DELETE, beforeEvent = "#msClass.getLogDetails(#id)", msClass = TestCaseReportTemplateService.class)
    public int delete(@PathVariable String id) {
        return testCaseReportTemplateService.deleteTestCaseReportTemplate(id);
    }

}
