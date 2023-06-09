package io.apirun.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.apirun.api.service.ApiAutomationService;
import io.apirun.base.domain.Schedule;
import io.apirun.controller.request.QueryScheduleRequest;
import io.apirun.controller.request.ScheduleRequest;
import io.apirun.dto.ScheduleDao;
import io.apirun.service.ScheduleService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RequestMapping("schedule")
@RestController
public class ScheduleController {
    @Resource
    private ScheduleService scheduleService;
    @Resource
    private ApiAutomationService apiAutomationService;

    @PostMapping("/list/{goPage}/{pageSize}")
    public List<ScheduleDao> list(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody QueryScheduleRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return scheduleService.list(request);
    }

    @GetMapping("/findOne/{testId}/{group}")
    public Schedule schedule(@PathVariable String testId,@PathVariable String group) {
        Schedule schedule = scheduleService.getScheduleByResource(testId,group);
        return schedule;
    }

    @PostMapping(value = "/update")
    public void updateSchedule(@RequestBody Schedule request) {
        scheduleService.updateSchedule(request);
    }

    @PostMapping(value = "/create")
    public void createSchedule(@RequestBody ScheduleRequest request) {
        scheduleService.createSchedule(request);
    }

    @GetMapping(value = "/getTaskInfo")
    public Object getTaskInfo() {
        return scheduleService.getCurrentlyExecutingJobs();
    }
}
