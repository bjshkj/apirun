package io.apirun.controller;

import io.apirun.dto.StatisticsProjectRankingDTO;
import io.apirun.dto.StatisticsUserRankingDTO;
import io.apirun.dto.UserActivityChartDTO;
import io.apirun.service.ProjectService;
import io.apirun.service.UserService;
import io.apirun.service.WorkspaceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

@RequestMapping("statistics")
@RestController
public class StatisticsSummaryController {
    @Resource
    private UserService userService;
    @Resource
    private WorkspaceService workspaceService;
    @Resource
    private ProjectService projectService;

//    用户新增，活跃情况统计
    @GetMapping("/getUsersNewAddWithActivity/{begin_time}/{end_time}/{date_span}")
    public UserActivityChartDTO getUsersNewAddWithActivity(@PathVariable String begin_time, @PathVariable String end_time, @PathVariable String date_span) throws ParseException {
        return userService.getUserAddActivity(begin_time, end_time, date_span);
    }


    @GetMapping("/getWorkspaceUserActivity/{workspace_id}/{begin_time}/{end_time}")
    public List<StatisticsUserRankingDTO> getWorkspaceUserActivity(@PathVariable String workspace_id, @PathVariable String begin_time, @PathVariable String end_time){
        return workspaceService.getWorkspaceUserActivity(workspace_id,begin_time,end_time);
    }

    @GetMapping("/getProjectsActivityRanking/{begin_time}/{end_time}/{query_type}")
    public List<StatisticsProjectRankingDTO> getProjectsActivityRanking(@PathVariable String begin_time, @PathVariable String end_time, @PathVariable String query_type){
        return projectService.getProjectsActivityRanking(begin_time,end_time,query_type);
    }


}
