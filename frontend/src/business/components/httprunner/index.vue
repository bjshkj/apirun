<template>
  <el-col>
    <http-runner-header-menus/>
    <div>
      <transition>
        <keep-alive>
          <router-view :baseUrl="baseUrl"/>
        </keep-alive>
      </transition>
    </div>
  </el-col>
</template>
<script>

  import HttpRunnerHeaderMenus from "./HttpRunnerHeaderMenus";

  export default {
    name: "HttpRunnerIndex",
    components: {HttpRunnerHeaderMenus},
    data() {
      return {
        baseUrl: "httprunner"
      }
    }
  }

</script>

<style scoped>

</style>
