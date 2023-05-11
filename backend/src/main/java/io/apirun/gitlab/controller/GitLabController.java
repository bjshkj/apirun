package io.apirun.gitlab.controller;

import io.apirun.commons.exception.MSException;
import io.apirun.commons.utils.LogUtil;

import io.apirun.gitlab.models.*;
import io.apirun.gitlab.service.GitLabService;
import io.apirun.security.GuestAccess;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/gitLab")
public class GitLabController {

    @Resource
    GitLabService gitLabService;

    /**
     * 获取自己的项目列表
     * @return
     */
    @GetMapping("/project")
    public GitlabProject getProject(String id){
        return gitLabService.getProject(id);
    }


    @GetMapping("/repoTree")
    public List<GitlabRepositoryTree> getRepoTree(String id, String path,  String ref){
        if(id == null || id.trim().isEmpty()){
            LogUtil.error("当前项目id为空");
            MSException.throwException("当前项目id为空");
        }
        List<GitlabRepositoryTree> ans = new ArrayList<>();
        List<GitlabRepositoryTree> list = gitLabService.getRepoTree(id,path,ref);
        for (GitlabRepositoryTree tree : list){
            tree.setPath(path.trim().isEmpty() ? tree.getName() : path+"/" + tree.getName());
            ans.add(tree);
        }
        return ans;
//        return gitLabService.getRepoTree(path,ref);
    }

    @GetMapping("/repoFileContent")
    public GitlabRepositoryFile getRepoFileContent(String id,  String path,  String ref){
        if(id == null || id.trim().isEmpty()){
            LogUtil.error("当前项目id为空");
            MSException.throwException("当前项目id为空");
        }
        if(path == null || path.trim().isEmpty()){
            LogUtil.error("文件路径path为空");
            MSException.throwException("文件路径path为空");
        }

        if(ref == null || ref.trim().isEmpty()){
            LogUtil.error("仓库分支ref为空");
            MSException.throwException("仓库分支ref为空");
        }
        return gitLabService.getRepoFileContent(id,path,ref);
    }


    @GetMapping("/branch")
    public List<GitlabBranch> getBranches( String id){
        if(id == null || id.trim().isEmpty()){
            LogUtil.error("项目id为空");
            MSException.throwException("项目id为空");
        }
        return gitLabService.getBranches(id);
    }

    @GetMapping("/commitList")
    public List<GitlabCommit> getBranches(String id,String ref,String page, String per_page){
        if(id == null || id.trim().isEmpty()){
            LogUtil.error("项目id为空");
            MSException.throwException("项目id为空");
        }
        if(ref == null || ref.trim().isEmpty()){
            LogUtil.error("项目分支ref为空");
            MSException.throwException("项目分支ref为空");
        }
        return gitLabService.getCommitInfo(id,page,per_page,ref);
    }

    @GuestAccess
    @GetMapping("/getFileArchive")
    public byte[] getFileArchive(String id, String sha){
        if (id == null || id.trim().isEmpty()){
            LogUtil.error("项目id为空");
            MSException.throwException("项目id为空");
        }
        return gitLabService.getFileArchive(id,sha);
    }
}
