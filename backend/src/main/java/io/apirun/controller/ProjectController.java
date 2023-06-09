package io.apirun.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.apirun.api.service.ApiTestEnvironmentService;
import io.apirun.base.domain.FileMetadata;
import io.apirun.base.domain.Project;
import io.apirun.commons.constants.OperLogConstants;
import io.apirun.commons.utils.PageUtils;
import io.apirun.commons.utils.Pager;
import io.apirun.commons.utils.SessionUtils;
import io.apirun.controller.request.AddProjectRequest;
import io.apirun.controller.request.ProjectRequest;
import io.apirun.dto.ProjectDTO;
import io.apirun.dto.WorkspaceMemberDTO;
import io.apirun.log.annotation.MsAuditLog;
import io.apirun.service.ProjectService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping(value = "/project")
public class ProjectController {
    @Resource
    private ProjectService projectService;
    @Resource
    private ApiTestEnvironmentService apiTestEnvironmentService;

    @GetMapping("/listAll")
    public List<ProjectDTO> listAll() {
        String currentWorkspaceId = SessionUtils.getCurrentWorkspaceId();
        ProjectRequest request = new ProjectRequest();
        request.setWorkspaceId(currentWorkspaceId);
        return projectService.getProjectList(request);
    }

    /*jenkins项目列表*/
    @GetMapping("/listAll/{workspaceId}")
    public List<ProjectDTO> jlistAll(@PathVariable String workspaceId) {
        ProjectRequest request = new ProjectRequest();
        request.setWorkspaceId(workspaceId);
        return projectService.getProjectList(request);
    }

    /*极库云项目列表*/
    @GetMapping("/listAll/type")
    public List<ProjectDTO> geelibAll(String email,String projectType){
        return projectService.getProjects(email,projectType);
    }

    @GetMapping("/recent/{count}")
    public List<Project> recentProjects(@PathVariable int count) {
        String currentWorkspaceId = SessionUtils.getCurrentWorkspaceId();
        ProjectRequest request = new ProjectRequest();
        request.setWorkspaceId(currentWorkspaceId);
        // 最近 `count` 个项目
        PageHelper.startPage(1, count);
        return projectService.getRecentProjectList(request);
    }

    @GetMapping("/get/{id}")
    public Project getProject(@PathVariable String id) {
        return projectService.getProjectById(id);
    }

    @PostMapping("/add")
    @MsAuditLog(module = "project_project_manager", type = OperLogConstants.CREATE, content = "#msClass.getLogDetails(#project.id)", msClass = ProjectService.class)
    public Project addProject(@RequestBody AddProjectRequest project, HttpServletRequest request) {
        Project returnModel = projectService.addProject(project);

        //创建项目的时候默认增加Mock环境
        String requestUrl = request.getRequestURL().toString();
        String baseUrl = "";
        if (requestUrl.contains("/project/add")) {
            baseUrl = requestUrl.split("/project/add")[0];
        }
        apiTestEnvironmentService.getMockEnvironmentByProjectId(returnModel.getId(), project.getProtocal(), baseUrl);
        return returnModel;
    }

    @PostMapping("/list/{goPage}/{pageSize}")
    public Pager<List<ProjectDTO>> getProjectList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody ProjectRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, projectService.getProjectList(request));
    }

    /**
     * 切换项目
     *
     * @param request
     * @return
     */
    @PostMapping("/list/related")
    public List<ProjectDTO> getSwitchProject(@RequestBody ProjectRequest request) {
        request.setWorkspaceId(SessionUtils.getCurrentWorkspaceId());
        return projectService.getSwitchProject(request);
    }

    @PostMapping("/list/related/httprunner")
    public List<ProjectDTO> getSwitchHttpRunnerProject(@RequestBody ProjectRequest request) {
        request.setWorkspaceId(SessionUtils.getCurrentWorkspaceId());
        return projectService.getSwitchHttpRunnerProject(request);
    }

    @GetMapping("/delete/{projectId}")
    @MsAuditLog(module = "project_project_manager", type = OperLogConstants.DELETE, beforeEvent = "#msClass.getLogDetails(#projectId)", msClass = ProjectService.class)
    public void deleteProject(@PathVariable(value = "projectId") String projectId) {
        //checkPermissionService.checkProjectOwner(projectId);
        projectService.deleteProject(projectId);
    }

    @PostMapping("/update")
    @MsAuditLog(module = "project_project_manager", type = OperLogConstants.UPDATE, beforeEvent = "#msClass.getLogDetails(#Project.id)", content = "#msClass.getLogDetails(#Project.id)", msClass = ProjectService.class)
    public void updateProject(@RequestBody Project Project) {
        projectService.updateProject(Project);
    }

    @PostMapping(value = "upload/files/{projectId}", consumes = {"multipart/form-data"})
    @MsAuditLog(module = "project_file_management", type = OperLogConstants.IMPORT, content = "#msClass.getLogDetails(#projectId)", msClass = ProjectService.class)
    public List<FileMetadata> uploadFiles(@PathVariable String projectId, @RequestPart(value = "file") List<MultipartFile> files) {
        return projectService.uploadFiles(projectId, files);
    }

    @PostMapping(value = "/update/file/{fileId}", consumes = {"multipart/form-data"})
    @MsAuditLog(module = "project_file_management", type = OperLogConstants.IMPORT, content = "#msClass.getLogDetails(#fileId)", msClass = ProjectService.class)
    public FileMetadata updateFile(@PathVariable String fileId, @RequestPart(value = "file") MultipartFile file) {
        return projectService.updateFile(fileId, file);
    }

    @GetMapping(value = "delete/file/{fileId}")
    @MsAuditLog(module = "project_project_manager", type = OperLogConstants.DELETE, beforeEvent = "#msClass.getLogDetails(#fileId)", msClass = ProjectService.class)
    public void deleteFile(@PathVariable String fileId) {
        projectService.deleteFile(fileId);
    }

    @PostMapping("/member/update")
    @MsAuditLog(module = "project_project_member", type = OperLogConstants.UPDATE, beforeEvent = "#msClass.getLogDetails(#memberDTO)", content = "#msClass.getLogDetails(#memberDTO)", msClass = ProjectService.class)
    public void updateMember(@RequestBody WorkspaceMemberDTO memberDTO) {
        projectService.updateMember(memberDTO);
    }
}
