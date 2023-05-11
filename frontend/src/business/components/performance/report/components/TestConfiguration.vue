<template>
  <el-tabs>
    <performance-pressure-config :is-read-only="true" :test="test" :report-id="reportId" :report="report"/>
  </el-tabs>
</template>

<script>
import PerformancePressureConfig from "@/business/components/performance/test/components/PerformancePressureConfig";

export default {
  name: "TestConfiguration",
  components: {PerformancePressureConfig},
  props: {
    test: Object,
    testId: String,
    reportId: String,
    report: Object,
  }
};
</script>

<style scoped>

</style>
