<template>
  <div>
    <ms-container>
      <ms-aside-container>
        <el-alert title="请先选择一个项目" type="error" v-if="!projectId" style="margin-bottom: 10px"></el-alert>
        <template v-else>
          <el-row :gutter="5">
            <el-col :span="8">
              <el-select
                size="small"
                v-model="gitRef"
                placeholder="分支"
                filterable
                :default-first-option="true"
                @change="handleChangeGitRef"
              >
                <el-option
                  v-for="item in branchList"
                  :key="item.name"
                  :title="item.name"
                  :label="item.name"
                  :value="item.name">
                </el-option>
              </el-select>
            </el-col>
            <el-col :span="12">
              <el-select
                size="small"
                v-model="commitId"
                placeholder="commit"
                filterable
                :default-first-option="true"
                @change="handleChangeCommit"
              >
                <el-option
                  v-for="item in commitList"
                  :key="item.short_id"
                  :label="item.short_id"
                  :value="item.short_id">
                </el-option>
              </el-select>
            </el-col>
            <el-col :span="4">
              <el-button size="small" type="primary" icon="el-icon-caret-right"  :disabled="!checkedNodes.length" @click="openDialog" title="运行"></el-button>
            </el-col>
          </el-row>

          <el-tree
            v-if="isRouterAlive"
            v-loading="!gitRef || !commitId"
            ref="tree"
            class="tree"
            node-key="path"
            :check-strictly="true"
            show-checkbox
            @check-change="handleCheckChange"
            :highlight-current="true"
            :expand-on-click-node="false"
            :props="{
              label: 'name',
              isLeaf: 'isLeaf',
              children: 'children'
            }"
            :load="loadTreeNode"
            lazy
            empty-text="数据加载中……"
            @node-click="handleClickNode">
            <template #default="{ node, data }">
              <div class="tree-node">
                <div>
                  <i class="el-icon-folder" v-if="data.type === 'tree'" />
                  <i class="el-icon-document" v-else></i>
                  <span class="tree-node-text ellipsis" :title="node.label" >{{node.label}}</span>
                </div>

                <!-- <el-button type="text" icon="el-icon-caret-right" class="tree-node-icon" @click="runNode(data)"></el-button> -->
              </div>
            </template>
          </el-tree>
        </template>
      </ms-aside-container>

      <ms-main-container>
        <file-view
          ref="fileView"
          v-show="curNode && curNode.id"
        ></file-view>
        <el-empty
          v-show="!curNode"
          class="empty"
          :image-size="300"
          description="点击文件，即可预览"
        ></el-empty>
      </ms-main-container>
    </ms-container>

    <el-dialog
      title="创建任务"
      width="600px"
      :visible.sync="showDialog"
      :destroy-on-close="true"
      :close-on-click-modal="false"
      @close="closeDialog">

      <el-form
        :model="formData"
        label-position="right"
        label-width="90px"
        size="small"
        :rules="formRules"
        ref="form">
        <el-form-item label="任务名称" prop="name">
          <el-input class="ms-http-input" size="small" v-model="formData.name"/>
        </el-form-item>
        <el-form-item label="env文件" prop="env">
          <el-select class="ms-http-input" size="small" v-model="formData.env" style="width: 100%">
            <el-option v-for="item in envOptions" :key="item.path" :label="item.path" :value="item.path"/>
          </el-select>
        </el-form-item>
        <el-form-item label="hosts文件" prop="hosts">
          <el-select class="ms-http-input" size="small" v-model="formData.hosts" style="width: 100%">
            <el-option v-for="item in hostsOptions" :key="item.path" :label="item.path" :value="item.path"/>
          </el-select>
        </el-form-item>
      </el-form>

      <template v-slot:footer>
        <ms-dialog-footer
          @cancel="showDialog = false"
          @confirm="runNode">
        </ms-dialog-footer>
    </template>

    </el-dialog>
  </div>
</template>

<script>
import MsContainer from "@common/components/MsContainer";
import MsMainContainer from "@common/components/MsMainContainer";
import MsAsideContainer from "@common/components/MsAsideContainer";
import MsDialogFooter from "@common/components/MsDialogFooter";
import FileView from "./FileView";
import {getCurrentProjectID} from "@/common/js/utils";

export default {
  name: "httpRunnerRepo",
  components: {
    MsMainContainer,
    MsContainer,
    MsAsideContainer,
    MsDialogFooter,
    FileView
  },
  data() {
    return {
      // projectId: "404d087e-e1a8-4a25-a74c-f89693c26ecc",
      projectId: "",
      queryPath: "",
      gitRef: "",
      branchList: [],
      commitId: "",
      commitList: [],
      rootNodeData: { path: "", type: "tree" },
      isRouterAlive: true,
      curNode: null,
      checkedNodes: [],
      // dialog
      showDialog: false,
      formData: {
        name: "",
        env: "",
        hosts: ""
      },
      formRules: {
        name: [
          { required: true, message: this.$t('test_track.case.input_name'), trigger: 'blur' },
          { max: 100, message: this.$t('test_track.length_less_than') + '100', trigger: 'blur' }
        ],
        env: [
          { required: true, message: "请选择env文件路径", trigger: 'change' }
        ],
        hosts: [
          { required: true, message: "请选择hosts文件路径", trigger: 'change' }
        ],
      },
      envOptions: [],
      hostsOptions: [],
    }
  },
  computed: {
    /*projectId () {
      return this.$store.state.projectId;
    },*/
  },
  watch: {
    "$route.query": {
      handler (newQuery, oldQuery) {
        this.gitRef = newQuery.gitRef  || this.gitRef;
        if (!this.gitRef) {
          this.getBranchList();
        }
        this.queryPath = newQuery.queryPath ;
        this.commitId = newQuery.commitId || this.commitId;
        if (newQuery.gitRef !== oldQuery.gitRef || newQuery.commitId !== oldQuery.commitId) {
          this.reload();
        }

        this.focusTree();
      },
      deep: true
    }
  },
  created() {
    this.init()
    this.getBranchList()
  },

  methods: {
    init () {
      const query = this.$route.query;
      this.projectId = getCurrentProjectID();
      this.gitRef = query.gitRef
      this.commitId = query.commitId;
      this.queryPath = query.queryPath;
    },
    getBranchList() {
      if (!this.projectId)  return
      this.$get(`/gitLab/branch?id=${this.projectId}`, response => {
        this.branchList = response.data
        this.gitRef = this.$route.query.gitRef || this.branchList[0].name
        this.getCommitList()
        // this.setRouteQuery({ gitRef: this.gitRef });
      })
    },
    handleChangeGitRef (value) {
      //this.setRouteQuery({ gitRef: value, queryPath: "" });
      this.$get(`/gitLab/commitList?id=${this.projectId}&ref=${value}&page=1&per_page=10`, response => {
        this.commitList = response.data
        this.commitId = this.commitList[0].short_id
        this.setRouteQuery({ gitRef: value, commitId: this.commitId });
      })
    },
    getCommitList() {
      if (!this.projectId || !this.gitRef)  return

      this.$get(`/gitLab/commitList?id=${this.projectId}&ref=${this.gitRef}&page=1&per_page=10`, response => {
        this.commitList = response.data
        this.commitId = this.$route.query.commitId || this.commitList[0].short_id
        this.setRouteQuery({ gitRef: this.gitRef, commitId: this.commitId });
      })
    },
    handleChangeCommit (value) {
      this.setRouteQuery({ gitRef: this.gitRef, queryPath: "", commitId: value });
    },
    setRouteQuery (newQuery, { isReplace = false } = {}) {
      const config = {
        name: this.$route.name,
        params: this.$route.params,
        query: { ...this.$route.query, ...newQuery }
      };

      isReplace ? this.$router.replace(config) : this.$router.push(config);
    },
    loadTreeNode (node, resolve) {
      if (node.data?.children) {
        resolve(node.data.children);
        this.focusTree();
      } else if (!this.projectId) {
        resolve([])
      } else {
        const path = node?.level >= 1 ? node.data.path : "";
        const commitId = this.getCommitId();
        if (!commitId) {
          resolve([]);
        } else {
          this.$get(`/gitLab/repoTree?id=${this.projectId}&path=${path}&ref=${commitId}`, response => {
            const list = response.data.map(el => {
              return {
                ...el,
                isLeaf: el.type !== "tree"
              };
            });

            resolve(list);
            this.focusTree();
          });
        }
      }
    },
    focusTree () {
      this.$nextTick(() => {
        if (this.$refs.tree) {
          if (this.queryPath) {
            this.$refs.tree.setCurrentKey(this.queryPath);
            const node = this.$refs.tree.getNode(this.queryPath);
            if (node) {
              this.setCurrentList(node.data);
            } else {
              this.queryPath.split("/").forEach((path, index, arr) => {
                const fullPath = arr.slice(0, index + 1).join("/");
                this.expandNode(fullPath);
              });
            }
          } else { // 根节点，取消高亮
            this.$refs.tree.setCurrentKey(null);
            this.setCurrentList(this.rootNodeData);
          }
        }
      });
    },
    expandNode (path) {
      const node = this.$refs.tree.getNode(path);
      node && node.expand();
    },
    setCurrentList (node) {
      this.queryPath = node.path;

      if (node.type !== "tree") {
        this.curNode = node;
        this.$refs.fileView.loadData(this.projectId, node.path, this.gitRef, node)
      } else {
        this.curNode = null
      }
    },
    reload () {
      this.curNode = null;
      this.isRouterAlive = false;
      this.$nextTick(() => {
        this.isRouterAlive = true;
      });
    },
    handleCheckChange(data, checked, indeterminate) {
      this.checkedNodes = this.$refs.tree.getCheckedNodes()
    },
    handleClickNode(nodeData) {
      this.setRouteQuery({ queryPath: nodeData.path });
    },
    openDialog() {
      this.showDialog = true

      const commitId = this.getCommitId()

      this.$get(`/gitLab/repoTree?id=${this.projectId}&path=env&ref=${commitId}`, response => {
        this.envOptions = response.data
      })
      this.$get(`/gitLab/repoTree?id=${this.projectId}&path=hosts&ref=${commitId}`, response => {
        this.hostsOptions = response.data
      })
    },
    closeDialog() {
      this.$refs.form.resetFields()
      this.showDialog = false;
    },
    getCommitId () {
      let commitId;
      if (this.commitId) {
        commitId= this.commitId
      } else {
        const branch = this.branchList.find(el => el.name === this.gitRef)
        commitId = branch?.commit?.id
      }
      return commitId || this.gitRef
    },
    runNode(){
      this.$refs.form.validate((valid) => {
        if (valid) {
          this.$post("/httpruner/addHttpRunnerTask", {
            taskName: this.formData.name,
            projectId: this.projectId,
            gitVersion: this.getCommitId(),
            debugMode: "INFO",
            execCasePaths: this.checkedNodes.map(el => el.path).join(","),
            dotEnvPath: this.formData.env,
            hostsPath: this.formData.hosts,
            triggerModel: 'MANUAL' // 触发方式 MANUAL/手动 STREAM/流水线
          }, response => {
            this.showDialog = false
            this.$router.push({ name: "httpRunnerReportList" })
          });
        }
      })
    }
  }
};
</script>

<style scoped>
.tree {
  margin-top: 15px;
}

.tree-node {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.tree-node-text {
  width: 180px;
  padding: 0px 5px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* .tree-node-icon {
  font-size: 20px;
  visibility: hidden;
}

.tree-node:hover .tree-node-icon{
  visibility: visible;
}

.tree-node-icon:hover {
  color: #303133;
} */

.empty {
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
  text-align: center;
  box-sizing: border-box;
}
</style>
