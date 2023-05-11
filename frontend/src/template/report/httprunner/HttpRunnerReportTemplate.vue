<template>
  <ms-container>
    <ms-main-container>
      <div class="title-content">
        <img class="login-logo" :src="'/display/file/loginLogo'" alt="">
        <span>接口测试平台 | APITesting测试报告</span>
      </div>
      <el-card v-if="report.taskStatus === 'DONE'">
        <div class="title">报告：{{ report.taskName }}</div>

        <el-descriptions border :column="4" v-loading="isSummaryLoading">
          <el-descriptions-item label="时间" span="2">{{ report.stime | timestampFormatDate }} ~
            {{ report.ftime | timestampFormatDate }}
          </el-descriptions-item>
          <el-descriptions-item label="耗时">{{ report.duration }}s</el-descriptions-item>
          <el-descriptions-item label="创建人">{{ report.createUser }}</el-descriptions-item>
          <el-descriptions-item label="平台" span="4">
            {{ report.platform }}
            <el-divider direction="vertical"></el-divider>
            httprunner {{ report.httprunnerVersion }}
            <el-divider direction="vertical"></el-divider>
            {{ report.pythonVersion }}
          </el-descriptions-item>
          <el-descriptions-item label="测试用例（成功/失败）" span="4">
            <span class="color-primary">{{ report.tcasesTotal }}</span>
            (
            <span class="color-green">{{ report.tcasesSuccess }}</span>
            /
            <span class="color-red">{{ report.tcasesFail }}</span>
            )
          </el-descriptions-item>
          <!-- <el-descriptions-item label="测试步骤（成功/失败/错误/跳过/期待的失败/不期待的成功）" span="4"> -->
          <el-descriptions-item label="测试步骤（成功/失败/错误/跳过）" span="4">
            <span class="color-primary">{{ report.tstepsTotal }}</span>
            (
            <span class="color-green">{{ report.tstepsSuccesses }}</span>
            /
            <span class="color-red">{{ report.tstepsFailures }}</span>
            /
            <span class="color-orange">{{ report.tstepsErrors }}</span>
            /
            <span class="color-gray">{{ report.tstepsSkipped }}</span>
            <!-- /
            <span class="color-pink">{{report.tstepsExpectedFailures}}</span>
            /
            <span class="color-blue">{{report.tstepsUnexpectedSuccesses}}</span> -->
            )
          </el-descriptions-item>
        </el-descriptions>

        <div v-loading="isResultLoading" style="margin-top: 40px">
          <h4>详细内容</h4>
          <el-table
            :data="result">
            <el-table-column type="expand">
              <template slot-scope="{ row }">
                <div class="inner-table">
                  <el-table :data="row.testStepResults">
                    <el-table-column
                      label="名称"
                      prop="name">
                    </el-table-column>
                    <el-table-column
                      label="请求类型"
                      prop="method">
                      <template slot-scope="{row}">
                        <el-tag size="mini"
                                :style="{'background-color': getColor(true, row.method), border: getColor(true, row.method)}"
                                class="api-el-tag">
                          {{ row.method }}
                        </el-tag>
                      </template>
                    </el-table-column>
                    <el-table-column
                      label="URL"
                      prop="path">
                    </el-table-column>
                    <el-table-column
                      label="结果"
                      prop="status">
                      <template slot-scope="{row}">
                        <span class="color-green" v-if="row.status==='success'">{{ row.status }}</span>
                        <span class="color-red" v-else-if="row.status==='failure'">{{ row.status }}</span>
                        <span v-else>{{ row.status }}</span>
                      </template>
                    </el-table-column>
                    <el-table-column
                      label="操作">
                      <template slot-scope="childScope">
                        <el-button type="text" size="small" @click="openDialog(childScope.row)">详情</el-button>
                        <el-button type="text" size="small" @click="openErrorDialog(childScope.row)"
                                   v-if="childScope.row.attachment">报错信息
                        </el-button>
                      </template>
                    </el-table-column>
                  </el-table>
                </div>
              </template>
            </el-table-column>
            <el-table-column
              label="名称"
              prop="name">
            </el-table-column>
            <el-table-column
              label="耗时"
              prop="duration"
              :formatter="(row) => row.duration.toFixed(2) + 's'">
            </el-table-column>
            <el-table-column label="状态" width="400">
              <template slot="header">
                状态
                <!-- <el-tooltip effect="dark" content="成功/失败/错误/跳过/期待的失败/不期待的成功" placement="top"> -->
                <el-tooltip effect="dark" content="成功/失败/错误/跳过" placement="top">
                  <i class="el-icon-warning" style="cursor: pointer"></i>
                </el-tooltip>
              </template>
              <template slot-scope="{ row }">
                <span class="color-primary">{{ row.stepStatInfo.total }}</span>
                (
                <span class="color-green">{{ row.stepStatInfo.successes }}</span>
                /
                <span class="color-red">{{ row.stepStatInfo.failures }}</span>
                /
                <span class="color-orange">{{ row.stepStatInfo.errors }}</span>
                /
                <span class="color-gray">{{ row.stepStatInfo.skipped }}</span>
                <!-- /
                <span class="color-pink">{{row.stepStatInfo.expectedFailures}}</span>
                /
                <span class="color-blue">{{row.stepStatInfo.unexpectedSuccesses}}</span> -->
                )
              </template>
            </el-table-column>
            <el-table-column
              label="结果"
              prop="success">
              <template slot-scope="{ row }">
                <span class="color-green" v-if="row.success">成功</span>
                <span class="color-red" v-else>失败</span>
              </template>
            </el-table-column>
          </el-table>
        </div>

      </el-card>

      <el-card v-if="report.taskStatus === 'ERROR'">
        <div class="title">报告：{{ report.taskName }}</div>
        <div><h3>执行状态：{{ report.taskStatus }}</h3></div>
        <div>
          <h3>运行日志</h3>
          <ul v-infinite-scroll="loadLog">
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
        </div>
      </el-card>

      <el-dialog
        title="Request and Response data"
        :visible.sync="showDialog"
        :destroy-on-close="true"
        :close-on-click-modal="false"
        @close="closeDialog">

        <div class="dialog-content">
          <div class="title">Name: {{ curItem.name }}</div>
          <div class="title">Request:</div>
          <table v-if="curItem.requestInfo">
            <tr>
              <th>url</th>
              <td>{{ curItem.requestInfo.url }}</td>
            </tr>
            <tr>
              <th>method</th>
              <td>
                <el-tag
                  size="mini"
                  :style="{'background-color': getColor(true, curItem.requestInfo.method), border: getColor(true, curItem.requestInfo.method)}"
                  class="api-el-tag">
                  {{ curItem.requestInfo.method }}
                </el-tag>
              </td>
            </tr>
            <tr>
              <th>headers</th>
              <td>
                <pre v-html="curItem.requestInfo.headers"></pre>
              </td>
            </tr>
            <tr>
              <th>body</th>
              <td>
                <pre v-html="curItem.requestInfo.body"></pre>
              </td>
            </tr>
          </table>
          <div class="title">Response:</div>
          <table v-if="curItem.responseInfo">
            <tr>
              <th>ok</th>
              <td>{{ curItem.responseInfo.responseOk }}</td>
            </tr>
            <tr>
              <th>url</th>
              <td>{{ curItem.responseInfo.url }}</td>
            </tr>
            <tr>
              <th>status code</th>
              <td>{{ curItem.responseInfo.statusCode }}</td>
            </tr>
            <tr>
              <th>status msg</th>
              <td>{{ curItem.responseInfo.statusMsg }}</td>
            </tr>
            <tr>
              <th>headers</th>
              <td>
                <pre v-html="curItem.responseInfo.headers"></pre>
              </td>
            </tr>
            <tr>
              <th>cookies</th>
              <td>
                <pre v-html="curItem.responseInfo.cookies"></pre>
              </td>
            </tr>
            <tr>
              <th>encoding</th>
              <td>{{ curItem.responseInfo.encoding }}</td>
            </tr>
            <tr>
              <th>content type</th>
              <td>{{ curItem.responseInfo.contentType }}</td>
            </tr>
            <tr>
              <th>body</th>
              <td>
                <pre v-html="curItem.responseInfo.body"></pre>
              </td>
            </tr>
          </table>
          <div class="title">Validators:</div>
          <table v-if="curItem.validateExtractor">
            <tr>
              <th>check</th>
              <th>comparator</th>
              <th>expect value</th>
              <th>actual value</th>
            </tr>
            <tr v-for="(item, index) in curItem.validateExtractor" :key="index">
              <td class="bg-green">{{ item.check }}</td>
              <td>{{ item.comparator }}</td>
              <td>{{ item.expect_value }}</td>
              <td>{{ item.check_value }}</td>
            </tr>
          </table>
          <div class="title">Statistics:</div>
          <table v-if="curItem">
            <tr>
              <th>content size</th>
              <td>{{ curItem.contentSize }} bytes</td>
            </tr>
            <tr>
              <th>response time</th>
              <td>{{ curItem.responseTimeMs }} ms</td>
            </tr>
            <tr>
              <th>elapsed</th>
              <td>{{ curItem.elapsedMs }} ms</td>
            </tr>
          </table>
        </div>

        <template v-slot:footer>
          <div class="dialog-footer">
            <el-button type="primary" @click="closeDialog">关闭</el-button>
          </div>
        </template>
      </el-dialog>


      <el-dialog
        title="报错信息"
        :visible.sync="showErrorDialog"
        :destroy-on-close="true"
        :close-on-click-modal="false"
        @close="closeErrorDialog">

        <div class="dialog-content">
          <div class="title">{{ curItem.name }}</div>
          <div>
            <pre v-html="curItem.attachment"></pre>
          </div>
        </div>


        <template v-slot:footer>
          <div class="dialog-footer">
            <el-button type="primary" @click="closeErrorDialog">关闭</el-button>
          </div>
        </template>
      </el-dialog>
    </ms-main-container>
  </ms-container>
</template>

<script>
import MsMainContainer from "@common/components/MsMainContainer";
import MsContainer from "@common/components/MsContainer";
import {API_METHOD_COLOUR} from "@comp/api/definition/model/JsonData";
import {getQueryVariable} from "@/common/js/utils";

export default {
  name: "HttpRunnerReportTemplate",
  components: {
    MsMainContainer,
    MsContainer,
  },
  data() {
    return {
      isSummaryLoading: false,
      isResultLoading: false,
      reportId: "",
      report: {},
      result: [],
      activeCase: "",
      showDialog: false,
      curItem: {},
      methodColorMap: new Map(API_METHOD_COLOUR),
      showErrorDialog: false,
      logPage: 1,
      logPageCount: 1,
      logContent: [],
      logLoading: false,
    };
  },
  created() {
    let reportId = getQueryVariable("reportId");
    this.reportId = reportId;
    this.getReportSummary();
    this.getReportResult();
    this.loadLog();
  },
  methods: {
    getReportSummary() {
      if (this.reportId) {
        this.isSummaryLoading = true;
        this.$get(`/api/httprunner/result/summary/info/${this.reportId}`, response => {
          if (response.data) {
            this.report = response.data || {};
            this.isSummaryLoading = false;
          } else {
            this.isSummaryLoading = false;
            this.$error(this.$t('api_report.not_exist'));
          }
        });
      }
    },
    getReportResult() {
      if (this.reportId) {
        this.isResultLoading = true;
        this.$get(`/api/httprunner/result/testcases/${this.reportId}`, response => {
          if (response.data) {
            this.result = response.data || [];
            this.isResultLoading = false;
          } else {
            this.isResultLoading = false;
            this.$error(this.$t('api_report.not_exist'));
          }
        });
      }
    },
    loadLog() {
      if (this.logLoading || this.logPage > this.logPageCount)
        return;

      this.logLoading = true;
      this.$get(`/api/httprunner/result/log/${this.reportId}/${this.logPage}`, res => {
        res.data.listObject.forEach(el => {
          this.logContent = this.logContent.concat(el.content.split('\n'));
        })
        this.logPageCount = res.data.pageCount
        this.logPage++;
        this.logLoading = false;
      });
    },
    openDialog(item) {
      this.showDialog = true;
      this.curItem = item;
    },
    closeDialog() {
      this.showDialog = false;
      this.curItem = {};
    },
    openErrorDialog(item) {
      this.showErrorDialog = true;
      this.curItem = item;
    },
    closeErrorDialog() {
      this.showErrorDialog = false;
      this.curItem = {};
    },
    getColor(enable, method) {
      return this.methodColorMap.get(method);
    }
  }
}
</script>

<style scoped>
.title {
  height: 40px;
  font-weight: bold;
  font-size: 18px;
}

.api-el-tag {
  color: white;
}

.inner-table ::v-deep tr,
.inner-table ::v-deep th {
  background-color: #f3f3f3;
}

.color-primary {
  color: #783887;
}

.color-green {
  color: #67C23A;
}

.color-red {
  color: #F56C6C;
}

.color-orange {
  color: #E6A23C;
}

.color-gray {
  color: #909399;
}

.color-blue {
  color: #1989fa;
}

.color-pink {
  color: #fa19dc;
}

.dialog-content {
  padding-right: 10px;
  max-height: 50vh;
  overflow: auto;
}

.dialog-content table {
  overflow: auto;
  width: 100%;
  margin-bottom: 20px;
}

.dialog-content th {
  width: 100px;
  background-color: skyblue;
  padding: 5px 12px;
}

.dialog-content td {
  background-color: lightblue;
  padding: 5px 12px;
}

.dialog-content pre {
  white-space: pre-wrap;
}

.dialog-content td.bg-green {
  background-color: lightgreen;
}

.empty ::v-deep .el-empty__image {
  margin: auto;
}

.empty {
  text-align: center;
}

.title-content {
  width: 1200px;
  display: flex;
  align-items: center;
  font-size: 20px;
  color: #333333;
}

.login-logo {
  width: 200px;
  height: 100px;
  margin-right: 10px;
}
</style>
