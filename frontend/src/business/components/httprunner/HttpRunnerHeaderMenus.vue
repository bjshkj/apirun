<template>
  <div id="menu-bar" v-if="isRouterAlive">
    <el-row type="flex">
      <http-runner-project-change :project-name="currentProject"/>
<!--      <project-change :project-name="currentProject"/>-->
      <el-col :span="14">
        <el-menu class="header-menu" :unique-opened="true" mode="horizontal" router :default-active='$route.path'>

          <el-menu-item :index="'/httprunner/repo'">
            代码仓库
          </el-menu-item>

          <el-menu-item :index="'/httprunner/report'" >
            报告列表
          </el-menu-item>
        </el-menu>
      </el-col>
    </el-row>
  </div>

</template>

<script>
import HttpRunnerProjectChange from "../common/head/HttpRunnerProjectSwitch";
import SearchList from "../common/head/SearchList";
import ProjectChange from "../common/head/ProjectSwitch";

export default {
  name: "HttpRunnerHeaderMenus",
  components: {HttpRunnerProjectChange, SearchList, ProjectChange},
  data() {
    return {
      isRouterAlive: true,
      currentProject: ''
    };
  },
  methods: {
    reload() {
      this.isRouterAlive = false;
      this.$nextTick(function () {
        this.isRouterAlive = true;
      });
    },
  },
  mounted() {

  },
  beforeDestroy() {

  }
};

</script>

<style scoped>
#menu-bar {
  border-bottom: 1px solid #E6E6E6;
  background-color: #FFF;
}

.deactivation >>> .el-submenu__title {
  border-bottom: white !important;
}
</style>
