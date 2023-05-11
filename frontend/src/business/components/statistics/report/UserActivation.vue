<template>
  <el-card class="table-card" body-style="padding:10px;" v-loading="result.loading">
    <el-row>
      <el-col :span="12">
        <span class="title">
          用户活跃度
        </span>
      </el-col>
      <el-col :span="12">
        <el-select v-model="value" placeholder="请选择" class="ms-col-type select" @change="handleChange" style="width: 100px">
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
      <ms-chart ref="userChart" class="chart-config" :options="getChart()" :autoresize="true" style="width: 100%"/>
    </ms-container>
  </el-card>
</template>

<script>
import MsChart from "@common/chart/MsChart";
import MsContainer from "@common/components/MsContainer";

export default {
  name: "UserActivation",
  components: {MsContainer, MsChart},
  props: {
    value2: {
      type: Array,
      required: true
    }
  },
  data() {
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
      value: '按天',
      beginTime: this.value2[0].toString(),
      endTime: this.value2[1].toString(),
      dataSpan: '',
      xlist: [],
      activeList: [],
      newList: []
    }
  },
  created() {
    this.getDataSpan();
    this.getUserActivity();
  },
  methods: {
    handleChange(val){
      this.dataSpan = val;
      this.getUserActivity();
    },
    getDataSpan(){
      const dateBegin = new Date(this.beginTime).getTime();
      const dateEnd = new Date(this.endTime).getTime();
      const dateDiff = dateEnd - dateBegin;
      const dayDiff = Math.floor(dateDiff / (24 * 3600 * 1000))
      if (parseInt(dayDiff) >= 0 && parseInt(dayDiff) < 29){
        this.dataSpan = 'D';
        this.value = '按天';
      }else if (parseInt(dayDiff) >= 29 && parseInt(dayDiff) < 89){
        this.dataSpan = 'W';
        this.value = '按周';
      }else if (parseInt(dayDiff) >= 89){
        this.dataSpan = 'M';
        this.value = '按月';
      }
    },
    getUserActivity(){
      this.result = this.$get(`/statistics/getUsersNewAddWithActivity/${this.beginTime}/${this.endTime}/${this.dataSpan}`,response => {
        this.xlist = response.data.xlist;
        response.data.ylist.forEach(item => {
          if (item.name === '用户活跃情况'){
            this.activeList = item.data;
          }else if(item.name === '用户新增情况'){
            this.newList = item.data;
          }
        })
      })
    },
    getChart(){
      let option = {
        tooltip: {
          trigger: 'axis'
        },
        color:["#32cd32", "#6495ed"],
        legend: {
          data: ['用户活跃情况', '用户新增情况'],
          itemWidth: 30,
          itemHeight: 20,
          padding: 10,
          textStyle: {
            fontSize: 15
          }
        },
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
            name: '用户活跃情况',
            type: 'line',
            data: this.activeList
          },
          {
            name: '用户新增情况',
            type: 'line',
            data: this.newList
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
