package io.apirun.assemblyline.service;

import io.apirun.assemblyline.constants.AsseLineStatus;
import io.apirun.assemblyline.dto.HttpRunnerDto;
import io.apirun.base.domain.HttprunnerTask;
import io.apirun.commons.utils.IPUtil;
import io.apirun.commons.utils.LogUtil;
import io.apirun.assemblyline.dto.GeelibRequest;
import io.apirun.gitlab.models.GitlabBranch;
import io.apirun.gitlab.models.GitlabCommit;
import io.apirun.gitlab.models.GitlabRepositoryTree;
import io.apirun.gitlab.service.GitLabService;
import io.apirun.httpruner.dto.HttpRunnerTaskStatus;
import io.apirun.httpruner.dto.HttprunnerTaskSummaryDTO;
import io.apirun.httpruner.dto.TestCaseStatInfo;
import io.apirun.httpruner.service.HttpRunnerResultService;
import io.apirun.httpruner.service.HttpRunnerService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class GeelibGttpRunnerService {

    @Value("${server.port}")
    private String serverPort;

    @Resource
    private GitLabService gitLabService;
    @Resource
    private HttpRunnerService httpRunnerService;
    @Resource
    private HttpRunnerResultService httpRunnerResultService;
    @Resource
    private Environment environment;

    final String REPORT_SHOW_URL = "/httpRunner?reportId=";
    final String BASE_URL = "";

    /**
     * 极库云流水线校验 执行httprunner任务所需参数是否正确
     *
     * @param geelibRequest
     * @return
     */
    public HttpRunnerDto CalibrationParameters(GeelibRequest geelibRequest) {
        HttpRunnerDto dto = new HttpRunnerDto();
        if (!StringUtils.isNotBlank(geelibRequest.getName())) {
            dto.setMsg("任务名为空,请重新输入");
            return dto;
        }
        if (!StringUtils.isNotBlank(geelibRequest.getId())) {
            dto.setMsg("项目id为空,请重新输入");
            return dto;
        }
        if (!StringUtils.isNotBlank(geelibRequest.getBranch())) {
            dto.setMsg("项目分支为空,请重新输入");
            return dto;
        }
        if (!StringUtils.isNotBlank(geelibRequest.getCommit())) {
            dto.setMsg("commit为空,请重新输入");
            return dto;
        }
        if (!StringUtils.isNotBlank(geelibRequest.getExecPath())) {
            dto.setMsg("执行路径为空,请重新输入");
            return dto;
        }
        if (!StringUtils.isNotBlank(geelibRequest.getEnvPath())) {
            dto.setMsg("env文件路径为空,请重新输入");
            return dto;
        }
        if (!StringUtils.isNotBlank(geelibRequest.getHostsPath())) {
            dto.setMsg("hosts文件路径为空,请重新输入");
            return dto;
        }

        List<GitlabBranch> branchList = gitLabService.getBranches(geelibRequest.getId());
        if (branchList.stream().noneMatch(gitlabBranch -> gitlabBranch.getName().equals(geelibRequest.getBranch()))) {
            dto.setMsg("该分支不存在,请重新输入");
            return dto;
        }
        List<GitlabCommit> commitList = gitLabService.getCommitInfo(geelibRequest.getId(), "1", "10", geelibRequest.getBranch());
        if (commitList.stream().noneMatch(gitlabCommit -> gitlabCommit.getShortId().equals(geelibRequest.getCommit()))) {
            dto.setMsg("该commit不存在,请重新输入");
            return dto;
        }
        //执行路径支持多路径，每个路径用逗号隔开
        String[] execPath = geelibRequest.getExecPath().split(",");
        //判断执行路径是否存在
        for (String s : execPath) {
            StringBuilder path = new StringBuilder();
            String[] paths = s.split("/");
            for (int j = 0; j < paths.length - 1; j++) {
                path.append(paths[j]);
                if (j < paths.length - 2) {
                    path.append("/");
                }
            }
            List<GitlabRepositoryTree> repositoryTreeList = gitLabService.getRepoTree(geelibRequest.getId(), path.toString(), geelibRequest.getBranch());
            if (repositoryTreeList.stream().
                    noneMatch(gitlabRepositoryTree ->
                            gitlabRepositoryTree.getName().equals(paths[paths.length - 1]))) {
                dto.setMsg("执行路径不存在,请重新输入执行路径");
                return dto;
            }
        }
        StringBuilder envPath = new StringBuilder();
        String[] envs = geelibRequest.getEnvPath().split("/");
        for (int i = 0; i < envs.length -1; i++){
            envPath.append(envs[i]);
            if (i < envs.length - 2){
                envPath.append("/");
            }
        }
        StringBuilder hostsPath = new StringBuilder();
        String[] hosts = geelibRequest.getHostsPath().split("/");
        for (int i = 0; i < hosts.length -1; i++){
            hostsPath.append(hosts[i]);
            if (i < hosts.length - 2){
                hostsPath.append("/");
            }
        }
        //判断hosts和env文件路径是否存在
        List<GitlabRepositoryTree> envList = gitLabService.getRepoTree(geelibRequest.getId(), envPath.toString(), geelibRequest.getCommit());
        if (envList.stream().noneMatch(env -> env.getName().equals(envs[envs.length-1]))){
            dto.setMsg("env路径不存在,请重新输入env路径");
            return dto;
        }
        List<GitlabRepositoryTree> hostsList = gitLabService.getRepoTree(geelibRequest.getId(), hostsPath.toString(), geelibRequest.getCommit());
        if (hostsList.stream().noneMatch(gitlabRepositoryTree -> gitlabRepositoryTree.getName().equals(hosts[hosts.length-1]))){
            dto.setMsg("hosts路径不存在,请重新输入hosts路径");
            return dto;
        }
        LogUtil.info("流水线执行httpruner任务：" + geelibRequest);
        dto.setStatus(true);
        return dto;
    }

    /**
     * 极库云流水线，查询任务执行状态，返回执行结果
     * @param taskId
     * @return
     */
    public TestCaseStatInfo getTaskStatus(String taskId){
        TestCaseStatInfo statInfo = new TestCaseStatInfo();
        HttprunnerTask httpRunner = httpRunnerService.getHttprunnerTask(taskId);
        if (httpRunner == null){
            statInfo.setMessage("没有找到该任务，请检查任务id是否正确");
            return statInfo;
        }
        //如果任务状态为DONE，则获取项目执行情况并跳出循环
        if (httpRunner.getTaskStatus().equals(HttpRunnerTaskStatus.DONE.toString())){
            HttprunnerTaskSummaryDTO info = httpRunnerResultService.getSummaryInfo(taskId);
            statInfo.setTotal(info.getTcasesTotal());
            statInfo.setSuccess(info.getTcasesSuccess());
            statInfo.setFailures(info.getTcasesFail());
            if (info.getTcasesFail() == 0){
                statInfo.setStatus(AsseLineStatus.SUCCESS);
            }else {
                statInfo.setStatus(AsseLineStatus.PARTIAL_SUCCESS);
            }
            getEnv(taskId, statInfo, httpRunner.getTaskStatus());
        }
        //如果任务状态为ERROR、TIMEOUT,返回异常
        if (httpRunner.getTaskStatus().equals(HttpRunnerTaskStatus.ERROR.toString()) ||
                httpRunner.getTaskStatus().equals(HttpRunnerTaskStatus.TIMEOUT.toString())){
            statInfo.setMessage("任务执行异常，请前往接口测试平台查看详情");
            getEnv(taskId, statInfo, httpRunner.getTaskStatus());
            statInfo.setStatus(AsseLineStatus.FAILURE);
        }
        //如果任务状态为CREATED、SENDED、EXECUTING、QUEUE
        if (httpRunner.getTaskStatus().equals(HttpRunnerTaskStatus.CREATED.toString()) ||
                httpRunner.getTaskStatus().equals(HttpRunnerTaskStatus.SENDED.toString()) ||
                httpRunner.getTaskStatus().equals(HttpRunnerTaskStatus.EXECUTING.toString()) ||
                httpRunner.getTaskStatus().equals(HttpRunnerTaskStatus.QUEUE.toString())){
            statInfo.setMessage("任务执行中");
            statInfo.setStatus(AsseLineStatus.EXECUTING);
            return statInfo;
        }
        return statInfo;
    }

    /**
     * 获取当前环境是生产还是测试，测试返回ip，生产返回域名
     * @param taskId
     * @param statInfo
     * @param status
     */
    private void getEnv(String taskId, TestCaseStatInfo statInfo, String status) {
        //获取当前环境是生产还是测试
        String[] activeProfiles = environment.getActiveProfiles();
        for (String profile : activeProfiles) {
            if ("dev".equals(profile) || "test".equals(profile)) {
                statInfo.setUrl(String.format("http://%s:%s%s%s", IPUtil.getLocalIP(), serverPort, REPORT_SHOW_URL, taskId));
                break;
            } else if ("prod".equals(profile)) {
                statInfo.setUrl(String.format("%s%s%s", BASE_URL, REPORT_SHOW_URL, taskId));
                break;
            }
        }
    }
}
