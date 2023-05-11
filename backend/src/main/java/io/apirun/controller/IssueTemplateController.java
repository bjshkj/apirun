package io.apirun.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.apirun.base.domain.IssueTemplate;
import io.apirun.commons.constants.OperLogConstants;
import io.apirun.commons.utils.PageUtils;
import io.apirun.commons.utils.Pager;
import io.apirun.controller.request.BaseQueryRequest;
import io.apirun.controller.request.UpdateIssueTemplateRequest;
import io.apirun.dto.IssueTemplateDao;
import io.apirun.log.annotation.MsAuditLog;
import io.apirun.service.IssueTemplateService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RequestMapping("field/template/issue")
@RestController

public class IssueTemplateController {
    @Resource
    private IssueTemplateService issueTemplateService;

    @PostMapping("/add")
    @MsAuditLog(module = "workspace_template_settings", type = OperLogConstants.CREATE, content = "#msClass.getLogDetails(#request.id)", msClass = IssueTemplateService.class)
    public void add(@RequestBody UpdateIssueTemplateRequest request) {
        issueTemplateService.add(request);
    }

    @PostMapping("/list/{goPage}/{pageSize}")
    public Pager<List<IssueTemplate>> list(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody BaseQueryRequest request) {
        Page<List<IssueTemplate>> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, issueTemplateService.list(request));
    }

    @GetMapping("/delete/{id}")
    @MsAuditLog(module = "workspace_template_settings", type = OperLogConstants.DELETE, beforeEvent = "#msClass.getLogDetails(#id)", msClass = IssueTemplateService.class)
    public void delete(@PathVariable(value = "id") String id) {
        issueTemplateService.delete(id);
    }

    @PostMapping("/update")
    @MsAuditLog(module = "workspace_template_settings", type = OperLogConstants.UPDATE, beforeEvent = "#msClass.getLogDetails(#request.id)", content = "#msClass.getLogDetails(#request.id)", msClass = IssueTemplateService.class)
    public void update(@RequestBody UpdateIssueTemplateRequest request) {
        issueTemplateService.update(request);
    }

    @GetMapping("/option/{workspaceId}")
    public List<IssueTemplate> list(@PathVariable String workspaceId) {
        return issueTemplateService.getOption(workspaceId);
    }

    @GetMapping("/get/relate/{projectId}")
    public IssueTemplateDao getTemplate(@PathVariable String projectId) {
        return issueTemplateService.getTemplate(projectId);
    }
}
