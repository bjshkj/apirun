<template>
  <el-col>
<!--    <statistics-report-header-menus/>-->
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

  import StatisticsReportHeaderMenus from "./StatisticsReportHeaderMenus";

  export default {
    name: "index",
    components: {StatisticsReportHeaderMenus},
    data() {
      return {
          baseUrl: "statistics"
      }
    }
  }
</script>

<style scoped>

</style>
