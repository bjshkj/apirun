package io.apirun.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.apirun.base.domain.CustomField;
import io.apirun.commons.constants.OperLogConstants;
import io.apirun.commons.utils.PageUtils;
import io.apirun.commons.utils.Pager;
import io.apirun.controller.request.QueryCustomFieldRequest;
import io.apirun.log.annotation.MsAuditLog;
import io.apirun.service.CustomFieldService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RequestMapping("custom/field")
@RestController

public class CustomFieldController {

    @Resource
    private CustomFieldService customFieldService;

    @PostMapping("/add")
    @MsAuditLog(module = "workspace_template_settings", type = OperLogConstants.CREATE, content = "#msClass.getLogDetails(#customField.id)", msClass = CustomFieldService.class)
    public String add(@RequestBody CustomField customField) {
        return customFieldService.add(customField);
    }

    @PostMapping("/list/{goPage}/{pageSize}")
    public Pager<List<CustomField>> list(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody QueryCustomFieldRequest request) {
        Page<List<CustomField>> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, customFieldService.list(request));
    }

    @PostMapping("/list/relate/{goPage}/{pageSize}")
    public Pager<List<CustomField>> listRelate(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody QueryCustomFieldRequest request) {
        return customFieldService.listRelate(goPage, pageSize, request);
    }

    @GetMapping("/delete/{id}")
    @MsAuditLog(module = "workspace_template_settings", type = OperLogConstants.DELETE, beforeEvent = "#msClass.getLogDetails(#id)", msClass = CustomFieldService.class)
    public void delete(@PathVariable(value = "id") String id) {
        customFieldService.delete(id);
    }

    @PostMapping("/update")
    @MsAuditLog(module = "workspace_template_settings", type = OperLogConstants.UPDATE, beforeEvent = "#msClass.getLogDetails(#customField.id)", content = "#msClass.getLogDetails(#customField.id)", msClass = CustomFieldService.class)
    public void update(@RequestBody CustomField customField) {
        customFieldService.update(customField);
    }

    @PostMapping("/list/ids")
    public List<String> list(@RequestBody QueryCustomFieldRequest request) {
        return customFieldService.listIds(request);
    }

    @PostMapping("/list")
    public List<CustomField> getList(@RequestBody QueryCustomFieldRequest request) {
        return customFieldService.list(request);
    }

    @PostMapping("/default")
    public List<CustomField> getDefaultList(@RequestBody QueryCustomFieldRequest request) {
        return customFieldService.getDefaultField(request);
    }

}
