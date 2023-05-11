<template>
  <el-card class="table-card" body-style="padding:10px;" v-loading="result.loading">
    <el-row>
      <el-col :span="12">
          <span class="title">
            平台用例数变化情况
          </span>
      </el-col>
      <el-col :span="12">
        <el-select v-model="value1" placeholder="请选择" class="ms-col-type select" style="width: 100px" @change="handleChangeType">
          <el-option
            v-for="item in optionType"
            :key="item.value"
            :label="item.label"
            :value="item.value">
          </el-option>
        </el-select>
        <span style="margin: 10px 10px 10px; float:right;">|</span>
        <el-select v-model="value" placeholder="请选择" class="ms-col-type select" style="width: 100px"  @change="handleChange">
          <el-option
            v-for="item in options"
            :key="item.value"
            :label="item.label"
            :value="item.value">
          </el-option>
        </el-select>
      </el-col>
    </el-row>
    <ms-container>
      <ms-chart ref="caseChart" class="chart-config" :options="getCasesChart()" :autoresize="true" style="width: 100%"/>
    </ms-container>
  </el-card>
</template>

<script>
import MsChart from "@common/chart/MsChart";
import MsContainer from "@common/components/MsContainer";

export default {
  name: "CasesCard",
  components: {MsContainer, MsChart},
  props: {
    value2: {
      type: Array,
      required: true
    }
  },
  data(){
    return {
      result: {},
      options: [
        {
          value: 'D',
          label: '按天'
        },
        {
          value: 'W',
          label: '按周'
        },
        {
          value: 'M',
          label: '按月'
        }
      ],
      optionType: [
        {
          value: '新增',
          label: '新增'
        },
        {
          value: '修改',
          label: '修改'
        },
        {
          value: '删除',
          label: '删除'
        }
      ],
      value: '按天',
      value1: '新增',
      workspaceDate:{},
      requestData: {
        beginTime: this.value2[0].toString(),
        endTime: this.value2[1].toString(),
        workspaceId: '',
        dataSpan: '',
      },
      xlist: [],
      ylist: [],
      apiList: [],
      caseList: [],
      httpRunnerList: [],
      perList: [],
      sceList: [],
      operationType: ''
    }
  },
  created() {
    this.getDataSpan();
    this.getChartData();
  },
  methods: {
    handleChange(val){
      this.requestData.dataSpan = val;
      this.value1 = '新增';
      this.getChartData();
    },
    handleChangeType(val){
      this.operationType = val;
      this.ylist.forEach(item => {
        if (item.operationType === val){
          this.apiList = item.apiNum;
          this.caseList = item.caseNum;
          this.httpRunnerList = item.httprunnerNum;
          this.perList = item.perNum;
          this.sceList = item.sceNum;
        }
      })
    },
    getDataSpan(){
      const dateBegin = new Date(this.requestData.beginTime).getTime();
      const dateEnd = new Date(this.requestData.endTime).getTime();
      const dateDiff = dateEnd - dateBegin;
      const dayDiff = Math.floor(dateDiff / (24 * 3600 * 1000))
      if (parseInt(dayDiff) >= 0 && parseInt(dayDiff) < 29){
        this.requestData.dataSpan = 'D';
        this.value = '按天';
      }else if (parseInt(dayDiff) >= 29 && parseInt(dayDiff) < 89){
        this.requestData.dataSpan = 'W';
        this.value = '按周';
      }else if (parseInt(dayDiff) >= 89){
        this.requestData.dataSpan = 'M';
        this.value = '按月';
      }
    },
    getChartData(){
      this.result = this.$post("/report/statistics/getChartData", this.requestData, response => {
        this.xlist = response.data.xlist;
        this.ylist = response.data.ylists;
        this.ylist.forEach(item => {
          if (item.operationType === "新增"){
            this.apiList = item.apiNum;
            this.caseList = item.caseNum;
            this.httpRunnerList = item.httprunnerNum;
            this.perList = item.perNum;
            this.sceList = item.sceNum;
          }
        })
      })
    },
    getCasesChart(){
      let option = {
        tooltip: {
          trigger: 'axis'
        },
        legend: {
          data: ['接口', '接口用例', '场景用例', '性能用例', 'httprunner用例'],
          itemWidth: 30,
          itemHeight: 20,
          padding: 10,
          textStyle: {
            fontSize: 15
          }
        },
        color: ["#32cd32", "#6495ed", "#ff7f50", "#87cefa", "#da70d6"],
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: this.xlist
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            name: '接口',
            type: 'line',
            data: this.apiList
          },
          {
            name: '接口用例',
            type: 'line',
            data: this.caseList
          },
          {
            name: '场景用例',
            type: 'line',
            data: this.sceList
          },
          {
            name: '性能用例',
            type: 'line',
            data: this.perList
          },
          {
            name: 'httprunner用例',
            type: 'line',
            data: this.httpRunnerList
          }
        ]
      };
      return option;
    }
  }
}
</script>

<style scoped>
.select {
  float: right;
  padding: 3px 0;
}
.title {
  font-size: 16px;
  font-weight: 500;
  margin-top: 0;
  text-overflow: ellipsis;
  overflow: hidden;
  white-space: nowrap;
}
</style>
