<template>
  <el-card body-style="padding:10px;" class="table-card" v-loading="result.loading">
    <el-row>
      <el-col :span="12">
        <div class="title">
          <span>平台用例统计</span>
        </div>
      </el-col>
      <el-col :span="12">
        <el-select v-model="value" placeholder="请选择" class="select" @change="handleChange">
          <el-option
            v-for="item in options"
            :key="item.value"
            :label="item.label"
            :value="item.value">
          </el-option>
        </el-select>
      </el-col>
    </el-row>
    <el-container>
      <ms-chart ref="casesTotalChart" class="chart-config" :options="getCasesChart()" :autoresize="true" style="width: 100%"/>
    </el-container>
  </el-card>
</template>

<script>
import MsChart from "@common/chart/MsChart";

export default {
  name: "CasesTotalCard",
  components: {MsChart},
  props: {
    value2: {
      type: Array,
      required: true
    }
  },
  data() {
    return {
      result: {},
      options: [{
        value: '',
        label: '平台'
      }],
      value: '',
      workspaceDate: {},
      requestData: {
        beginTime: this.value2[0].toString(),
        endTime: this.value2[1].toString(),
        workspaceId: ''
      },
      data: {}
    }
  },
  created() {
    this.getWorkspaceList();
    this.getCasesTotal();
  },
  methods: {
    handleChange(val) {
      this.requestData.workspaceId = val;
      this.getCasesTotal();
    },
    getWorkspaceList() {
      this.$get("/workspace/list", response => {
        response.data.forEach(item => {
          this.workspaceDate = item;
          this.options.push({value: this.workspaceDate.id, label: this.workspaceDate.name})
        })
      })
    },
    getCasesTotal() {
      this.result = this.$post("/report/statistics/cases/total", this.requestData, response => {
        this.data = response.data;
      })
    },
    getCasesChart() {
      let total = this.data.apiNum + this.data.caseNum+this.data.httpRunnerNum+this.data.perNum+this.data.sceneNum
      let option = {
        tooltip: {
          trigger: 'item',
          formatter: "{a} <br>{b} : {c} ({d}%)"
        },
        //color:['#FF0000', '#FFC851', '#00FF00','#45C2E0','#9933FA'],
        color: ["#32cd32", "#6495ed", "#ff7f50", "#87cefa", "#da70d6"],
        legend: {
          orient: 'vertical',
          left: 'left',
          itemWidth: 30,
          itemHeight: 20,
          padding: 10,
          textStyle: {
            fontSize: 15
          }
        },
        series: [
          {
            name: 'Access From',
            type: 'pie',
            radius: ['40%', '70%'],
            label: {
              normal: {
                show: true,
                position: 'center',
                color:'#4c4a4a',
                formatter: '{total|' + total + '}'+ '\n\r' + '{active|全部用例数}',
                rich: {
                  total:{
                    fontSize: 35,
                    fontFamily : "微软雅黑",
                    color:'#454c5c'
                  },
                  active: {
                    fontFamily : "微软雅黑",
                    fontSize: 16,
                    color:'#6c7a89',
                    lineHeight:30,
                  },
                }
              },
              emphasis: {//中间文字显示
                show: true,
              }
            },
            data: [
              { value: this.data.apiNum, name: '接口数' },
              { value: this.data.caseNum, name: '接口用例' },
              { value: this.data.sceneNum, name: '场景用例' },
              { value: this.data.perNum, name: '性能用例' },
              { value: this.data.httpRunnerNum, name: 'httprunner用例' },
            ],
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            },
            itemStyle: {
              normal: {
                label: {
                  textStyle: {
                    fontSize: 13,
                    fontStyle: "normal"
                  }
                }
              }
            }
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
  width: 100px;
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
