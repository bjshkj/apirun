package io.apirun.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.apirun.base.domain.TestCaseTemplate;
import io.apirun.base.domain.TestCaseTemplateWithBLOBs;
import io.apirun.commons.constants.OperLogConstants;
import io.apirun.commons.utils.PageUtils;
import io.apirun.commons.utils.Pager;
import io.apirun.controller.request.BaseQueryRequest;
import io.apirun.controller.request.UpdateCaseFieldTemplateRequest;
import io.apirun.dto.TestCaseTemplateDao;
import io.apirun.log.annotation.MsAuditLog;
import io.apirun.service.TestCaseTemplateService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RequestMapping("field/template/case")
@RestController

public class TestCaseTemplateController {

    @Resource
    private TestCaseTemplateService testCaseTemplateService;

    @PostMapping("/add")
    @MsAuditLog(module = "workspace_template_settings", type = OperLogConstants.CREATE, content = "#msClass.getLogDetails(#request.id)", msClass = TestCaseTemplateService.class)
    public void add(@RequestBody UpdateCaseFieldTemplateRequest request) {
        testCaseTemplateService.add(request);
    }

    @PostMapping("/list/{goPage}/{pageSize}")
    public Pager<List<TestCaseTemplateWithBLOBs>> list(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody BaseQueryRequest request) {
        Page<List<TestCaseTemplateWithBLOBs>> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, testCaseTemplateService.list(request));
    }

    @GetMapping("/delete/{id}")
    @MsAuditLog(module = "workspace_template_settings", type = OperLogConstants.DELETE, beforeEvent = "#msClass.getLogDetails(#id)", msClass = TestCaseTemplateService.class)
    public void delete(@PathVariable(value = "id") String id) {
        testCaseTemplateService.delete(id);
    }

    @PostMapping("/update")
    @MsAuditLog(module = "workspace_template_settings", type = OperLogConstants.UPDATE, beforeEvent = "#msClass.getLogDetails(#request.id)", content = "#msClass.getLogDetails(#request.id)", msClass = TestCaseTemplateService.class)
    public void update(@RequestBody UpdateCaseFieldTemplateRequest request) {
        testCaseTemplateService.update(request);
    }

    @GetMapping("/option/{workspaceId}")
    public List<TestCaseTemplate> list(@PathVariable String workspaceId) {
        return testCaseTemplateService.getOption(workspaceId);
    }

    @GetMapping("/get/relate/{projectId}")
    public TestCaseTemplateDao getTemplate(@PathVariable String projectId) {
        return testCaseTemplateService.getTemplate(projectId);
    }
}
