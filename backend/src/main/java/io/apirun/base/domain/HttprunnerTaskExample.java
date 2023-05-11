package io.apirun.base.domain;

import java.util.ArrayList;
import java.util.List;

public class HttprunnerTaskExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public HttprunnerTaskExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andIdIsNull() {
            addCriterion("id is null");
            return (Criteria) this;
        }

        public Criteria andIdIsNotNull() {
            addCriterion("id is not null");
            return (Criteria) this;
        }

        public Criteria andIdEqualTo(String value) {
            addCriterion("id =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(String value) {
            addCriterion("id <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(String value) {
            addCriterion("id >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(String value) {
            addCriterion("id >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(String value) {
            addCriterion("id <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(String value) {
            addCriterion("id <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLike(String value) {
            addCriterion("id like", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotLike(String value) {
            addCriterion("id not like", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<String> values) {
            addCriterion("id in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<String> values) {
            addCriterion("id not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(String value1, String value2) {
            addCriterion("id between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(String value1, String value2) {
            addCriterion("id not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andTaskNameIsNull() {
            addCriterion("task_name is null");
            return (Criteria) this;
        }

        public Criteria andTaskNameIsNotNull() {
            addCriterion("task_name is not null");
            return (Criteria) this;
        }

        public Criteria andTaskNameEqualTo(String value) {
            addCriterion("task_name =", value, "taskName");
            return (Criteria) this;
        }

        public Criteria andTaskNameNotEqualTo(String value) {
            addCriterion("task_name <>", value, "taskName");
            return (Criteria) this;
        }

        public Criteria andTaskNameGreaterThan(String value) {
            addCriterion("task_name >", value, "taskName");
            return (Criteria) this;
        }

        public Criteria andTaskNameGreaterThanOrEqualTo(String value) {
            addCriterion("task_name >=", value, "taskName");
            return (Criteria) this;
        }

        public Criteria andTaskNameLessThan(String value) {
            addCriterion("task_name <", value, "taskName");
            return (Criteria) this;
        }

        public Criteria andTaskNameLessThanOrEqualTo(String value) {
            addCriterion("task_name <=", value, "taskName");
            return (Criteria) this;
        }

        public Criteria andTaskNameLike(String value) {
            addCriterion("task_name like", value, "taskName");
            return (Criteria) this;
        }

        public Criteria andTaskNameNotLike(String value) {
            addCriterion("task_name not like", value, "taskName");
            return (Criteria) this;
        }

        public Criteria andTaskNameIn(List<String> values) {
            addCriterion("task_name in", values, "taskName");
            return (Criteria) this;
        }

        public Criteria andTaskNameNotIn(List<String> values) {
            addCriterion("task_name not in", values, "taskName");
            return (Criteria) this;
        }

        public Criteria andTaskNameBetween(String value1, String value2) {
            addCriterion("task_name between", value1, value2, "taskName");
            return (Criteria) this;
        }

        public Criteria andTaskNameNotBetween(String value1, String value2) {
            addCriterion("task_name not between", value1, value2, "taskName");
            return (Criteria) this;
        }

        public Criteria andProjectIdIsNull() {
            addCriterion("project_id is null");
            return (Criteria) this;
        }

        public Criteria andProjectIdIsNotNull() {
            addCriterion("project_id is not null");
            return (Criteria) this;
        }

        public Criteria andProjectIdEqualTo(String value) {
            addCriterion("project_id =", value, "projectId");
            return (Criteria) this;
        }

        public Criteria andProjectIdNotEqualTo(String value) {
            addCriterion("project_id <>", value, "projectId");
            return (Criteria) this;
        }

        public Criteria andProjectIdGreaterThan(String value) {
            addCriterion("project_id >", value, "projectId");
            return (Criteria) this;
        }

        public Criteria andProjectIdGreaterThanOrEqualTo(String value) {
            addCriterion("project_id >=", value, "projectId");
            return (Criteria) this;
        }

        public Criteria andProjectIdLessThan(String value) {
            addCriterion("project_id <", value, "projectId");
            return (Criteria) this;
        }

        public Criteria andProjectIdLessThanOrEqualTo(String value) {
            addCriterion("project_id <=", value, "projectId");
            return (Criteria) this;
        }

        public Criteria andProjectIdLike(String value) {
            addCriterion("project_id like", value, "projectId");
            return (Criteria) this;
        }

        public Criteria andProjectIdNotLike(String value) {
            addCriterion("project_id not like", value, "projectId");
            return (Criteria) this;
        }

        public Criteria andProjectIdIn(List<String> values) {
            addCriterion("project_id in", values, "projectId");
            return (Criteria) this;
        }

        public Criteria andProjectIdNotIn(List<String> values) {
            addCriterion("project_id not in", values, "projectId");
            return (Criteria) this;
        }

        public Criteria andProjectIdBetween(String value1, String value2) {
            addCriterion("project_id between", value1, value2, "projectId");
            return (Criteria) this;
        }

        public Criteria andProjectIdNotBetween(String value1, String value2) {
            addCriterion("project_id not between", value1, value2, "projectId");
            return (Criteria) this;
        }

        public Criteria andGitVersionIsNull() {
            addCriterion("git_version is null");
            return (Criteria) this;
        }

        public Criteria andGitVersionIsNotNull() {
            addCriterion("git_version is not null");
            return (Criteria) this;
        }

        public Criteria andGitVersionEqualTo(String value) {
            addCriterion("git_version =", value, "gitVersion");
            return (Criteria) this;
        }

        public Criteria andGitVersionNotEqualTo(String value) {
            addCriterion("git_version <>", value, "gitVersion");
            return (Criteria) this;
        }

        public Criteria andGitVersionGreaterThan(String value) {
            addCriterion("git_version >", value, "gitVersion");
            return (Criteria) this;
        }

        public Criteria andGitVersionGreaterThanOrEqualTo(String value) {
            addCriterion("git_version >=", value, "gitVersion");
            return (Criteria) this;
        }

        public Criteria andGitVersionLessThan(String value) {
            addCriterion("git_version <", value, "gitVersion");
            return (Criteria) this;
        }

        public Criteria andGitVersionLessThanOrEqualTo(String value) {
            addCriterion("git_version <=", value, "gitVersion");
            return (Criteria) this;
        }

        public Criteria andGitVersionLike(String value) {
            addCriterion("git_version like", value, "gitVersion");
            return (Criteria) this;
        }

        public Criteria andGitVersionNotLike(String value) {
            addCriterion("git_version not like", value, "gitVersion");
            return (Criteria) this;
        }

        public Criteria andGitVersionIn(List<String> values) {
            addCriterion("git_version in", values, "gitVersion");
            return (Criteria) this;
        }

        public Criteria andGitVersionNotIn(List<String> values) {
            addCriterion("git_version not in", values, "gitVersion");
            return (Criteria) this;
        }

        public Criteria andGitVersionBetween(String value1, String value2) {
            addCriterion("git_version between", value1, value2, "gitVersion");
            return (Criteria) this;
        }

        public Criteria andGitVersionNotBetween(String value1, String value2) {
            addCriterion("git_version not between", value1, value2, "gitVersion");
            return (Criteria) this;
        }

        public Criteria andDebugModeIsNull() {
            addCriterion("debug_mode is null");
            return (Criteria) this;
        }

        public Criteria andDebugModeIsNotNull() {
            addCriterion("debug_mode is not null");
            return (Criteria) this;
        }

        public Criteria andDebugModeEqualTo(String value) {
            addCriterion("debug_mode =", value, "debugMode");
            return (Criteria) this;
        }

        public Criteria andDebugModeNotEqualTo(String value) {
            addCriterion("debug_mode <>", value, "debugMode");
            return (Criteria) this;
        }

        public Criteria andDebugModeGreaterThan(String value) {
            addCriterion("debug_mode >", value, "debugMode");
            return (Criteria) this;
        }

        public Criteria andDebugModeGreaterThanOrEqualTo(String value) {
            addCriterion("debug_mode >=", value, "debugMode");
            return (Criteria) this;
        }

        public Criteria andDebugModeLessThan(String value) {
            addCriterion("debug_mode <", value, "debugMode");
            return (Criteria) this;
        }

        public Criteria andDebugModeLessThanOrEqualTo(String value) {
            addCriterion("debug_mode <=", value, "debugMode");
            return (Criteria) this;
        }

        public Criteria andDebugModeLike(String value) {
            addCriterion("debug_mode like", value, "debugMode");
            return (Criteria) this;
        }

        public Criteria andDebugModeNotLike(String value) {
            addCriterion("debug_mode not like", value, "debugMode");
            return (Criteria) this;
        }

        public Criteria andDebugModeIn(List<String> values) {
            addCriterion("debug_mode in", values, "debugMode");
            return (Criteria) this;
        }

        public Criteria andDebugModeNotIn(List<String> values) {
            addCriterion("debug_mode not in", values, "debugMode");
            return (Criteria) this;
        }

        public Criteria andDebugModeBetween(String value1, String value2) {
            addCriterion("debug_mode between", value1, value2, "debugMode");
            return (Criteria) this;
        }

        public Criteria andDebugModeNotBetween(String value1, String value2) {
            addCriterion("debug_mode not between", value1, value2, "debugMode");
            return (Criteria) this;
        }

        public Criteria andGitVerDescIsNull() {
            addCriterion("git_ver_desc is null");
            return (Criteria) this;
        }

        public Criteria andGitVerDescIsNotNull() {
            addCriterion("git_ver_desc is not null");
            return (Criteria) this;
        }

        public Criteria andGitVerDescEqualTo(String value) {
            addCriterion("git_ver_desc =", value, "gitVerDesc");
            return (Criteria) this;
        }

        public Criteria andGitVerDescNotEqualTo(String value) {
            addCriterion("git_ver_desc <>", value, "gitVerDesc");
            return (Criteria) this;
        }

        public Criteria andGitVerDescGreaterThan(String value) {
            addCriterion("git_ver_desc >", value, "gitVerDesc");
            return (Criteria) this;
        }

        public Criteria andGitVerDescGreaterThanOrEqualTo(String value) {
            addCriterion("git_ver_desc >=", value, "gitVerDesc");
            return (Criteria) this;
        }

        public Criteria andGitVerDescLessThan(String value) {
            addCriterion("git_ver_desc <", value, "gitVerDesc");
            return (Criteria) this;
        }

        public Criteria andGitVerDescLessThanOrEqualTo(String value) {
            addCriterion("git_ver_desc <=", value, "gitVerDesc");
            return (Criteria) this;
        }

        public Criteria andGitVerDescLike(String value) {
            addCriterion("git_ver_desc like", value, "gitVerDesc");
            return (Criteria) this;
        }

        public Criteria andGitVerDescNotLike(String value) {
            addCriterion("git_ver_desc not like", value, "gitVerDesc");
            return (Criteria) this;
        }

        public Criteria andGitVerDescIn(List<String> values) {
            addCriterion("git_ver_desc in", values, "gitVerDesc");
            return (Criteria) this;
        }

        public Criteria andGitVerDescNotIn(List<String> values) {
            addCriterion("git_ver_desc not in", values, "gitVerDesc");
            return (Criteria) this;
        }

        public Criteria andGitVerDescBetween(String value1, String value2) {
            addCriterion("git_ver_desc between", value1, value2, "gitVerDesc");
            return (Criteria) this;
        }

        public Criteria andGitVerDescNotBetween(String value1, String value2) {
            addCriterion("git_ver_desc not between", value1, value2, "gitVerDesc");
            return (Criteria) this;
        }

        public Criteria andExecCasePathsIsNull() {
            addCriterion("exec_case_paths is null");
            return (Criteria) this;
        }

        public Criteria andExecCasePathsIsNotNull() {
            addCriterion("exec_case_paths is not null");
            return (Criteria) this;
        }

        public Criteria andExecCasePathsEqualTo(String value) {
            addCriterion("exec_case_paths =", value, "execCasePaths");
            return (Criteria) this;
        }

        public Criteria andExecCasePathsNotEqualTo(String value) {
            addCriterion("exec_case_paths <>", value, "execCasePaths");
            return (Criteria) this;
        }

        public Criteria andExecCasePathsGreaterThan(String value) {
            addCriterion("exec_case_paths >", value, "execCasePaths");
            return (Criteria) this;
        }

        public Criteria andExecCasePathsGreaterThanOrEqualTo(String value) {
            addCriterion("exec_case_paths >=", value, "execCasePaths");
            return (Criteria) this;
        }

        public Criteria andExecCasePathsLessThan(String value) {
            addCriterion("exec_case_paths <", value, "execCasePaths");
            return (Criteria) this;
        }

        public Criteria andExecCasePathsLessThanOrEqualTo(String value) {
            addCriterion("exec_case_paths <=", value, "execCasePaths");
            return (Criteria) this;
        }

        public Criteria andExecCasePathsLike(String value) {
            addCriterion("exec_case_paths like", value, "execCasePaths");
            return (Criteria) this;
        }

        public Criteria andExecCasePathsNotLike(String value) {
            addCriterion("exec_case_paths not like", value, "execCasePaths");
            return (Criteria) this;
        }

        public Criteria andExecCasePathsIn(List<String> values) {
            addCriterion("exec_case_paths in", values, "execCasePaths");
            return (Criteria) this;
        }

        public Criteria andExecCasePathsNotIn(List<String> values) {
            addCriterion("exec_case_paths not in", values, "execCasePaths");
            return (Criteria) this;
        }

        public Criteria andExecCasePathsBetween(String value1, String value2) {
            addCriterion("exec_case_paths between", value1, value2, "execCasePaths");
            return (Criteria) this;
        }

        public Criteria andExecCasePathsNotBetween(String value1, String value2) {
            addCriterion("exec_case_paths not between", value1, value2, "execCasePaths");
            return (Criteria) this;
        }

        public Criteria andDotEnvPathIsNull() {
            addCriterion("dot_env_path is null");
            return (Criteria) this;
        }

        public Criteria andDotEnvPathIsNotNull() {
            addCriterion("dot_env_path is not null");
            return (Criteria) this;
        }

        public Criteria andDotEnvPathEqualTo(String value) {
            addCriterion("dot_env_path =", value, "dotEnvPath");
            return (Criteria) this;
        }

        public Criteria andDotEnvPathNotEqualTo(String value) {
            addCriterion("dot_env_path <>", value, "dotEnvPath");
            return (Criteria) this;
        }

        public Criteria andDotEnvPathGreaterThan(String value) {
            addCriterion("dot_env_path >", value, "dotEnvPath");
            return (Criteria) this;
        }

        public Criteria andDotEnvPathGreaterThanOrEqualTo(String value) {
            addCriterion("dot_env_path >=", value, "dotEnvPath");
            return (Criteria) this;
        }

        public Criteria andDotEnvPathLessThan(String value) {
            addCriterion("dot_env_path <", value, "dotEnvPath");
            return (Criteria) this;
        }

        public Criteria andDotEnvPathLessThanOrEqualTo(String value) {
            addCriterion("dot_env_path <=", value, "dotEnvPath");
            return (Criteria) this;
        }

        public Criteria andDotEnvPathLike(String value) {
            addCriterion("dot_env_path like", value, "dotEnvPath");
            return (Criteria) this;
        }

        public Criteria andDotEnvPathNotLike(String value) {
            addCriterion("dot_env_path not like", value, "dotEnvPath");
            return (Criteria) this;
        }

        public Criteria andDotEnvPathIn(List<String> values) {
            addCriterion("dot_env_path in", values, "dotEnvPath");
            return (Criteria) this;
        }

        public Criteria andDotEnvPathNotIn(List<String> values) {
            addCriterion("dot_env_path not in", values, "dotEnvPath");
            return (Criteria) this;
        }

        public Criteria andDotEnvPathBetween(String value1, String value2) {
            addCriterion("dot_env_path between", value1, value2, "dotEnvPath");
            return (Criteria) this;
        }

        public Criteria andDotEnvPathNotBetween(String value1, String value2) {
            addCriterion("dot_env_path not between", value1, value2, "dotEnvPath");
            return (Criteria) this;
        }

        public Criteria andHostsPathIsNull() {
            addCriterion("hosts_path is null");
            return (Criteria) this;
        }

        public Criteria andHostsPathIsNotNull() {
            addCriterion("hosts_path is not null");
            return (Criteria) this;
        }

        public Criteria andHostsPathEqualTo(String value) {
            addCriterion("hosts_path =", value, "hostsPath");
            return (Criteria) this;
        }

        public Criteria andHostsPathNotEqualTo(String value) {
            addCriterion("hosts_path <>", value, "hostsPath");
            return (Criteria) this;
        }

        public Criteria andHostsPathGreaterThan(String value) {
            addCriterion("hosts_path >", value, "hostsPath");
            return (Criteria) this;
        }

        public Criteria andHostsPathGreaterThanOrEqualTo(String value) {
            addCriterion("hosts_path >=", value, "hostsPath");
            return (Criteria) this;
        }

        public Criteria andHostsPathLessThan(String value) {
            addCriterion("hosts_path <", value, "hostsPath");
            return (Criteria) this;
        }

        public Criteria andHostsPathLessThanOrEqualTo(String value) {
            addCriterion("hosts_path <=", value, "hostsPath");
            return (Criteria) this;
        }

        public Criteria andHostsPathLike(String value) {
            addCriterion("hosts_path like", value, "hostsPath");
            return (Criteria) this;
        }

        public Criteria andHostsPathNotLike(String value) {
            addCriterion("hosts_path not like", value, "hostsPath");
            return (Criteria) this;
        }

        public Criteria andHostsPathIn(List<String> values) {
            addCriterion("hosts_path in", values, "hostsPath");
            return (Criteria) this;
        }

        public Criteria andHostsPathNotIn(List<String> values) {
            addCriterion("hosts_path not in", values, "hostsPath");
            return (Criteria) this;
        }

        public Criteria andHostsPathBetween(String value1, String value2) {
            addCriterion("hosts_path between", value1, value2, "hostsPath");
            return (Criteria) this;
        }

        public Criteria andHostsPathNotBetween(String value1, String value2) {
            addCriterion("hosts_path not between", value1, value2, "hostsPath");
            return (Criteria) this;
        }

        public Criteria andTriggerModelIsNull() {
            addCriterion("trigger_model is null");
            return (Criteria) this;
        }

        public Criteria andTriggerModelIsNotNull() {
            addCriterion("trigger_model is not null");
            return (Criteria) this;
        }

        public Criteria andTriggerModelEqualTo(String value) {
            addCriterion("trigger_model =", value, "triggerModel");
            return (Criteria) this;
        }

        public Criteria andTriggerModelNotEqualTo(String value) {
            addCriterion("trigger_model <>", value, "triggerModel");
            return (Criteria) this;
        }

        public Criteria andTriggerModelGreaterThan(String value) {
            addCriterion("trigger_model >", value, "triggerModel");
            return (Criteria) this;
        }

        public Criteria andTriggerModelGreaterThanOrEqualTo(String value) {
            addCriterion("trigger_model >=", value, "triggerModel");
            return (Criteria) this;
        }

        public Criteria andTriggerModelLessThan(String value) {
            addCriterion("trigger_model <", value, "triggerModel");
            return (Criteria) this;
        }

        public Criteria andTriggerModelLessThanOrEqualTo(String value) {
            addCriterion("trigger_model <=", value, "triggerModel");
            return (Criteria) this;
        }

        public Criteria andTriggerModelLike(String value) {
            addCriterion("trigger_model like", value, "triggerModel");
            return (Criteria) this;
        }

        public Criteria andTriggerModelNotLike(String value) {
            addCriterion("trigger_model not like", value, "triggerModel");
            return (Criteria) this;
        }

        public Criteria andTriggerModelIn(List<String> values) {
            addCriterion("trigger_model in", values, "triggerModel");
            return (Criteria) this;
        }

        public Criteria andTriggerModelNotIn(List<String> values) {
            addCriterion("trigger_model not in", values, "triggerModel");
            return (Criteria) this;
        }

        public Criteria andTriggerModelBetween(String value1, String value2) {
            addCriterion("trigger_model between", value1, value2, "triggerModel");
            return (Criteria) this;
        }

        public Criteria andTriggerModelNotBetween(String value1, String value2) {
            addCriterion("trigger_model not between", value1, value2, "triggerModel");
            return (Criteria) this;
        }

        public Criteria andCreateUserIsNull() {
            addCriterion("create_user is null");
            return (Criteria) this;
        }

        public Criteria andCreateUserIsNotNull() {
            addCriterion("create_user is not null");
            return (Criteria) this;
        }

        public Criteria andCreateUserEqualTo(String value) {
            addCriterion("create_user =", value, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateUserNotEqualTo(String value) {
            addCriterion("create_user <>", value, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateUserGreaterThan(String value) {
            addCriterion("create_user >", value, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateUserGreaterThanOrEqualTo(String value) {
            addCriterion("create_user >=", value, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateUserLessThan(String value) {
            addCriterion("create_user <", value, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateUserLessThanOrEqualTo(String value) {
            addCriterion("create_user <=", value, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateUserLike(String value) {
            addCriterion("create_user like", value, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateUserNotLike(String value) {
            addCriterion("create_user not like", value, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateUserIn(List<String> values) {
            addCriterion("create_user in", values, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateUserNotIn(List<String> values) {
            addCriterion("create_user not in", values, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateUserBetween(String value1, String value2) {
            addCriterion("create_user between", value1, value2, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateUserNotBetween(String value1, String value2) {
            addCriterion("create_user not between", value1, value2, "createUser");
            return (Criteria) this;
        }

        public Criteria andTaskStatusIsNull() {
            addCriterion("task_status is null");
            return (Criteria) this;
        }

        public Criteria andTaskStatusIsNotNull() {
            addCriterion("task_status is not null");
            return (Criteria) this;
        }

        public Criteria andTaskStatusEqualTo(String value) {
            addCriterion("task_status =", value, "taskStatus");
            return (Criteria) this;
        }

        public Criteria andTaskStatusNotEqualTo(String value) {
            addCriterion("task_status <>", value, "taskStatus");
            return (Criteria) this;
        }

        public Criteria andTaskStatusGreaterThan(String value) {
            addCriterion("task_status >", value, "taskStatus");
            return (Criteria) this;
        }

        public Criteria andTaskStatusGreaterThanOrEqualTo(String value) {
            addCriterion("task_status >=", value, "taskStatus");
            return (Criteria) this;
        }

        public Criteria andTaskStatusLessThan(String value) {
            addCriterion("task_status <", value, "taskStatus");
            return (Criteria) this;
        }

        public Criteria andTaskStatusLessThanOrEqualTo(String value) {
            addCriterion("task_status <=", value, "taskStatus");
            return (Criteria) this;
        }

        public Criteria andTaskStatusLike(String value) {
            addCriterion("task_status like", value, "taskStatus");
            return (Criteria) this;
        }

        public Criteria andTaskStatusNotLike(String value) {
            addCriterion("task_status not like", value, "taskStatus");
            return (Criteria) this;
        }

        public Criteria andTaskStatusIn(List<String> values) {
            addCriterion("task_status in", values, "taskStatus");
            return (Criteria) this;
        }

        public Criteria andTaskStatusNotIn(List<String> values) {
            addCriterion("task_status not in", values, "taskStatus");
            return (Criteria) this;
        }

        public Criteria andTaskStatusBetween(String value1, String value2) {
            addCriterion("task_status between", value1, value2, "taskStatus");
            return (Criteria) this;
        }

        public Criteria andTaskStatusNotBetween(String value1, String value2) {
            addCriterion("task_status not between", value1, value2, "taskStatus");
            return (Criteria) this;
        }

        public Criteria andCtimeIsNull() {
            addCriterion("ctime is null");
            return (Criteria) this;
        }

        public Criteria andCtimeIsNotNull() {
            addCriterion("ctime is not null");
            return (Criteria) this;
        }

        public Criteria andCtimeEqualTo(Long value) {
            addCriterion("ctime =", value, "ctime");
            return (Criteria) this;
        }

        public Criteria andCtimeNotEqualTo(Long value) {
            addCriterion("ctime <>", value, "ctime");
            return (Criteria) this;
        }

        public Criteria andCtimeGreaterThan(Long value) {
            addCriterion("ctime >", value, "ctime");
            return (Criteria) this;
        }

        public Criteria andCtimeGreaterThanOrEqualTo(Long value) {
            addCriterion("ctime >=", value, "ctime");
            return (Criteria) this;
        }

        public Criteria andCtimeLessThan(Long value) {
            addCriterion("ctime <", value, "ctime");
            return (Criteria) this;
        }

        public Criteria andCtimeLessThanOrEqualTo(Long value) {
            addCriterion("ctime <=", value, "ctime");
            return (Criteria) this;
        }

        public Criteria andCtimeIn(List<Long> values) {
            addCriterion("ctime in", values, "ctime");
            return (Criteria) this;
        }

        public Criteria andCtimeNotIn(List<Long> values) {
            addCriterion("ctime not in", values, "ctime");
            return (Criteria) this;
        }

        public Criteria andCtimeBetween(Long value1, Long value2) {
            addCriterion("ctime between", value1, value2, "ctime");
            return (Criteria) this;
        }

        public Criteria andCtimeNotBetween(Long value1, Long value2) {
            addCriterion("ctime not between", value1, value2, "ctime");
            return (Criteria) this;
        }

        public Criteria andUtimeIsNull() {
            addCriterion("utime is null");
            return (Criteria) this;
        }

        public Criteria andUtimeIsNotNull() {
            addCriterion("utime is not null");
            return (Criteria) this;
        }

        public Criteria andUtimeEqualTo(Long value) {
            addCriterion("utime =", value, "utime");
            return (Criteria) this;
        }

        public Criteria andUtimeNotEqualTo(Long value) {
            addCriterion("utime <>", value, "utime");
            return (Criteria) this;
        }

        public Criteria andUtimeGreaterThan(Long value) {
            addCriterion("utime >", value, "utime");
            return (Criteria) this;
        }

        public Criteria andUtimeGreaterThanOrEqualTo(Long value) {
            addCriterion("utime >=", value, "utime");
            return (Criteria) this;
        }

        public Criteria andUtimeLessThan(Long value) {
            addCriterion("utime <", value, "utime");
            return (Criteria) this;
        }

        public Criteria andUtimeLessThanOrEqualTo(Long value) {
            addCriterion("utime <=", value, "utime");
            return (Criteria) this;
        }

        public Criteria andUtimeIn(List<Long> values) {
            addCriterion("utime in", values, "utime");
            return (Criteria) this;
        }

        public Criteria andUtimeNotIn(List<Long> values) {
            addCriterion("utime not in", values, "utime");
            return (Criteria) this;
        }

        public Criteria andUtimeBetween(Long value1, Long value2) {
            addCriterion("utime between", value1, value2, "utime");
            return (Criteria) this;
        }

        public Criteria andUtimeNotBetween(Long value1, Long value2) {
            addCriterion("utime not between", value1, value2, "utime");
            return (Criteria) this;
        }

        public Criteria andNodeIdIsNull() {
            addCriterion("node_id is null");
            return (Criteria) this;
        }

        public Criteria andNodeIdIsNotNull() {
            addCriterion("node_id is not null");
            return (Criteria) this;
        }

        public Criteria andNodeIdEqualTo(String value) {
            addCriterion("node_id =", value, "nodeId");
            return (Criteria) this;
        }

        public Criteria andNodeIdNotEqualTo(String value) {
            addCriterion("node_id <>", value, "nodeId");
            return (Criteria) this;
        }

        public Criteria andNodeIdGreaterThan(String value) {
            addCriterion("node_id >", value, "nodeId");
            return (Criteria) this;
        }

        public Criteria andNodeIdGreaterThanOrEqualTo(String value) {
            addCriterion("node_id >=", value, "nodeId");
            return (Criteria) this;
        }

        public Criteria andNodeIdLessThan(String value) {
            addCriterion("node_id <", value, "nodeId");
            return (Criteria) this;
        }

        public Criteria andNodeIdLessThanOrEqualTo(String value) {
            addCriterion("node_id <=", value, "nodeId");
            return (Criteria) this;
        }

        public Criteria andNodeIdLike(String value) {
            addCriterion("node_id like", value, "nodeId");
            return (Criteria) this;
        }

        public Criteria andNodeIdNotLike(String value) {
            addCriterion("node_id not like", value, "nodeId");
            return (Criteria) this;
        }

        public Criteria andNodeIdIn(List<String> values) {
            addCriterion("node_id in", values, "nodeId");
            return (Criteria) this;
        }

        public Criteria andNodeIdNotIn(List<String> values) {
            addCriterion("node_id not in", values, "nodeId");
            return (Criteria) this;
        }

        public Criteria andNodeIdBetween(String value1, String value2) {
            addCriterion("node_id between", value1, value2, "nodeId");
            return (Criteria) this;
        }

        public Criteria andNodeIdNotBetween(String value1, String value2) {
            addCriterion("node_id not between", value1, value2, "nodeId");
            return (Criteria) this;
        }

        public Criteria andImageIsNull() {
            addCriterion("image is null");
            return (Criteria) this;
        }

        public Criteria andImageIsNotNull() {
            addCriterion("image is not null");
            return (Criteria) this;
        }

        public Criteria andImageEqualTo(String value) {
            addCriterion("image =", value, "image");
            return (Criteria) this;
        }

        public Criteria andImageNotEqualTo(String value) {
            addCriterion("image <>", value, "image");
            return (Criteria) this;
        }

        public Criteria andImageGreaterThan(String value) {
            addCriterion("image >", value, "image");
            return (Criteria) this;
        }

        public Criteria andImageGreaterThanOrEqualTo(String value) {
            addCriterion("image >=", value, "image");
            return (Criteria) this;
        }

        public Criteria andImageLessThan(String value) {
            addCriterion("image <", value, "image");
            return (Criteria) this;
        }

        public Criteria andImageLessThanOrEqualTo(String value) {
            addCriterion("image <=", value, "image");
            return (Criteria) this;
        }

        public Criteria andImageLike(String value) {
            addCriterion("image like", value, "image");
            return (Criteria) this;
        }

        public Criteria andImageNotLike(String value) {
            addCriterion("image not like", value, "image");
            return (Criteria) this;
        }

        public Criteria andImageIn(List<String> values) {
            addCriterion("image in", values, "image");
            return (Criteria) this;
        }

        public Criteria andImageNotIn(List<String> values) {
            addCriterion("image not in", values, "image");
            return (Criteria) this;
        }

        public Criteria andImageBetween(String value1, String value2) {
            addCriterion("image between", value1, value2, "image");
            return (Criteria) this;
        }

        public Criteria andImageNotBetween(String value1, String value2) {
            addCriterion("image not between", value1, value2, "image");
            return (Criteria) this;
        }

        public Criteria andStimeIsNull() {
            addCriterion("stime is null");
            return (Criteria) this;
        }

        public Criteria andStimeIsNotNull() {
            addCriterion("stime is not null");
            return (Criteria) this;
        }

        public Criteria andStimeEqualTo(Long value) {
            addCriterion("stime =", value, "stime");
            return (Criteria) this;
        }

        public Criteria andStimeNotEqualTo(Long value) {
            addCriterion("stime <>", value, "stime");
            return (Criteria) this;
        }

        public Criteria andStimeGreaterThan(Long value) {
            addCriterion("stime >", value, "stime");
            return (Criteria) this;
        }

        public Criteria andStimeGreaterThanOrEqualTo(Long value) {
            addCriterion("stime >=", value, "stime");
            return (Criteria) this;
        }

        public Criteria andStimeLessThan(Long value) {
            addCriterion("stime <", value, "stime");
            return (Criteria) this;
        }

        public Criteria andStimeLessThanOrEqualTo(Long value) {
            addCriterion("stime <=", value, "stime");
            return (Criteria) this;
        }

        public Criteria andStimeIn(List<Long> values) {
            addCriterion("stime in", values, "stime");
            return (Criteria) this;
        }

        public Criteria andStimeNotIn(List<Long> values) {
            addCriterion("stime not in", values, "stime");
            return (Criteria) this;
        }

        public Criteria andStimeBetween(Long value1, Long value2) {
            addCriterion("stime between", value1, value2, "stime");
            return (Criteria) this;
        }

        public Criteria andStimeNotBetween(Long value1, Long value2) {
            addCriterion("stime not between", value1, value2, "stime");
            return (Criteria) this;
        }

        public Criteria andFtimeIsNull() {
            addCriterion("ftime is null");
            return (Criteria) this;
        }

        public Criteria andFtimeIsNotNull() {
            addCriterion("ftime is not null");
            return (Criteria) this;
        }

        public Criteria andFtimeEqualTo(Long value) {
            addCriterion("ftime =", value, "ftime");
            return (Criteria) this;
        }

        public Criteria andFtimeNotEqualTo(Long value) {
            addCriterion("ftime <>", value, "ftime");
            return (Criteria) this;
        }

        public Criteria andFtimeGreaterThan(Long value) {
            addCriterion("ftime >", value, "ftime");
            return (Criteria) this;
        }

        public Criteria andFtimeGreaterThanOrEqualTo(Long value) {
            addCriterion("ftime >=", value, "ftime");
            return (Criteria) this;
        }

        public Criteria andFtimeLessThan(Long value) {
            addCriterion("ftime <", value, "ftime");
            return (Criteria) this;
        }

        public Criteria andFtimeLessThanOrEqualTo(Long value) {
            addCriterion("ftime <=", value, "ftime");
            return (Criteria) this;
        }

        public Criteria andFtimeIn(List<Long> values) {
            addCriterion("ftime in", values, "ftime");
            return (Criteria) this;
        }

        public Criteria andFtimeNotIn(List<Long> values) {
            addCriterion("ftime not in", values, "ftime");
            return (Criteria) this;
        }

        public Criteria andFtimeBetween(Long value1, Long value2) {
            addCriterion("ftime between", value1, value2, "ftime");
            return (Criteria) this;
        }

        public Criteria andFtimeNotBetween(Long value1, Long value2) {
            addCriterion("ftime not between", value1, value2, "ftime");
            return (Criteria) this;
        }

        public Criteria andTcasesTotalIsNull() {
            addCriterion("tcases_total is null");
            return (Criteria) this;
        }

        public Criteria andTcasesTotalIsNotNull() {
            addCriterion("tcases_total is not null");
            return (Criteria) this;
        }

        public Criteria andTcasesTotalEqualTo(Integer value) {
            addCriterion("tcases_total =", value, "tcasesTotal");
            return (Criteria) this;
        }

        public Criteria andTcasesTotalNotEqualTo(Integer value) {
            addCriterion("tcases_total <>", value, "tcasesTotal");
            return (Criteria) this;
        }

        public Criteria andTcasesTotalGreaterThan(Integer value) {
            addCriterion("tcases_total >", value, "tcasesTotal");
            return (Criteria) this;
        }

        public Criteria andTcasesTotalGreaterThanOrEqualTo(Integer value) {
            addCriterion("tcases_total >=", value, "tcasesTotal");
            return (Criteria) this;
        }

        public Criteria andTcasesTotalLessThan(Integer value) {
            addCriterion("tcases_total <", value, "tcasesTotal");
            return (Criteria) this;
        }

        public Criteria andTcasesTotalLessThanOrEqualTo(Integer value) {
            addCriterion("tcases_total <=", value, "tcasesTotal");
            return (Criteria) this;
        }

        public Criteria andTcasesTotalIn(List<Integer> values) {
            addCriterion("tcases_total in", values, "tcasesTotal");
            return (Criteria) this;
        }

        public Criteria andTcasesTotalNotIn(List<Integer> values) {
            addCriterion("tcases_total not in", values, "tcasesTotal");
            return (Criteria) this;
        }

        public Criteria andTcasesTotalBetween(Integer value1, Integer value2) {
            addCriterion("tcases_total between", value1, value2, "tcasesTotal");
            return (Criteria) this;
        }

        public Criteria andTcasesTotalNotBetween(Integer value1, Integer value2) {
            addCriterion("tcases_total not between", value1, value2, "tcasesTotal");
            return (Criteria) this;
        }

        public Criteria andTcasesSuccessIsNull() {
            addCriterion("tcases_success is null");
            return (Criteria) this;
        }

        public Criteria andTcasesSuccessIsNotNull() {
            addCriterion("tcases_success is not null");
            return (Criteria) this;
        }

        public Criteria andTcasesSuccessEqualTo(Integer value) {
            addCriterion("tcases_success =", value, "tcasesSuccess");
            return (Criteria) this;
        }

        public Criteria andTcasesSuccessNotEqualTo(Integer value) {
            addCriterion("tcases_success <>", value, "tcasesSuccess");
            return (Criteria) this;
        }

        public Criteria andTcasesSuccessGreaterThan(Integer value) {
            addCriterion("tcases_success >", value, "tcasesSuccess");
            return (Criteria) this;
        }

        public Criteria andTcasesSuccessGreaterThanOrEqualTo(Integer value) {
            addCriterion("tcases_success >=", value, "tcasesSuccess");
            return (Criteria) this;
        }

        public Criteria andTcasesSuccessLessThan(Integer value) {
            addCriterion("tcases_success <", value, "tcasesSuccess");
            return (Criteria) this;
        }

        public Criteria andTcasesSuccessLessThanOrEqualTo(Integer value) {
            addCriterion("tcases_success <=", value, "tcasesSuccess");
            return (Criteria) this;
        }

        public Criteria andTcasesSuccessIn(List<Integer> values) {
            addCriterion("tcases_success in", values, "tcasesSuccess");
            return (Criteria) this;
        }

        public Criteria andTcasesSuccessNotIn(List<Integer> values) {
            addCriterion("tcases_success not in", values, "tcasesSuccess");
            return (Criteria) this;
        }

        public Criteria andTcasesSuccessBetween(Integer value1, Integer value2) {
            addCriterion("tcases_success between", value1, value2, "tcasesSuccess");
            return (Criteria) this;
        }

        public Criteria andTcasesSuccessNotBetween(Integer value1, Integer value2) {
            addCriterion("tcases_success not between", value1, value2, "tcasesSuccess");
            return (Criteria) this;
        }

        public Criteria andTcasesFailIsNull() {
            addCriterion("tcases_fail is null");
            return (Criteria) this;
        }

        public Criteria andTcasesFailIsNotNull() {
            addCriterion("tcases_fail is not null");
            return (Criteria) this;
        }

        public Criteria andTcasesFailEqualTo(Integer value) {
            addCriterion("tcases_fail =", value, "tcasesFail");
            return (Criteria) this;
        }

        public Criteria andTcasesFailNotEqualTo(Integer value) {
            addCriterion("tcases_fail <>", value, "tcasesFail");
            return (Criteria) this;
        }

        public Criteria andTcasesFailGreaterThan(Integer value) {
            addCriterion("tcases_fail >", value, "tcasesFail");
            return (Criteria) this;
        }

        public Criteria andTcasesFailGreaterThanOrEqualTo(Integer value) {
            addCriterion("tcases_fail >=", value, "tcasesFail");
            return (Criteria) this;
        }

        public Criteria andTcasesFailLessThan(Integer value) {
            addCriterion("tcases_fail <", value, "tcasesFail");
            return (Criteria) this;
        }

        public Criteria andTcasesFailLessThanOrEqualTo(Integer value) {
            addCriterion("tcases_fail <=", value, "tcasesFail");
            return (Criteria) this;
        }

        public Criteria andTcasesFailIn(List<Integer> values) {
            addCriterion("tcases_fail in", values, "tcasesFail");
            return (Criteria) this;
        }

        public Criteria andTcasesFailNotIn(List<Integer> values) {
            addCriterion("tcases_fail not in", values, "tcasesFail");
            return (Criteria) this;
        }

        public Criteria andTcasesFailBetween(Integer value1, Integer value2) {
            addCriterion("tcases_fail between", value1, value2, "tcasesFail");
            return (Criteria) this;
        }

        public Criteria andTcasesFailNotBetween(Integer value1, Integer value2) {
            addCriterion("tcases_fail not between", value1, value2, "tcasesFail");
            return (Criteria) this;
        }

        public Criteria andTstepsTotalIsNull() {
            addCriterion("tsteps_total is null");
            return (Criteria) this;
        }

        public Criteria andTstepsTotalIsNotNull() {
            addCriterion("tsteps_total is not null");
            return (Criteria) this;
        }

        public Criteria andTstepsTotalEqualTo(Integer value) {
            addCriterion("tsteps_total =", value, "tstepsTotal");
            return (Criteria) this;
        }

        public Criteria andTstepsTotalNotEqualTo(Integer value) {
            addCriterion("tsteps_total <>", value, "tstepsTotal");
            return (Criteria) this;
        }

        public Criteria andTstepsTotalGreaterThan(Integer value) {
            addCriterion("tsteps_total >", value, "tstepsTotal");
            return (Criteria) this;
        }

        public Criteria andTstepsTotalGreaterThanOrEqualTo(Integer value) {
            addCriterion("tsteps_total >=", value, "tstepsTotal");
            return (Criteria) this;
        }

        public Criteria andTstepsTotalLessThan(Integer value) {
            addCriterion("tsteps_total <", value, "tstepsTotal");
            return (Criteria) this;
        }

        public Criteria andTstepsTotalLessThanOrEqualTo(Integer value) {
            addCriterion("tsteps_total <=", value, "tstepsTotal");
            return (Criteria) this;
        }

        public Criteria andTstepsTotalIn(List<Integer> values) {
            addCriterion("tsteps_total in", values, "tstepsTotal");
            return (Criteria) this;
        }

        public Criteria andTstepsTotalNotIn(List<Integer> values) {
            addCriterion("tsteps_total not in", values, "tstepsTotal");
            return (Criteria) this;
        }

        public Criteria andTstepsTotalBetween(Integer value1, Integer value2) {
            addCriterion("tsteps_total between", value1, value2, "tstepsTotal");
            return (Criteria) this;
        }

        public Criteria andTstepsTotalNotBetween(Integer value1, Integer value2) {
            addCriterion("tsteps_total not between", value1, value2, "tstepsTotal");
            return (Criteria) this;
        }

        public Criteria andTstepsFailuresIsNull() {
            addCriterion("tsteps_failures is null");
            return (Criteria) this;
        }

        public Criteria andTstepsFailuresIsNotNull() {
            addCriterion("tsteps_failures is not null");
            return (Criteria) this;
        }

        public Criteria andTstepsFailuresEqualTo(Integer value) {
            addCriterion("tsteps_failures =", value, "tstepsFailures");
            return (Criteria) this;
        }

        public Criteria andTstepsFailuresNotEqualTo(Integer value) {
            addCriterion("tsteps_failures <>", value, "tstepsFailures");
            return (Criteria) this;
        }

        public Criteria andTstepsFailuresGreaterThan(Integer value) {
            addCriterion("tsteps_failures >", value, "tstepsFailures");
            return (Criteria) this;
        }

        public Criteria andTstepsFailuresGreaterThanOrEqualTo(Integer value) {
            addCriterion("tsteps_failures >=", value, "tstepsFailures");
            return (Criteria) this;
        }

        public Criteria andTstepsFailuresLessThan(Integer value) {
            addCriterion("tsteps_failures <", value, "tstepsFailures");
            return (Criteria) this;
        }

        public Criteria andTstepsFailuresLessThanOrEqualTo(Integer value) {
            addCriterion("tsteps_failures <=", value, "tstepsFailures");
            return (Criteria) this;
        }

        public Criteria andTstepsFailuresIn(List<Integer> values) {
            addCriterion("tsteps_failures in", values, "tstepsFailures");
            return (Criteria) this;
        }

        public Criteria andTstepsFailuresNotIn(List<Integer> values) {
            addCriterion("tsteps_failures not in", values, "tstepsFailures");
            return (Criteria) this;
        }

        public Criteria andTstepsFailuresBetween(Integer value1, Integer value2) {
            addCriterion("tsteps_failures between", value1, value2, "tstepsFailures");
            return (Criteria) this;
        }

        public Criteria andTstepsFailuresNotBetween(Integer value1, Integer value2) {
            addCriterion("tsteps_failures not between", value1, value2, "tstepsFailures");
            return (Criteria) this;
        }

        public Criteria andTstepsErrorsIsNull() {
            addCriterion("tsteps_errors is null");
            return (Criteria) this;
        }

        public Criteria andTstepsErrorsIsNotNull() {
            addCriterion("tsteps_errors is not null");
            return (Criteria) this;
        }

        public Criteria andTstepsErrorsEqualTo(Integer value) {
            addCriterion("tsteps_errors =", value, "tstepsErrors");
            return (Criteria) this;
        }

        public Criteria andTstepsErrorsNotEqualTo(Integer value) {
            addCriterion("tsteps_errors <>", value, "tstepsErrors");
            return (Criteria) this;
        }

        public Criteria andTstepsErrorsGreaterThan(Integer value) {
            addCriterion("tsteps_errors >", value, "tstepsErrors");
            return (Criteria) this;
        }

        public Criteria andTstepsErrorsGreaterThanOrEqualTo(Integer value) {
            addCriterion("tsteps_errors >=", value, "tstepsErrors");
            return (Criteria) this;
        }

        public Criteria andTstepsErrorsLessThan(Integer value) {
            addCriterion("tsteps_errors <", value, "tstepsErrors");
            return (Criteria) this;
        }

        public Criteria andTstepsErrorsLessThanOrEqualTo(Integer value) {
            addCriterion("tsteps_errors <=", value, "tstepsErrors");
            return (Criteria) this;
        }

        public Criteria andTstepsErrorsIn(List<Integer> values) {
            addCriterion("tsteps_errors in", values, "tstepsErrors");
            return (Criteria) this;
        }

        public Criteria andTstepsErrorsNotIn(List<Integer> values) {
            addCriterion("tsteps_errors not in", values, "tstepsErrors");
            return (Criteria) this;
        }

        public Criteria andTstepsErrorsBetween(Integer value1, Integer value2) {
            addCriterion("tsteps_errors between", value1, value2, "tstepsErrors");
            return (Criteria) this;
        }

        public Criteria andTstepsErrorsNotBetween(Integer value1, Integer value2) {
            addCriterion("tsteps_errors not between", value1, value2, "tstepsErrors");
            return (Criteria) this;
        }

        public Criteria andTstepsSkippedIsNull() {
            addCriterion("tsteps_skipped is null");
            return (Criteria) this;
        }

        public Criteria andTstepsSkippedIsNotNull() {
            addCriterion("tsteps_skipped is not null");
            return (Criteria) this;
        }

        public Criteria andTstepsSkippedEqualTo(Integer value) {
            addCriterion("tsteps_skipped =", value, "tstepsSkipped");
            return (Criteria) this;
        }

        public Criteria andTstepsSkippedNotEqualTo(Integer value) {
            addCriterion("tsteps_skipped <>", value, "tstepsSkipped");
            return (Criteria) this;
        }

        public Criteria andTstepsSkippedGreaterThan(Integer value) {
            addCriterion("tsteps_skipped >", value, "tstepsSkipped");
            return (Criteria) this;
        }

        public Criteria andTstepsSkippedGreaterThanOrEqualTo(Integer value) {
            addCriterion("tsteps_skipped >=", value, "tstepsSkipped");
            return (Criteria) this;
        }

        public Criteria andTstepsSkippedLessThan(Integer value) {
            addCriterion("tsteps_skipped <", value, "tstepsSkipped");
            return (Criteria) this;
        }

        public Criteria andTstepsSkippedLessThanOrEqualTo(Integer value) {
            addCriterion("tsteps_skipped <=", value, "tstepsSkipped");
            return (Criteria) this;
        }

        public Criteria andTstepsSkippedIn(List<Integer> values) {
            addCriterion("tsteps_skipped in", values, "tstepsSkipped");
            return (Criteria) this;
        }

        public Criteria andTstepsSkippedNotIn(List<Integer> values) {
            addCriterion("tsteps_skipped not in", values, "tstepsSkipped");
            return (Criteria) this;
        }

        public Criteria andTstepsSkippedBetween(Integer value1, Integer value2) {
            addCriterion("tsteps_skipped between", value1, value2, "tstepsSkipped");
            return (Criteria) this;
        }

        public Criteria andTstepsSkippedNotBetween(Integer value1, Integer value2) {
            addCriterion("tsteps_skipped not between", value1, value2, "tstepsSkipped");
            return (Criteria) this;
        }

        public Criteria andTstepsExpectedFailuresIsNull() {
            addCriterion("tsteps_expected_failures is null");
            return (Criteria) this;
        }

        public Criteria andTstepsExpectedFailuresIsNotNull() {
            addCriterion("tsteps_expected_failures is not null");
            return (Criteria) this;
        }

        public Criteria andTstepsExpectedFailuresEqualTo(Integer value) {
            addCriterion("tsteps_expected_failures =", value, "tstepsExpectedFailures");
            return (Criteria) this;
        }

        public Criteria andTstepsExpectedFailuresNotEqualTo(Integer value) {
            addCriterion("tsteps_expected_failures <>", value, "tstepsExpectedFailures");
            return (Criteria) this;
        }

        public Criteria andTstepsExpectedFailuresGreaterThan(Integer value) {
            addCriterion("tsteps_expected_failures >", value, "tstepsExpectedFailures");
            return (Criteria) this;
        }

        public Criteria andTstepsExpectedFailuresGreaterThanOrEqualTo(Integer value) {
            addCriterion("tsteps_expected_failures >=", value, "tstepsExpectedFailures");
            return (Criteria) this;
        }

        public Criteria andTstepsExpectedFailuresLessThan(Integer value) {
            addCriterion("tsteps_expected_failures <", value, "tstepsExpectedFailures");
            return (Criteria) this;
        }

        public Criteria andTstepsExpectedFailuresLessThanOrEqualTo(Integer value) {
            addCriterion("tsteps_expected_failures <=", value, "tstepsExpectedFailures");
            return (Criteria) this;
        }

        public Criteria andTstepsExpectedFailuresIn(List<Integer> values) {
            addCriterion("tsteps_expected_failures in", values, "tstepsExpectedFailures");
            return (Criteria) this;
        }

        public Criteria andTstepsExpectedFailuresNotIn(List<Integer> values) {
            addCriterion("tsteps_expected_failures not in", values, "tstepsExpectedFailures");
            return (Criteria) this;
        }

        public Criteria andTstepsExpectedFailuresBetween(Integer value1, Integer value2) {
            addCriterion("tsteps_expected_failures between", value1, value2, "tstepsExpectedFailures");
            return (Criteria) this;
        }

        public Criteria andTstepsExpectedFailuresNotBetween(Integer value1, Integer value2) {
            addCriterion("tsteps_expected_failures not between", value1, value2, "tstepsExpectedFailures");
            return (Criteria) this;
        }

        public Criteria andTstepsUnexpectedSuccessesIsNull() {
            addCriterion("tsteps_unexpected_successes is null");
            return (Criteria) this;
        }

        public Criteria andTstepsUnexpectedSuccessesIsNotNull() {
            addCriterion("tsteps_unexpected_successes is not null");
            return (Criteria) this;
        }

        public Criteria andTstepsUnexpectedSuccessesEqualTo(Integer value) {
            addCriterion("tsteps_unexpected_successes =", value, "tstepsUnexpectedSuccesses");
            return (Criteria) this;
        }

        public Criteria andTstepsUnexpectedSuccessesNotEqualTo(Integer value) {
            addCriterion("tsteps_unexpected_successes <>", value, "tstepsUnexpectedSuccesses");
            return (Criteria) this;
        }

        public Criteria andTstepsUnexpectedSuccessesGreaterThan(Integer value) {
            addCriterion("tsteps_unexpected_successes >", value, "tstepsUnexpectedSuccesses");
            return (Criteria) this;
        }

        public Criteria andTstepsUnexpectedSuccessesGreaterThanOrEqualTo(Integer value) {
            addCriterion("tsteps_unexpected_successes >=", value, "tstepsUnexpectedSuccesses");
            return (Criteria) this;
        }

        public Criteria andTstepsUnexpectedSuccessesLessThan(Integer value) {
            addCriterion("tsteps_unexpected_successes <", value, "tstepsUnexpectedSuccesses");
            return (Criteria) this;
        }

        public Criteria andTstepsUnexpectedSuccessesLessThanOrEqualTo(Integer value) {
            addCriterion("tsteps_unexpected_successes <=", value, "tstepsUnexpectedSuccesses");
            return (Criteria) this;
        }

        public Criteria andTstepsUnexpectedSuccessesIn(List<Integer> values) {
            addCriterion("tsteps_unexpected_successes in", values, "tstepsUnexpectedSuccesses");
            return (Criteria) this;
        }

        public Criteria andTstepsUnexpectedSuccessesNotIn(List<Integer> values) {
            addCriterion("tsteps_unexpected_successes not in", values, "tstepsUnexpectedSuccesses");
            return (Criteria) this;
        }

        public Criteria andTstepsUnexpectedSuccessesBetween(Integer value1, Integer value2) {
            addCriterion("tsteps_unexpected_successes between", value1, value2, "tstepsUnexpectedSuccesses");
            return (Criteria) this;
        }

        public Criteria andTstepsUnexpectedSuccessesNotBetween(Integer value1, Integer value2) {
            addCriterion("tsteps_unexpected_successes not between", value1, value2, "tstepsUnexpectedSuccesses");
            return (Criteria) this;
        }

        public Criteria andTstepsSuccessesIsNull() {
            addCriterion("tsteps_successes is null");
            return (Criteria) this;
        }

        public Criteria andTstepsSuccessesIsNotNull() {
            addCriterion("tsteps_successes is not null");
            return (Criteria) this;
        }

        public Criteria andTstepsSuccessesEqualTo(Integer value) {
            addCriterion("tsteps_successes =", value, "tstepsSuccesses");
            return (Criteria) this;
        }

        public Criteria andTstepsSuccessesNotEqualTo(Integer value) {
            addCriterion("tsteps_successes <>", value, "tstepsSuccesses");
            return (Criteria) this;
        }

        public Criteria andTstepsSuccessesGreaterThan(Integer value) {
            addCriterion("tsteps_successes >", value, "tstepsSuccesses");
            return (Criteria) this;
        }

        public Criteria andTstepsSuccessesGreaterThanOrEqualTo(Integer value) {
            addCriterion("tsteps_successes >=", value, "tstepsSuccesses");
            return (Criteria) this;
        }

        public Criteria andTstepsSuccessesLessThan(Integer value) {
            addCriterion("tsteps_successes <", value, "tstepsSuccesses");
            return (Criteria) this;
        }

        public Criteria andTstepsSuccessesLessThanOrEqualTo(Integer value) {
            addCriterion("tsteps_successes <=", value, "tstepsSuccesses");
            return (Criteria) this;
        }

        public Criteria andTstepsSuccessesIn(List<Integer> values) {
            addCriterion("tsteps_successes in", values, "tstepsSuccesses");
            return (Criteria) this;
        }

        public Criteria andTstepsSuccessesNotIn(List<Integer> values) {
            addCriterion("tsteps_successes not in", values, "tstepsSuccesses");
            return (Criteria) this;
        }

        public Criteria andTstepsSuccessesBetween(Integer value1, Integer value2) {
            addCriterion("tsteps_successes between", value1, value2, "tstepsSuccesses");
            return (Criteria) this;
        }

        public Criteria andTstepsSuccessesNotBetween(Integer value1, Integer value2) {
            addCriterion("tsteps_successes not between", value1, value2, "tstepsSuccesses");
            return (Criteria) this;
        }

        public Criteria andDurationIsNull() {
            addCriterion("duration is null");
            return (Criteria) this;
        }

        public Criteria andDurationIsNotNull() {
            addCriterion("duration is not null");
            return (Criteria) this;
        }

        public Criteria andDurationEqualTo(Double value) {
            addCriterion("duration =", value, "duration");
            return (Criteria) this;
        }

        public Criteria andDurationNotEqualTo(Double value) {
            addCriterion("duration <>", value, "duration");
            return (Criteria) this;
        }

        public Criteria andDurationGreaterThan(Double value) {
            addCriterion("duration >", value, "duration");
            return (Criteria) this;
        }

        public Criteria andDurationGreaterThanOrEqualTo(Double value) {
            addCriterion("duration >=", value, "duration");
            return (Criteria) this;
        }

        public Criteria andDurationLessThan(Double value) {
            addCriterion("duration <", value, "duration");
            return (Criteria) this;
        }

        public Criteria andDurationLessThanOrEqualTo(Double value) {
            addCriterion("duration <=", value, "duration");
            return (Criteria) this;
        }

        public Criteria andDurationIn(List<Double> values) {
            addCriterion("duration in", values, "duration");
            return (Criteria) this;
        }

        public Criteria andDurationNotIn(List<Double> values) {
            addCriterion("duration not in", values, "duration");
            return (Criteria) this;
        }

        public Criteria andDurationBetween(Double value1, Double value2) {
            addCriterion("duration between", value1, value2, "duration");
            return (Criteria) this;
        }

        public Criteria andDurationNotBetween(Double value1, Double value2) {
            addCriterion("duration not between", value1, value2, "duration");
            return (Criteria) this;
        }

        public Criteria andTestLogIdIsNull() {
            addCriterion("test_log_id is null");
            return (Criteria) this;
        }

        public Criteria andTestLogIdIsNotNull() {
            addCriterion("test_log_id is not null");
            return (Criteria) this;
        }

        public Criteria andTestLogIdEqualTo(String value) {
            addCriterion("test_log_id =", value, "testLogId");
            return (Criteria) this;
        }

        public Criteria andTestLogIdNotEqualTo(String value) {
            addCriterion("test_log_id <>", value, "testLogId");
            return (Criteria) this;
        }

        public Criteria andTestLogIdGreaterThan(String value) {
            addCriterion("test_log_id >", value, "testLogId");
            return (Criteria) this;
        }

        public Criteria andTestLogIdGreaterThanOrEqualTo(String value) {
            addCriterion("test_log_id >=", value, "testLogId");
            return (Criteria) this;
        }

        public Criteria andTestLogIdLessThan(String value) {
            addCriterion("test_log_id <", value, "testLogId");
            return (Criteria) this;
        }

        public Criteria andTestLogIdLessThanOrEqualTo(String value) {
            addCriterion("test_log_id <=", value, "testLogId");
            return (Criteria) this;
        }

        public Criteria andTestLogIdLike(String value) {
            addCriterion("test_log_id like", value, "testLogId");
            return (Criteria) this;
        }

        public Criteria andTestLogIdNotLike(String value) {
            addCriterion("test_log_id not like", value, "testLogId");
            return (Criteria) this;
        }

        public Criteria andTestLogIdIn(List<String> values) {
            addCriterion("test_log_id in", values, "testLogId");
            return (Criteria) this;
        }

        public Criteria andTestLogIdNotIn(List<String> values) {
            addCriterion("test_log_id not in", values, "testLogId");
            return (Criteria) this;
        }

        public Criteria andTestLogIdBetween(String value1, String value2) {
            addCriterion("test_log_id between", value1, value2, "testLogId");
            return (Criteria) this;
        }

        public Criteria andTestLogIdNotBetween(String value1, String value2) {
            addCriterion("test_log_id not between", value1, value2, "testLogId");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}