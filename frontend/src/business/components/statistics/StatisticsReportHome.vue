<template>
  <ms-main-container>
    <el-header>
      <el-row>
        <el-col :span="12">
          <div style="padding-top: 15px" class="title">
<!--            <el-button type="primary" size="mini" @click="handleExport" :disabled="readOnly">{{ $t('report.export') }}<i class="el-icon-download el-icon&#45;&#45;right"></i></el-button>-->
            <span>管理面板</span>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="block" style="float: right;padding-top: 10px;">
            <el-date-picker
              v-model="value2"
              type="daterange"
              align="right"
              unlink-panels
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              :picker-options="pickerOptions"
              @change="dataSearch">
            </el-date-picker>
          </div>
        </el-col>
      </el-row>
    </el-header>
    <el-main style="padding-top: 5px">
      <el-row :gutter="40">
        <el-col :span="12">
          <cases-total-card v-if="isReload" v-bind:value2 = "value2"/>
        </el-col>
        <el-col :span="12">
          <project-type-card v-if="isReload" v-bind:value2 = "value2"/>
        </el-col>
      </el-row>
      <el-row :gutter="40" style="margin-top:40px">
        <el-col :span="12">
          <cases-card v-if="isReload" v-bind:value2 = "value2"/>
        </el-col>
        <el-col :span="12">
          <user-activation v-if="isReload" v-bind:value2 = "value2"/>
        </el-col>
      </el-row>
      <el-row :gutter="40" style="margin-top:40px">
        <el-col :span="12">
          <user-activation-list v-if="isReload" v-bind:value2 = "value2"/>
        </el-col>
        <el-col :span="12">
          <project-activation-list v-if="isReload" v-bind:value2 = "value2"/>
        </el-col>
      </el-row>
    </el-main>
  </ms-main-container>

</template>

<script>

import MsContainer from "@common/components/MsContainer";
import MsMainContainer from "@common/components/MsMainContainer";
import CasesTotalCard from "@comp/statistics/report/CasesTotalCard";
import {formatTime} from "@/common/js/format-utils"
import ProjectTypeCard from "@comp/statistics/report/ProjectTypeCard";
import CasesCard from "@comp/statistics/report/CasesCard";
import UserActivationList from "@comp/statistics/report/UserActivationList";
import ProjectActivationList from "@comp/statistics/report/ProjectActivationList";
import UserActivation from "@comp/statistics/report/UserActivation";
require('echarts/lib/component/legend');
import html2canvas from 'html2canvas';
import {exportPdf,hasPermission} from "@/common/js/utils";

export default {
  name: "StatisticsReportHome",
  components: {
    UserActivation,
    ProjectActivationList,
    UserActivationList, CasesCard, ProjectTypeCard, CasesTotalCard, MsMainContainer, MsContainer},
  props:{

  },
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
      value2: [],
      isReload: true
    }
  },
  created() {
    this.getTime();
  },
  computed: {
    readOnly() {
      return !hasPermission('PROJECT_REPORT_ANALYSIS:READ+EXPORT');
    }
  },
  methods: {
    reload (){
      this.isReload = false
      this.$nextTick(() => (this.isReload = true))
    },
    getTime(){
      const end = new Date();
      const start = new Date();
      this.value2.push(formatTime(start.setTime(start.getTime() - 3600 * 1000 * 24 * 30),"{y}-{m}-{d}"));
      this.value2.push(formatTime(end.setTime(end.getTime()),"{y}-{m}-{d}"));
    },
    dataSearch(val){
      const date = val;
      this.value2 = [];
      if (date !== null){
        date.forEach(item => {
          this.value2.push(formatTime(item,"{y}-{m}-{d}"))
          this.reload()
        })
      }
    },
    handleExport() {
      let name = this.title;
      this.$nextTick(function () {
        setTimeout(() => {
          html2canvas(document.getElementById('statisticsReportHome'), {
            scale: 2
          }).then(function (canvas) {
            exportPdf(name, [canvas]);
          });
        }, 1000);
      });
    },
  }
}
</script>

<style scoped>
#menu-bar {
  border-bottom: 1px solid #E6E6E6;
  background-color: #FFF;
}

.deactivation >>> .el-submenu__title {
  border-bottom: white !important;
}
.title {
  font-size: 22px;
  font-weight: bold;
}
</style>
