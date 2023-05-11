<template>
  <el-card class="table-card" v-loading="result.loading">
    <template v-slot:header>
      <el-row>
        <el-col :span="12">
           <span class="title">
            项目活跃度排行榜
          </span>
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
    </template>
    <el-table border :data="tableData" class="adjust-table table-content" height="300px">
      <el-table-column prop="sortIndex"  :label="$t('api_test.home_page.failed_case_list.table_coloum.index')" type="index" width="100" show-overflow-tooltip/>
      <el-table-column prop="pname" :label="'项目名'"></el-table-column>
      <el-table-column prop="operCount" :label="'操作次数'"></el-table-column>
    </el-table>
  </el-card>
</template>

<script>
export default {
  name: "ProjectActivationList",
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
          value: 'api',
          label: '接口数'
        },
        {
          value: 'case',
          label: '接口用例'
        },
        {
          value: 'automation',
          label: '场景用例'
        },
        {
          value: 'performance',
          label: '性能用例'
        }
      ],
      value: '接口数',
      tableData: [],
      beginTime: this.value2[0].toString(),
      endTime: this.value2[1].toString(),
      queryType: 'api'
    }
  },
  created() {
    this.getProjectsActivity();
  },
  methods: {
    handleChange(val){
      this.queryType = val;
      this.getProjectsActivity();
    },
    getProjectsActivity(){
      this.result = this.$get(`/statistics/getProjectsActivityRanking/${this.beginTime}/${this.endTime}/${this.queryType}`,response => {
        this.tableData = response.data;
      })
    }
  }
}
</script>

<style scoped>
.select {
  float: right;
  width: 120px;
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
