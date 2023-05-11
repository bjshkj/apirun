<template>
  <el-card>
    <div class="block" align="right">
      <el-date-picker
        v-model="value2"
        type="daterange"
        align="right"
        unlink-panels
        range-separator="至"
        start-placeholder="开始日期"
        end-placeholder="结束日期"
        :picker-options="pickerOptions">
      </el-date-picker>
    </div>
  </el-card>
</template>

<script>

export default {
  name: "StatisticsReportHeaderMenus",
  data(){
    return {
      pickerOptions: {
        shortcuts: [{
          text: '最近一周',
          onClick(picker) {
            const end = new Date();
            const start = new Date();
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
            picker.$emit('pick', [start, end]);
          }
        }, {
          text: '最近一个月',
          onClick(picker) {
            const end = new Date();
            const start = new Date();
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
            picker.$emit('pick', [start, end]);
          }
        }, {
          text: '最近三个月',
          onClick(picker) {
            const end = new Date();
            const start = new Date();
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 90);
            picker.$emit('pick', [start, end]);
          }
        }]
      },
      value1: '',
      value2: []
    }
  },
  created() {
    this.getTime();
  },
  methods: {
    getTime(){
      const end = new Date();
      const start = new Date();
      this.value2.push(start.setTime(start.getTime() - 3600 * 1000 * 24 * 30));
      this.value2.push(end);
    }
  }
}
</script>

<style scoped>

</style>
