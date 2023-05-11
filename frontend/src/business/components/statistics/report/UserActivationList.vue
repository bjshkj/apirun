<template>
  <el-card class="table-card" v-loading="result.loading">
    <template v-slot:header>
      <el-row>
        <el-col :span="12">
           <span class="title">
            用户活跃度排行榜
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
      <el-table-column prop="uname" :label="'用户名'"></el-table-column>
      <el-table-column prop="operCount" :label="'操作次数'"></el-table-column>
    </el-table>
  </el-card>
</template>

<script>
export default {
  name: "UserActivationList",
  props: {
    value2: {
      type: Array,
      required: true
    }
  },
  data(){
    return {
      result: {},
      options: [],
      value: '',
      tableData: [],
      workspaceId: '',
      beginTime: this.value2[0].toString(),
      endTime: this.value2[1].toString(),
    }
  },
  created() {
    this.getWorkspaceList();
  },
  mounted() {
    this.getInitUserActivity();
  },
  methods: {
    handleChange(val){
      this.workspaceId = val;
      this.getUserActivity();
    },
    getWorkspaceList(){
      this.$get("/workspace/list", response => {
        response.data.forEach(item => {
          this.workspaceDate = item;
          this.options.push({value: this.workspaceDate.id,label: this.workspaceDate.name})
        })
        this.value = response.data[0].id;
        this.workspaceId = response.data[0].id;
      })
    },
    getUserActivity(){
      this.result = this.$get(`/statistics/getWorkspaceUserActivity/${this.workspaceId}/${this.beginTime}/${this.endTime}`,response => {
        this.tableData = response.data;
      })
    },
    getInitUserActivity(){
      this.$get("/workspace/list", response => {
        this.workspaceId = response.data[0].id;
        this.result = this.$get(`/statistics/getWorkspaceUserActivity/${this.workspaceId}/${this.beginTime}/${this.endTime}`,response => {
          this.tableData = response.data;
        })
      })
    }
  }
}
</script>

<style scoped>
.select {
  float: right;
  width: 150px;
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
