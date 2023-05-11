<template>
  <el-card class="table-card" body-style="padding:10px;" v-loading="result.loading">
    <el-row>
      <el-col :span="12">
        <div class="title">
          <span>平台项目统计</span>
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
      <ms-chart ref="jmeterChart" class="chart-config" :options="getJmeterChart()" :autoresize="true" style="width: 50%"/>
      <ms-chart ref="httpRunnerChart" class="chart-config" :options="getHttpRunnerChart()" :autoresize="true" style="width: 50%"/>
    </el-container>
  </el-card>
</template>

<script>
import MsChart from "@common/chart/MsChart";

export default {
  name: "ProjectTypeCard",
  components: {MsChart},
  props: {
    value2: {
      type: Array,
      required: true
    }
  },
  data(){
    return {
      result: {},
      options: [{
        value: '',
        label: '平台'
      }],
      value: '',
      workspaceDate:{},
      requestData: {
        beginTime: this.value2[0].toString(),
        endTime: this.value2[1].toString(),
        workspaceId: ''
      },
      jmeterNum: 0,
      httpRunnerNum: 0,
      newJmeterNum: 0,
      newHttpRunnerNum: 0
    }
  },
  created() {
    this.getWorkspaceList();
    this.getProjectType();
  },
  methods: {
    handleChange(val){
      this.requestData.workspaceId = val;
      this.getProjectType();
    },
    getWorkspaceList(){
      this.$get("/workspace/list", response => {
        response.data.forEach(item => {
          this.workspaceDate = item;
          this.options.push({value: this.workspaceDate.id,label: this.workspaceDate.name})
        })
      })
    },
    getProjectType(){
      this.result = this.$post("/report/statistics/count/projectType", this.requestData, response => {
        if (response.data.length !== 0){
          response.data.forEach(item => {
            if (item.projectType === 'jmeter'){
              this.jmeterNum = item.number;
              this.newJmeterNum = item.newNum;
            }else if (item.projectType === 'httprunner'){
              this.httpRunnerNum = item.number;
              this.newHttpRunnerNum = item.newNum;
            }
          })
        }
      })
    },
    getJmeterChart(){
      let option = {
        /*title: {
          text: '项目统计',
          left: 'center',
          textStyle: {
            fontSize: 25
          }
        },*/
        tooltip: {
          trigger: 'item',
          formatter: "{a} <br>{b} : {c} ({d}%)"
        },
        //color:['#FF0000', '#FFC851', '#00FF00','#45C2E0','#9933FA'],
        color:["#32cd32", "#6495ed"],
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
            /*label: {
              formatter: '{b}: {@[' + this.jmeterNum + ']} ({d}%)'
            },*/
            label: {
              normal: {
                show: true,
                position: 'center',
                color:'#4c4a4a',
                formatter: '{total|' + this.jmeterNum +'}'+ '\n\r' + '{active|Jmeter项目数}',
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
            lableLine: {
              normal: {
                show: false
              },
              emphasis: {
                show: true
              },
              tooltip: {
                show: false
              }
            },
            data: [
              { value: this.newJmeterNum, name: '新增项目' },
              { value: this.jmeterNum - this.newJmeterNum, name: 'jmeter' }
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
    },
    getHttpRunnerChart(){
      let option = {
        tooltip: {
          trigger: 'item',
          formatter: "{a} <br>{b} : {c} ({d}%)"
        },
        color:["#32cd32", "#6495ed"],
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
                formatter: '{total|' + this.httpRunnerNum +'}'+ '\n\r' + '{active|HttpRunner项目数}',
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
              { value: this.newHttpRunnerNum, name: '新增项目' },
              { value: this.httpRunnerNum - this.newHttpRunnerNum, name: 'httprunner' }
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
