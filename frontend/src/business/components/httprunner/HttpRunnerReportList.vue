<template>
  <ms-container>
    <ms-main-container>
      <el-card class="table-card" v-loading="result.loading">
        <template v-slot:header>
          <ms-table-header :condition="{}"
                           :title="$t('api_report.title')"
                           :show-create="false"
                           :have-search="false" />
        </template>
        <el-table
          border
          class="adjust-table table-content"
          :data="tableData"
          @sort-change="sort"
          @select-all="handleSelectAll"
          @select="handleSelect"
          :height="screenHeight"
          @filter-change="filter"
          @row-click="handleView">
          <el-table-column type="selection"/>
          <!-- <el-table-column width="40" :resizable="false" align="center">
            <template v-slot:default="scope">
              <show-more-btn :is-show="scope.row.showMore" :buttons="buttons" :size="selectRows.size"/>
            </template>
          </el-table-column> -->
          <el-table-column :label="$t('commons.name')" width="200" show-overflow-tooltip prop="taskName">
          </el-table-column>
          <el-table-column prop="createUser" :label="$t('api_test.creator')" show-overflow-tooltip/>
          <el-table-column prop="ctime" :label="$t('commons.create_time')">
            <template v-slot:default="scope">
              <span>{{ scope.row.ctime | timestampFormatDate }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="triggerModel" label="触发方式">
          </el-table-column>
          <el-table-column prop="taskStatus" :label="$t('commons.status')">
          </el-table-column>
          <el-table-column label="执行结果（总数/失败）">
            <template v-slot:default="scope">
              <span>{{ scope.row.tcasesTotal || '-' }} / {{scope.row.tcasesFail || '-' }}</span>
            </template>
          </el-table-column>
          <el-table-column :label="$t('commons.operating')">
            <template v-slot:default="scope">
              <ms-table-operator-button tip="console输出" icon="el-icon-tickets"
                                        @exec="openDialog(scope.row)" type="primary"/>
              <ms-table-operator-button :tip="$t('api_report.detail')" icon="el-icon-s-data"
                                        @exec="handleView(scope.row)" type="primary"/>
<!--              <ms-table-operator-button tip="报告下载" icon="el-icon-download"
                                        @exec="handleDownloadReport(scope.row)" type="primary"/>-->
              <ms-table-operator-button :tip="$t('api_report.delete')"
                                        icon="el-icon-delete" @exec="handleDelete(scope.row)" type="danger"/>
            </template>
          </el-table-column>
        </el-table>
        <ms-table-pagination :change="search" :current-page.sync="currentPage" :page-size.sync="pageSize"
                             :total="total"/>
      </el-card>
    </ms-main-container>

    <el-dialog
      title="日志"
      :visible.sync="showDialog"
      :destroy-on-close="true"
      :close-on-click-modal="false"
      @close="closeDialog">

      <ul v-infinite-scroll="loadLog" class="log-box" :disabled="disabledLoadLog">
        <li v-for="(item, index) in logContent" :key="index" class="log-item">
          {{ item }}
        </li>
      </ul>

        <el-empty
          v-if="!logContent.length"
          class="empty"
          :image-size="200"
          description="暂无日志"
        ></el-empty>

      <template v-slot:footer>
        <div class="dialog-footer">
          <el-button @click="downloadLog" @keydown.enter.native.prevent>下载日志</el-button>
          <el-button type="primary" @click="closeDialog">关闭</el-button>
        </div>
      </template>
    </el-dialog>
  </ms-container>
</template>

<script>
import MsTablePagination from "@common/pagination/TablePagination";
import MsTableHeader from "@common/components/MsTableHeader";
import MsContainer from "@common/components/MsContainer";
import MsMainContainer from "@common/components/MsMainContainer";
import MsTableOperatorButton from "@common/components/MsTableOperatorButton";
import ReportTriggerModeItem from "@common/tableItem/ReportTriggerModeItem";
import {REPORT_CONFIGS} from "@common/components/search/search-components";
import ShowMoreBtn from "@comp/track/case/components/ShowMoreBtn";
import {_filter, _sort} from "@/common/js/tableUtils";

export default {
  name: "HttpRunnerReportList",
  components: {
    ReportTriggerModeItem,
    MsTableOperatorButton,
    MsMainContainer, MsContainer, MsTableHeader, MsTablePagination, ShowMoreBtn
  },
  data() {
    return {
      result: {},
      condition: {
        components: REPORT_CONFIGS
      },
      tableData: [],
      multipleSelection: [],
      currentPage: 1,
      pageSize: 10,
      total: 0,
      loading: false,
      // buttons: [
      //   {
      //     name: this.$t('api_report.batch_delete'), handleClick: this.handleBatchDelete
      //   }
      // ],
      selectRows: new Set(),
      selectAll: false,
      unSelection: [],
      selectDataCounts: 0,
      screenHeight: 'calc(100vh - 295px)',
      // dialog
      curReport: null,
      showDialog: false,
      logPage: 1,
      logPageCount: 1,
      logContent: [],
      logLoading: false,
      timer: null
    }
  },
  computed: {
    projectId () {
      return this.$store.state.projectId;
    },
    disabledLoadLog () {
      return this.logLoading || this.logPage > this.logPageCount;
    }
  },
  watch: {
    '$route': 'init',
  },
  methods: {
    search() {
      this.result = this.$get(`/httpruner/list/${this.projectId}/${this.currentPage}/${this.pageSize}`, response => {
        let data = response.data;
        this.total = data.itemCount;
        this.tableData = data.listObject;
        this.selectRows.clear();

        this.pollingList()
      });
    },
    pollingList() {
      const isRightPage = this.$route.name === 'httpRunnerReportList'
      if (!isRightPage) return

      const needPolling = !!this.tableData.find(el => ["CREATED", "SENDED", "QUEUE", "EXECUTING"].includes(el.taskStatus));
      if (needPolling) {
        this.timer = setTimeout(() => {
          this.search()
        }, 5000);
      }
    },
    handleSelectionChange(val) {
      this.multipleSelection = val;
    },
    handleView(report) {
      if(report.taskStatus !=='DONE'){
        this.$warning("只有已完成的才能查看报告")
        return;
      }
      this.$router.push({ name: 'HttpRunnerReportDetail', params: { reportId: report.id }})
    },
    handleDownloadReport(report) {
      // this.$get(`/api/httprunner/result/log/download/${report.id}`,
      //   response => {
      //     console.log('downloadLog', response.data);
      //   });
    },
    handleDelete(report) {
      this.$alert(this.$t('api_report.delete_confirm') + report.taskName + "？", '', {
        confirmButtonText: this.$t('commons.confirm'),
        callback: (action) => {
          if (action === 'confirm') {
             this.result = this.$get(`/api/httprunner/result/summary/delete/${report.id}`, () => {
               this.$success(this.$t('commons.delete_success'));
               this.search();
             });
          }
        }
      });
    },
    init() {
      this.search();
    },
    sort(column) {
      _sort(column, this.condition);
      this.init();
    },
    filter(filters) {
      _filter(filters, this.condition);
      this.init();
    },
    handleSelect(selection, row) {
      if (this.selectRows.has(row)) {
        this.$set(row, "showMore", false);
        this.selectRows.delete(row);
      } else {
        this.$set(row, "showMore", true);
        this.selectRows.add(row);
      }
    },
    handleSelectAll(selection) {
      if (selection.length > 0) {
        this.tableData.forEach(item => {
          this.$set(item, "showMore", true);
          this.selectRows.add(item);
        });
      } else {
        this.selectRows.clear();
        this.tableData.forEach(row => {
          this.$set(row, "showMore", false);
        })
      }
    },
    // handleBatchDelete() {
    //   this.$alert(this.$t('api_report.delete_batch_confirm') + "？", '', {
    //     confirmButtonText: this.$t('commons.confirm'),
    //     callback: (action) => {
    //       if (action === 'confirm') {
    //         // let ids = Array.from(this.selectRows).map(row => row.id);
    //         // this.$post('/api/report/batch/delete', {ids: ids}, () => {
    //         //   this.selectRows.clear();
    //         //   this.$success(this.$t('commons.delete_success'));
    //         //   this.search();
    //         // });
    //       }
    //     }
    //   });
    // }

    openDialog(report) {
      this.showDialog = true
      this.logPage = 1
      this.logPageCount = 1
      this.logContent = []
      this.curReport = report
      this.loadLog()
    },
    closeDialog() {
      this.showDialog = false;
      this.curReport = null
    },
    loadLog() {
      if (this.logLoading || this.logPage > this.logPageCount) return;

      this.logLoading = true;
      this.$get(`/api/httprunner/result/log/${this.curReport.id}/${this.logPage}`, res => {
        res.data.listObject.forEach(el => {
          this.logContent = this.logContent.concat(el.content.split('\n'));
        })
        this.logPageCount = res.data.pageCount
        this.logPage++;
        this.logLoading = false;
      });
    },
    downloadLog() {
      let config = {
        url: `/api/httprunner/result/log/download/${this.curReport.id}`,
        method: 'get',
        responseType: 'blob'
      };
      this.$request(config).then(response => {
        const filename = `${this.curReport.taskName}.log`
        const blob = new Blob([response.data]);
        if ("download" in document.createElement("a")) {
          // 非IE下载
          //  chrome/firefox
          let aTag = document.createElement('a');
          aTag.download = filename;
          aTag.href = URL.createObjectURL(blob);
          aTag.click();
          URL.revokeObjectURL(aTag.href)
        } else {
          // IE10+下载
          navigator.msSaveBlob(blob, filename);
        }
      });
    },

  },

  created() {
    this.init();
  }
}
</script>

<style scoped>
.table-content {
  width: 100%;
}

.log-box {
  padding-right: 10px;
  max-height: 50vh;
  overflow:auto;
}

.log-item {
  line-height: 1.5em;
}

.empty {
  text-align: center;
}

.empty ::v-deep .el-empty__image {
  margin: auto;
}
</style>

