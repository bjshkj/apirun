<template>
  <div v-loading="result.loading">
    <el-tabs>
      <el-tab-pane v-for="(item,index) in instances" :key="index" :label="item" class="logging-content">
        <el-row>
          <el-col :span="12">
            <ms-chart ref="chart1" class="chart-config" :options="getCpuOption(item)" :autoresize="true"></ms-chart>
          </el-col>
          <el-col :span="12">
            <ms-chart ref="chart2" class="chart-config" :options="getMemoryOption(item)" :autoresize="true"></ms-chart>
          </el-col>
          <el-col :span="12">
            <ms-chart ref="chart3" class="chart-config" :options="getNetInOption(item)" :autoresize="true"></ms-chart>
          </el-col>
          <el-col :span="12">
            <ms-chart ref="chart4" class="chart-config" :options="getNetOutOption(item)" :autoresize="true"></ms-chart>
          </el-col>
          <el-col :span="12">
            <ms-chart ref="chart5" class="chart-config" :options="getDiskOption(item)" :autoresize="true"
                      v-if="item.includes('Redis') || item.includes('Mysql')"></ms-chart>
          </el-col>
          <el-col :span="12">
            <ms-chart ref="chart6" class="chart-config" :options="getOption(item)" :autoresize="true"
                      v-if="item.includes('Redis') || item.includes('Mysql')"></ms-chart>
          </el-col>
        </el-row>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>

import MsChart from "@/business/components/common/chart/MsChart";

export default {
  name: "MonitorCard",
  props: ['report'],
  components: {MsChart},
  data() {
    return {
      result: {},
      id: '',
      loading: false,
      instances: [],
      data: []
    };
  },
  created() {
    this.data = [];
    this.instances = [];
  },
  methods: {
    getResource() {
      /*this.result = this.$get("/metric/query/resource/" + this.report.id)
        .then(response => {
          this.instances = response.data.data;
        })
        .catch(() => {
        });

      this.$get("/metric/query/" + this.report.id)
        .then(result => {
          if (result) {
            this.data = result.data.data;
          }
        })
        .catch(() => {
        });*/
      this.result = this.$get("/hulk/query/resource/" + this.report.id)
        .then(response => {
          this.instances = response.data.data;
        })
        .catch(() => {
        });

      this.$get("/hulk/monitor/" + this.report.id)
        .then(result => {
          if (result) {
            this.data = result.data.data;
          }
        })
        .catch(() => {
        });
    },
    getMonitorData() {
      this.result = this.$get("/hulk/query/resource/" + this.report.id)
        .then(response => {
          this.instances = response.data.data;
        })
        .catch(() => {
        });
      this.$get("/hulk/monitorData/" + this.report.id)
        .then(result => {
          this.data = result.data.data;
        })
        .catch(() => {
        });
    },
    getCpuOption(id) {
      let xAxis = [];
      let yAxis = [];
      let legend = [];
      let text = '';
      let yname = '';
      this.data.map(d => {
        if ((d.targetLabel + ':' + d.target) === id && (d.pointerType === 'cpu' || d.pointerType === 'stat')) {
          text = d.pointerType;
          yname = d.numberIcalUnit;
          let t = JSON.parse(d.timeStamp)
          let y = JSON.parse(d.monitorData);
          let n = d.monitorName.substring(1, d.monitorName.length - 1).split(',');
          if (t && y){
            if (t.length > 0 && y.length > 0){
              t.forEach(item => {
                xAxis.push(this.parseTime(item));
              })
              y.forEach(item => {
                let index = y.indexOf(item);
                yAxis.push({
                  name: n[index],
                  data: item,
                  type: 'line'
                });
                legend.push(n[index]);
              })
            }
          }
        }
      });
      let option = {
        title: {
          left: 'center',
          text: text,
          textStyle: {
            color: '#8492a6'
          },
        },
        tooltip: {
          trigger: 'axis'
        },
        legend: {},
        xAxis: {
          type: 'category',
          data: xAxis
        },
        yAxis: {
          name: yname,
          type: 'value'
        },
        series: yAxis
      };
      this.$set(option.legend, "data", legend);
      this.$set(option.legend, "bottom", "10px");
      this.$set(option.legend, "type", "scroll");
      return option;
    },
    getMemoryOption(id) {
      let xAxis = [];
      let yAxis = [];
      let legend = [];
      let text = '';
      let yname = '';
      this.data.map(d => {
        if ((d.targetLabel + ':' + d.target) === id && (d.pointerType === 'mem' || d.pointerType === 'flow')) {
          text = d.pointerType;
          yname = d.numberIcalUnit;
          let t = JSON.parse(d.timeStamp)
          let y = JSON.parse(d.monitorData);
          let n = d.monitorName.substring(1, d.monitorName.length - 1).split(',');
          if (t && y){
            if (t.length > 0 && y.length > 0){
              t.forEach(item => {
                xAxis.push(this.parseTime(item));
              })
              y.forEach(item => {
                let index = y.indexOf(item);
                yAxis.push({
                  name: n[index],
                  data: item,
                  type: 'line'
                });
                legend.push(n[index]);
              })
            }
          }
        }
      });
      let option = {
        title: {
          left: 'center',
          text: text,
          textStyle: {
            color: '#8492a6'
          },
        },
        tooltip: {
          trigger: 'axis'
        },
        legend: {},
        xAxis: {
          type: 'category',
          data: xAxis
        },
        yAxis: {
          name: yname,
          type: 'value'
        },
        series: yAxis
      };
      this.$set(option.legend, "data", legend);
      this.$set(option.legend, "bottom", "10px");
      this.$set(option.legend, "type", "scroll");
      return option;
    },
    getNetInOption(id) {
      let xAxis = [];
      let yAxis = [];
      let legend = [];
      let text = '';
      let yname = '';
      this.data.map(d => {
        if ((d.targetLabel + ':' + d.target) === id &&
          (d.pointerType === 'net_in' || d.pointerType === 'conn')) {
          text = d.pointerType;
          yname = d.numberIcalUnit;
          let t = JSON.parse(d.timeStamp)
          let y = JSON.parse(d.monitorData);
          let n = d.monitorName.substring(1, d.monitorName.length - 1).split(',');
          if (t && y){
            if (t.length > 0 && y.length > 0){
              t.forEach(item => {
                xAxis.push(this.parseTime(item));
              })
              y.forEach(item => {
                let index = y.indexOf(item);
                yAxis.push({
                  name: n[index],
                  data: item,
                  type: 'line'
                });
                legend.push(n[index]);
              })
            }
          }
        }
      });
      let option = {
        title: {
          left: 'center',
          text: text,
          textStyle: {
            color: '#8492a6'
          },
        },
        tooltip: {
          trigger: 'axis'
        },
        legend: {},
        xAxis: {
          type: 'category',
          data: xAxis
        },
        yAxis: {
          type: 'value',
          name: yname
        },
        series: yAxis
      };
      this.$set(option.legend, "data", legend);
      this.$set(option.legend, "bottom", "10px");
      this.$set(option.legend, "type", "scroll");
      return option;
    },
    getNetOutOption(id) {
      let xAxis = [];
      let yAxis = [];
      let legend = [];
      let text = '';
      let yname = '';
      this.data.map(d => {
        if ((d.targetLabel + ':' + d.target) === id &&
          (d.pointerType === 'net_out' || d.pointerType === 'sdelay' || d.pointerType === 'key' || d.pointerType === 'queue')) {
          text = d.pointerType;
          yname = d.numberIcalUnit;
          let t = JSON.parse(d.timeStamp)
          let y = JSON.parse(d.monitorData);
          let n = d.monitorName.substring(1, d.monitorName.length - 1).split(',');
          if (t && y){
            if (t.length > 0 && y.length > 0){
              t.forEach(item => {
                xAxis.push(this.parseTime(item));
              })
              y.forEach(item => {
                let index = y.indexOf(item);
                yAxis.push({
                  name: n[index],
                  data: item,
                  type: 'line'
                });
                legend.push(n[index]);
              })
            }
          }
        }
      });
      let option = {
        title: {
          left: 'center',
          text: text,
          textStyle: {
            color: '#8492a6'
          },
        },
        tooltip: {
          trigger: 'axis'
        },
        legend: {},
        xAxis: {
          type: 'category',
          data: xAxis
        },
        yAxis: {
          type: 'value',
          name: yname
        },
        series: yAxis
      };
      this.$set(option.legend, "data", legend);
      this.$set(option.legend, "bottom", "10px");
      this.$set(option.legend, "type", "scroll");
      return option;
    },
    getDiskOption(id) {
      let xAxis = [];
      let yAxis = [];
      let legend = [];
      let text = '';
      let yname = '';
      this.data.map(d => {
        if ((d.targetLabel + ':' + d.target) === id && (d.pointerType === 'innodb_time' || d.pointerType === 'hit')) {
          text = d.pointerType;
          yname = d.numberIcalUnit;
          let t = JSON.parse(d.timeStamp)
          let y = JSON.parse(d.monitorData);
          let n = d.monitorName.substring(1, d.monitorName.length - 1).split(',');
          if (t && y){
            if (t.length > 0 && y.length > 0){
              t.forEach(item => {
                xAxis.push(this.parseTime(item));
              })
              y.forEach(item => {
                let index = y.indexOf(item);
                yAxis.push({
                  name: n[index],
                  data: item,
                  type: 'line'
                });
                legend.push(n[index]);
              })
            }
          }
        }
      });
      let option = {
        title: {
          left: 'center',
          text: text,
          textStyle: {
            color: '#8492a6'
          },
        },
        tooltip: {
          trigger: 'axis'
        },
        legend: {},
        xAxis: {
          type: 'category',
          data: xAxis
        },
        yAxis: {
          type: 'value',
          name: yname,
        },
        series: yAxis
      };
      this.$set(option.legend, "data", legend);
      this.$set(option.legend, "bottom", "10px");
      this.$set(option.legend, "type", "scroll");
      return option;
    },
    getOption(id) {
      let xAxis = [];
      let yAxis = [];
      let legend = [];
      let text = '';
      let yname = '';
      this.data.map(d => {
        if ((d.targetLabel + ':' + d.target) === id && (d.pointerType === 'innodb_stat' || d.pointerType === 'frag')) {
          text = d.pointerType;
          yname = d.numberIcalUnit;
          let t = JSON.parse(d.timeStamp)
          let y = JSON.parse(d.monitorData);
          let n = d.monitorName.substring(1, d.monitorName.length - 1).split(',');
          if (t && y){
            if (t.length > 0 && y.length > 0){
              t.forEach(item => {
                xAxis.push(this.parseTime(item));
              })
              y.forEach(item => {
                let index = y.indexOf(item);
                yAxis.push({
                  name: n[index],
                  data: item,
                  type: 'line'
                });
                legend.push(n[index]);
              })
            }
          }
        }
      });
      let option = {
        title: {
          left: 'center',
          text: text,
          textStyle: {
            color: '#8492a6'
          },
        },
        tooltip: {
          trigger: 'axis'
        },
        legend: {},
        xAxis: {
          type: 'category',
          data: xAxis
        },
        yAxis: {
          type: 'value',
          name: yname,
        },
        series: yAxis
      };
      this.$set(option.legend, "data", legend);
      this.$set(option.legend, "bottom", "10px");
      this.$set(option.legend, "type", "scroll");
      return option;
    },
    //时间戳转为年月日时分秒格式
    parseTime(timestamp) {
      var date = new Date(timestamp*1000);//时间戳为10位需*1000，时间戳为13位的话不需乘1000
      var Y = date.getFullYear() + '-';
      var M = (date.getMonth() + 1 < 10 ? '0' + (date.getMonth() + 1) : date.getMonth() + 1) + '-';
      var D = (date.getDate() < 10 ? '0' + date.getDate() : date.getDate()) + ' ';
      var h = (date.getHours() < 10 ? '0' + date.getHours() : date.getHours()) + ':';
      var m = (date.getMinutes() < 10 ? '0' + date.getMinutes() : date.getMinutes()) + ':';
      var s = (date.getSeconds() < 10 ? '0' + date.getSeconds() : date.getSeconds());
      let strDate = Y + M + D + h + m + s;
      return strDate;
    }
  },
  watch: {
    report: {
      handler(val) {
        if (!val.status || !val.id) {
          return;
        }
        let status = val.status;
        this.id = val.id;
        if (status === "Running") {
          this.getResource();
        } else if (status === "Completed") {
          this.getMonitorData();
        } else {
          this.instances = [];
          this.data = [];
        }
      },
      deep: true
    }
  }
};
</script>

<style scoped>
.chart-config {
  width: 100%;
}
</style>
