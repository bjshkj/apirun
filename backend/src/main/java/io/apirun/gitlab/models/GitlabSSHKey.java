package io.apirun.gitlab.models;


import io.apirun.gitlab.models.GitlabUser;

public class GitlabSSHKey {
    public static String KEYS_URL = "/keys";
    public static String DEPLOY_KEYS_URL = "/deploy_keys";

    private Integer _id;
    private String _title;
    private String _key;
    private GitlabUser _user;

    public Integer getId() {
        return _id;
    }

    public void setId(Integer id) {
        _id = id;
    }

    public String getTitle() {
        return _title;
    }

    public void setTitle(String title) {
        _title = title;
    }

    public String getKey() {
        return _key;
    }

    public void setKey(String key) {
        _key = key;
    }

    public GitlabUser getUser() {
        return _user;
    }

    public void setUser(GitlabUser user) {
        _user = user;
    }
}
