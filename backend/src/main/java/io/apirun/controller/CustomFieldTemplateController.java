package io.apirun.controller;

import io.apirun.base.domain.CustomFieldTemplate;
import io.apirun.dto.CustomFieldTemplateDao;
import io.apirun.service.CustomFieldTemplateService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("custom/field/template")
public class CustomFieldTemplateController {

    @Resource
    CustomFieldTemplateService customFieldTemplateService;

    @PostMapping("/list")
    public List<CustomFieldTemplateDao> list(@RequestBody CustomFieldTemplate request) {
        return customFieldTemplateService.list(request);
    }
}
