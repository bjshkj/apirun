package io.apirun.controller;

import io.apirun.commons.utils.SessionUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class IndexController {

    @GetMapping(value = "/")
    public String index() {
        return "index.html";
    }

    @GetMapping(value = "/login")
    public String login() {
        if (SessionUtils.getUser() == null) {
            return "login.html";
        } else {
            return "redirect:/";
        }
    }

    @GetMapping(value = "/document")
    public String document() {
        return "document.html";
    }

    /**
     * 对流水线增加查看jmeter任务报告页面
     * @return
     */
    @GetMapping(value = "/jmeter")
    public String jmeter() {
        return "apiReportTemplate.html";
    }

    /**
     * 对流水线增加查看httpRunner任务报告页面
     * @return
     */
    @GetMapping(value = "/httpRunner")
    public String httpRunner() {
        return "httpRunner.html";
    }
}
