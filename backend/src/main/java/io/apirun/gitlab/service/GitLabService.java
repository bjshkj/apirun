package io.apirun.gitlab.service;

import io.apirun.base.domain.Project;
import io.apirun.base.mapper.ProjectMapper;
import io.apirun.commons.exception.MSException;
import io.apirun.commons.utils.LogUtil;
import io.apirun.gitlab.GitlabAPI;
import io.apirun.gitlab.models.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GitLabService {

    private static final String gitLab7Url = "";
    private static final String gitLab8Url = "";

    @Resource
    private ProjectMapper projectMapper;
    @Resource
    private RestTemplate restTemplate;

    /**
     * 通过用户名和密码获取用户private-token
     * @param userName 用户名
     * @param password 密码
     * @return private-token(String)
     */
    public String getPrivateTokenByPassword(String userName, String password) {
        String privateToken = "";
        GitlabSession session = null;
        try {
            session = GitlabAPI.connect(gitLab7Url, userName, password);
            privateToken=session.getPrivateToken();
        } catch (IOException e) {
            LogUtil.error(e.getMessage(), e);
            MSException.throwException(e.getMessage());
        }
        return privateToken;

    }

    /**
     * 获取project对应的仓库信息
     * @return
     */
    public GitlabProject getProject(String id) {
        GitlabProject gitProject=null;
        Project apiProject = projectMapper.selectByPrimaryKey(id);
        String[] str = getUrlAndNameSpaceAndProjectName(apiProject.getGitPath());
        if (str == null || str.length<=0){
            LogUtil.error("当前项目没有配置git地址");
            MSException.throwException("当前项目没有配置git地址");
        }
        String url = str[0];
        String namespace=str[1];
        String projectName=str[2];
        isPrivateTokenEmpty(apiProject.getPrivateToken());
        GitlabAPI api = GitlabAPI.connect(url, apiProject.getPrivateToken());
        try {
             gitProject = api.getProject(namespace, projectName);
        } catch (IOException e) {
            LogUtil.error(e.getMessage(), e);
            MSException.throwException(e.getMessage());
        }
        return gitProject;
    }

    /**
     * 获取仓库某路径的目录树结构
     * @param path 路径
     * @param ref 仓库分支
     * @return
     */
    public List<GitlabRepositoryTree> getRepoTree(String id, String path, String ref) {
        List<GitlabRepositoryTree> repositoryTree = new ArrayList<>();
        GitlabProject gitProject = null;
        Project apiProject = projectMapper.selectByPrimaryKey(id);
        String[] str = getUrlAndNameSpaceAndProjectName(apiProject.getGitPath());
        if (str == null || str.length<=0){
            LogUtil.error("当前项目没有配置git地址");
            MSException.throwException("当前项目没有配置git地址");
        }
        String url = str[0];
        String namespace=str[1];
        String projectName=str[2];
        isPrivateTokenEmpty(apiProject.getPrivateToken());
        try {
            GitlabAPI api = GitlabAPI.connect(url, apiProject.getPrivateToken());
            gitProject = api.getProject(namespace, projectName);
            repositoryTree = api.getRepositoryTree(gitProject, path, ref, false);
        } catch (IOException e) {
            LogUtil.error(e.getMessage(), e);
            MSException.throwException(e.getMessage());
        }
        return repositoryTree;
    }

    /**
     * 获取某一个文件的内容
     * @param file_path 文件路径
     * @param ref   分支
     * @return
     */
    public GitlabRepositoryFile getRepoFileContent(String id, String file_path, String ref) {
        GitlabRepositoryFile repositoryFile=null;
        Project apiProject = projectMapper.selectByPrimaryKey(id);
        String[] str = getUrlAndNameSpaceAndProjectName(apiProject.getGitPath());
        if (str == null || str.length<=0){
            LogUtil.error("当前项目没有配置git地址");
            MSException.throwException("当前项目没有配置git地址");
        }
        String url = str[0];
        String namespace=str[1];
        String projectName=str[2];
        isPrivateTokenEmpty(apiProject.getPrivateToken());
        if(apiProject.getPrivateToken().isEmpty()){
            LogUtil.error("当前项目没有配置private_token");
            MSException.throwException("当前项目没有配置当前项目没有配置private_token\"");
        }
        try {
            GitlabAPI api = GitlabAPI.connect(url, apiProject.getPrivateToken());
            GitlabProject project = api.getProject(namespace,projectName);
            repositoryFile = api.getRepositoryFile(project, file_path, ref);
        } catch (IOException e) {
            LogUtil.error(e.getMessage(), e);
            MSException.throwException(e.getMessage());
        }
        return repositoryFile;
    }


    /***
     * 获取一个httprunner项目的所有分支信息
     * @return
     */
    public List<GitlabBranch> getBranches(String id){
        List<GitlabBranch> branches = new ArrayList<>();
        Project apiProject = projectMapper.selectByPrimaryKey(id);
        if (apiProject.getProjectType().equals("httprunner")){
            String[] str = getUrlAndNameSpaceAndProjectName(apiProject.getGitPath());
            if (str == null || str.length<=0){
                LogUtil.error("当前项目没有配置git地址");
                MSException.throwException("当前项目没有配置git地址");
            }
            String url = str[0];
            String namespace=str[1];
            String projectName=str[2];
            isPrivateTokenEmpty(apiProject.getPrivateToken());
            try {
                GitlabAPI api = GitlabAPI.connect(url, apiProject.getPrivateToken());
                GitlabProject project = api.getProject(namespace,projectName);
                branches = api.getBranches(project);
            } catch (IOException e) {
                LogUtil.error(e.getMessage(), e);
                MSException.throwException(e.getMessage());
            }
        }else {
            MSException.throwException("该项目不是httpRunner项目");
        }
        return branches;
    }

    /**
     * 通过url获取域名、namespace以及projectname
     * @param gitPath
     * @return
     */
    public String[] getUrlAndNameSpaceAndProjectName(String gitPath){
        String[] ans = new String[3];
//        Project apiProject = projectMapper.selectByPrimaryKey(id);
        String regex = "^(http(s)?:\\/\\/)?([\\w-]+\\.)+[\\w-]+(:\\d{2,5})?(\\/?\\#?[\\w-.\\/?%&=]*)?$";
        if (gitPath != null && gitPath.matches(regex)){
            if (!gitPath.trim().equals("")){
                String[] str = gitPath.split("/");
                String projectName =str[str.length-1];
                StringBuilder nameSpace = new StringBuilder();
                for (int i = 0; i < str.length - 1; i++) {
                    if (i >= 3 && i < str.length - 1) {
                        nameSpace.append(str[i]);
                        nameSpace.append("/");
                    }
                }
                int i = projectName.indexOf(".");
                if (i != -1) {
                    projectName = projectName.substring(0,i);
                }
                //String nameSpace = str[str.length-2];
                String hostUrl = str[0] + "//" +str[2];
                ans[0] = hostUrl;
                ans[1] = nameSpace.deleteCharAt(nameSpace.lastIndexOf("/")).toString();
                ans[2] = projectName;
                if (ans[0].equals(gitLab7Url) || ans[0].equals(gitLab8Url)) {
                    LogUtil.error("目前已不支持低于GitLab12版本代码仓库，建议迁移至GitLab12以上版本代码仓库");
                    MSException.throwException("目前已不支持低于GitLab12版本代码仓库，建议迁移至GitLab12以上版本代码仓库");
                }
            }
        }else {
            MSException.throwException("输入的gitPath路径不合法");
        }
        return ans;
    }

    /**
     * 判断token是否为空
     * @param privateToken
     */
    public void isPrivateTokenEmpty(String privateToken){
        if(privateToken == null || privateToken.isEmpty()){
            LogUtil.error("当前项目没有配置private_token");
            MSException.throwException("当前项目没有配置private_token");
        }
    }

    /**
     * 获取提交记录
     * @param id
     * @param page
     * @param per_page
     * @param ref
     * @return
     */
    public List<GitlabCommit> getCommitInfo(String id,String page,String per_page,String ref){
        List<GitlabCommit> commits = new ArrayList<>();
        Project apiProject = projectMapper.selectByPrimaryKey(id);
        if (apiProject.getProjectType().equals("httprunner")){
            String[] str = getUrlAndNameSpaceAndProjectName(apiProject.getGitPath());
            if (str == null || str.length<=0){
                LogUtil.error("当前项目没有配置git地址");
                MSException.throwException("当前项目没有配置git地址");
            }
            String url = str[0];
            String namespace=str[1];
            String projectName=str[2];
            isPrivateTokenEmpty(apiProject.getPrivateToken());
            try {
                GitlabAPI api = GitlabAPI.connect(url, apiProject.getPrivateToken());
                GitlabProject project = api.getProject(namespace,projectName);
                int page_num = page != null ? Integer.parseInt(page) : 1;
                int per_page_num = per_page != null ?Integer.parseInt(per_page) : 10;
                commits = api.getLastCommits(project.getId(),ref);
                commits = commits.stream().skip((page_num-1)*10).limit(per_page_num).collect(Collectors.toList());
            } catch (IOException e) {
                LogUtil.error(e.getMessage(), e);
                MSException.throwException(e.getMessage());
            }
        }else {
            MSException.throwException("该项目不是httpRunner项目");
        }
        return commits;
    }

    public byte[] getFileArchive(String id,String sha) {
        Project apiProject = projectMapper.selectByPrimaryKey(id);
        String[] str = getUrlAndNameSpaceAndProjectName(apiProject.getGitPath());
        if (str == null || str.length<=0){
            LogUtil.error("当前项目没有配置git地址");
            MSException.throwException("当前项目没有配置git地址");
        }
        String url = str[0];
        String namespace=str[1];
        String projectName=str[2];
        isPrivateTokenEmpty(apiProject.getPrivateToken());

        GitlabAPI api = GitlabAPI.connect(url, apiProject.getPrivateToken());
        GitlabProject project = null;
        byte[] fileArchive = null;
        try {
            project = api.getProject(namespace,projectName);
            if(sha == null || sha.trim().isEmpty()){
                fileArchive = this.getFileArchive(url, apiProject.getPrivateToken(), project);
            }else {
                fileArchive = this.getFileArchive(url, apiProject.getPrivateToken(), project, sha);
            }
        } catch (IOException e) {
            LogUtil.error("获取git仓库文件字节流失败"+e);
            MSException.throwException("获取git仓库文件字节流失败"+e);
        }
        return fileArchive;
    }

    /**
     * gitlab 官方api中的getFileArchive没有调通，没有找到是什么原因？
     * 自己重写了getFileArchive方法
     * @param url
     * @param token
     * @param project
     * @return
     */
    public byte[] getFileArchive(String url, String token, GitlabProject project) {
        String tailUrl = url +"/api/v4" +GitlabProject.URL + "/" + project.getId() + "/repository/archive.zip?private_token={private_token}";
        Map param = new HashMap();
        param.put("private_token", token);
        byte[] fileArchive = restTemplate.getForObject(tailUrl, byte[].class, param);
        return fileArchive;
    }

    public byte[] getFileArchive(String url, String token, GitlabProject project, String sha) {
        String tailUrl = url +"/api/v4" +GitlabProject.URL + "/" + project.getId() + "/repository/archive.zip?private_token={private_token}&sha={sha}";
        Map param = new HashMap();
        param.put("private_token", token);
        param.put("sha", sha);
        byte[] fileArchive = restTemplate.getForObject(tailUrl, byte[].class, param);
        return fileArchive;
    }

}
