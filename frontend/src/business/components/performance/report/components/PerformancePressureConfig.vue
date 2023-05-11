<template>
  <div v-loading="result.loading" class="pressure-config-container">
    <el-row>
      <el-col :span="12">

        <el-collapse v-model="activeNames" accordion>
          <el-collapse-item :name="index"
                            v-for="(threadGroup, index) in threadGroups.filter(th=>th.enabled === 'true' && th.deleted=='false')"
                            :key="index">
            <template slot="title">
              <el-row>
                <el-col :span="14">
                  <el-tooltip :content="threadGroup.attributes.testname" placement="top">
                    <div style="padding-right:20px; font-size: 16px;" class="wordwrap">
                      {{ threadGroup.attributes.testname }}
                    </div>
                  </el-tooltip>
                </el-col>
                <el-col :span="10">
                  <el-tag type="primary" size="mini" v-if="threadGroup.threadType === 'DURATION'">
                    {{ $t('load_test.thread_num') }}{{ threadGroup.threadNumber }},
                    {{ $t('load_test.duration') }}:
                    <span v-if="threadGroup.durationHours">
                      {{ threadGroup.durationHours }} {{ '小时' }}
                    </span>
                    <span v-if="threadGroup.durationMinutes">
                      {{ threadGroup.durationMinutes }} {{ '分钟' }}
                    </span>
                    <span v-if="threadGroup.durationSeconds">
                      {{ threadGroup.durationSeconds }} {{ '秒' }}
                    </span>
                  </el-tag>
                  <el-tag type="primary" size="mini" v-if="threadGroup.threadType === 'ITERATION'">
                    {{ $t('load_test.thread_num') }} {{ threadGroup.threadNumber }},
                    {{ $t('load_test.iterate_num') }} {{ threadGroup.iterateNum }}
                  </el-tag>
                </el-col>
              </el-row>
            </template>
            <div v-if="threadGroup.tgType === 'kg.apc.jmeter.threads.UltimateThreadGroup'">
              <el-form-item label="压力模型">
                <el-select v-model="threadGroup.model" placeholder="请选择" size="mini" @change="calculateTotalChart()">
                  <el-option
                    v-for="item in models"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value">
                  </el-option>
                </el-select>
              </el-form-item>
              <br>
              <el-form-item :label="$t('load_test.thread_num')">
                <el-input-number
                  controls-position="right"
                  v-model="threadGroup.threadNumber"
                  @change="calculateTotalChart()"
                  :min="1"
                  size="mini"/>
              </el-form-item>
              <el-form-item label="最小并发数" v-if="threadGroup.model === 'waveSurge'">
                <el-input-number
                  controls-position="right"
                  v-model="threadGroup.minThreadNumber"
                  @change="calculateTotalChart()"
                  :min="0"
                  size="mini">
                </el-input-number>
              </el-form-item>
              <br>
              <el-form-item :label="$t('load_test.duration')">
                <el-input-number controls-position="right"
                                 v-model="threadGroup.durationHours"
                                 :min="0"
                                 :max="9999"
                                 @change="calculateTotalChart()"
                                 size="mini"/>
              </el-form-item>
              <el-form-item label="时" label-width="20px"/>
              <el-form-item>
                <el-input-number controls-position="right"
                                 v-model="threadGroup.durationMinutes"
                                 :min="0"
                                 :max="59"
                                 @change="calculateTotalChart()"
                                 size="mini"/>
              </el-form-item>
              <el-form-item label="分" label-width="20px"/>
              <el-form-item>
                <el-input-number controls-position="right"
                                 v-model="threadGroup.durationSeconds"
                                 :min="0"
                                 :max="59"
                                 @change="calculateTotalChart()"
                                 size="mini"/>
              </el-form-item>
              <el-form-item label="秒" label-width="20px"/>
              <div v-if="threadGroup.model === 'shock'">
                <el-form-item label="震荡次数">
                  <el-input-number controls-position="right"
                                   v-model="threadGroup.shockCount"
                                   :min="0"
                                   @change="calculateTotalChart()"
                                   size="mini"/>
                </el-form-item>
              </div>
              <div v-if="threadGroup.model === 'waveSurge'">
                <el-form-item label="波涌次数">
                  <el-input-number controls-position="right"
                                   v-model="threadGroup.waveCount"
                                   :min="0"
                                   @change="calculateTotalChart()"
                                   size="mini"/>
                </el-form-item>
                <br>
                <el-form-item label="峰值持续时间">
                  <el-input-number controls-position="right"
                                   v-model="threadGroup.peakDuration"
                                   :min="0"
                                   :max="59"
                                   @change="calculateTotalChart()"
                                   size="mini"/>
                </el-form-item>
              </div>
              <div v-if="threadGroup.model === 'dip'">
                <el-form-item label="爬坡时间">
                  <el-input-number
                    controls-position="right"
                    :min="1"
                    v-model="threadGroup.rampUpTime"
                    @change="calculateTotalChart()"
                    size="mini"/>
                </el-form-item>
                <br>
                <el-form-item label="摸高期望指标">
                  <el-form-item label="响应时间 <= ">
                    <el-input-number
                      controls-position="right"
                      :min="100"
                      v-model="threadGroup.responseTime"
                      @change="calculateTotalChart()"
                      size="mini"/>
                    <span> ms</span>
                  </el-form-item>
                  <br>
                  <el-form-item label="成功率 >= ">
                    <el-input-number
                      controls-position="right"
                      :min="1"
                      :max="100"
                      v-model="threadGroup.errorRate"
                      @change="calculateTotalChart()"
                      size="mini"/>
                    <span> %</span>
                  </el-form-item>
                </el-form-item>
              </div>
            </div>
            <div v-if="threadGroup.tgType !== 'kg.apc.jmeter.threads.UltimateThreadGroup'">
              <el-form :inline="true" label-width="100px" :disabled="isReadOnly">
                <el-form-item :label="$t('load_test.thread_num')">
                  <el-input-number
                    controls-position="right"
                    :disabled="true"
                    :placeholder="$t('load_test.input_thread_num')"
                    v-model="threadGroup.threadNumber"
                    :min="1"
                    size="mini"/>
                </el-form-item>
                <br>
                <el-form-item :label="$t('执行方式')">
                  <el-radio-group v-model="threadGroup.threadType" :disabled="true">
                    <el-radio label="DURATION">{{ $t('load_test.by_duration') }}</el-radio>
                    <el-radio label="ITERATION">{{ $t('load_test.by_iteration') }}</el-radio>
                  </el-radio-group>
                </el-form-item>
                <br>
                <div v-if="threadGroup.threadType === 'DURATION'">
                  <el-form-item :label="$t('load_test.duration')">
                    <el-input-number controls-position="right"
                                     :disabled="true"
                                     v-model="threadGroup.durationHours"
                                     :min="0"
                                     :max="9999"
                                     @change="calculateTotalChart()"
                                     size="mini"/>
                  </el-form-item>
                  <el-form-item label="时" label-width="20px"/>
                  <el-form-item>
                    <el-input-number controls-position="right"
                                     v-model="threadGroup.durationMinutes"
                                     :min="0"
                                     :max="59"
                                     @change="calculateTotalChart()"
                                     size="mini"/>
                  </el-form-item>
                  <el-form-item label="分" label-width="20px"/>
                  <el-form-item>
                    <el-input-number controls-position="right"
                                     v-model="threadGroup.durationSeconds"
                                     :min="0"
                                     :max="59"
                                     @change="calculateTotalChart()"
                                     size="mini"/>
                  </el-form-item>
                  <el-form-item label="秒" label-width="20px"/>
                  <br>
                  <el-form-item :label="$t('load_test.rps_limit')">
                    <el-switch v-model="threadGroup.rpsLimitEnable" :disabled="true" @change="calculateTotalChart()"/>
                    &nbsp;
                    <el-input-number controls-position="right"
                                     :disabled="true"
                                     v-model="threadGroup.rpsLimit"
                                     @change="calculateTotalChart()"
                                     :min="1"
                                     size="mini"/>
                  </el-form-item>
                  <br>
                  <div v-if="threadGroup.tgType === 'com.blazemeter.jmeter.threads.concurrency.ConcurrencyThreadGroup'">
                    <el-form-item :label="$t('load_test.ramp_up_time_within')">
                      <el-input-number
                        :disabled="true"
                        :min="1"
                        :max="threadGroup.duration"
                        v-model="threadGroup.rampUpTime"
                        @change="calculateTotalChart()"
                        size="mini"/>
                    </el-form-item>
                    <el-form-item :label="$t('load_test.ramp_up_time_minutes', [getUnitLabel(threadGroup)])">
                      <el-input-number
                        :disabled="true"
                        :min="1"
                        :max="Math.min(threadGroup.threadNumber, threadGroup.rampUpTime)"
                        v-model="threadGroup.step"
                        @change="calculateTotalChart()"
                        size="mini"/>
                    </el-form-item>
                    <el-form-item :label="$t('load_test.ramp_up_time_times')"/>
                  </div>

                  <div v-if="threadGroup.tgType === 'ThreadGroup'">
                    <el-form-item :label="$t('load_test.ramp_up_time_within')">
                      <el-input-number
                        :disabled="true"
                        :min="1"
                        v-model="threadGroup.rampUpTime"
                        size="mini"/>
                    </el-form-item>
                    <el-form-item :label="$t('load_test.ramp_up_time_seconds', [getUnitLabel(threadGroup)])"/>
                  </div>

                </div>
                <div v-if="threadGroup.threadType === 'ITERATION'">
                  <el-form-item :label="$t('load_test.iterate_num')">
                    <el-input-number
                      :disabled="true"
                      v-model="threadGroup.iterateNum"
                      :min="1"
                      @change="calculateTotalChart()"
                      size="mini"/>
                  </el-form-item>
                  <br>
                  <el-form-item :label="$t('load_test.rps_limit')">
                    <el-switch v-model="threadGroup.rpsLimitEnable" :disabled="true" @change="calculateTotalChart()"/>
                    &nbsp;
                    <el-input-number
                      :disabled="true || !threadGroup.rpsLimitEnable"
                      v-model="threadGroup.rpsLimit"
                      @change="calculateTotalChart()"
                      :min="1"
                      size="mini"/>
                  </el-form-item>
                  <br>
                  <el-form-item :label="$t('load_test.ramp_up_time_within')">
                    <el-input-number
                      :disabled="true"
                      :min="1"
                      v-model="threadGroup.iterateRampUp"
                      @change="calculateTotalChart()"
                      size="mini"/>
                  </el-form-item>
                  <el-form-item :label="$t('load_test.ramp_up_time_seconds', [getUnitLabel(threadGroup)])"/>
                </div>
              </el-form>
            </div>
          </el-collapse-item>
        </el-collapse>
      </el-col>
      <el-col :span="12">
        <ms-chart class="chart-container" ref="chart1" :options="options" :autoresize="true"></ms-chart>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import echarts from "echarts";
import MsChart from "@/business/components/common/chart/MsChart";
import {findThreadGroup} from "@/business/components/performance/test/model/ThreadGroup";

const HANDLER = "handler";
const THREAD_GROUP_TYPE = "tgType";
const TARGET_LEVEL = "TargetLevel";
const RAMP_UP = "RampUp";
const STEPS = "Steps";
const DURATION = "duration";
const UNIT = "unit";
const RPS_LIMIT = "rpsLimit";
const RPS_LIMIT_ENABLE = "rpsLimitEnable";
const THREAD_TYPE = "threadType";
const ITERATE_NUM = "iterateNum";
const ITERATE_RAMP_UP = "iterateRampUpTime";
const ENABLED = "enabled";
const DELETED = "deleted";
const MODEL = "model";
const SHOCK_COUNT = "shockCount";
const WAVE_COUNT = "waveCount";
const MIN_THREAD_NUMBER = "minThreadNumber";
const PEAK_DURATION = "peakDuration";
const RESPONSE_TIME = "responseTime";
const ERROR_RATE = "errorRate";

const hexToRgba = function (hex, opacity) {
  return 'rgba(' + parseInt('0x' + hex.slice(1, 3)) + ',' + parseInt('0x' + hex.slice(3, 5)) + ','
    + parseInt('0x' + hex.slice(5, 7)) + ',' + opacity + ')';
};
const hexToRgb = function (hex) {
  return 'rgb(' + parseInt('0x' + hex.slice(1, 3)) + ',' + parseInt('0x' + hex.slice(3, 5))
    + ',' + parseInt('0x' + hex.slice(5, 7)) + ')';
};

export default {
  name: "MsPerformancePressureConfig",
  components: {MsChart},
  props: ['report'],
  data() {
    return {
      result: {},
      threadNumber: 0,
      duration: 0,
      rampUpTime: 0,
      step: 0,
      rpsLimit: 0,
      rpsLimitEnable: false,
      options: {},
      resourcePool: null,
      resourcePools: [],
      activeNames: ["0"],
      threadGroups: [],
      models: [{
        value: 'shock',
        label: '震荡模式'
      }, {
        value: 'waveSurge',
        label: '浪涌模式'
      }, {
        value: 'dip',
        label: '探底模式'
      }],
      model: 'shock',
      shockCount: 2
    };
  },
  activated() {
    this.getJmxContent();
  },
  methods: {
    calculateLoadConfiguration: function (data) {
      for (let i = 0; i < this.threadGroups.length; i++) {
        let d = data[i];
        if (!d) {
          return;
        }
        d.forEach(item => {
          switch (item.key) {
            case TARGET_LEVEL:
              this.threadGroups[i].threadNumber = item.value;
              break;
            case RAMP_UP:
              this.threadGroups[i].rampUpTime = item.value;
              break;
            case ITERATE_RAMP_UP:
              this.threadGroups[i].iterateRampUp = item.value;
              break;
            case DURATION:
              this.threadGroups[i].duration = item.value;
              break;
            case UNIT:
              this.threadGroups[i].unit = item.value;
              break;
            case STEPS:
              this.threadGroups[i].step = item.value;
              break;
            case RPS_LIMIT:
              this.threadGroups[i].rpsLimit = item.value;
              break;
            case RPS_LIMIT_ENABLE:
              this.threadGroups[i].rpsLimitEnable = item.value;
              break;
            case THREAD_TYPE:
              this.threadGroups[i].threadType = item.value;
              break;
            case ITERATE_NUM:
              this.threadGroups[i].iterateNum = item.value;
              break;
            case ENABLED:
              this.threadGroups[i].enabled = item.value;
              break;
            case DELETED:
              this.threadGroups[i].deleted = item.value;
              break;
            case HANDLER:
              this.threadGroups[i].handler = item.value;
              break;
            case THREAD_GROUP_TYPE:
              this.threadGroups[i].tgType = item.value;
              break;
            case MODEL:
              this.threadGroups[i].model = item.value;
              break;
            case SHOCK_COUNT:
              this.threadGroups[i].shockCount = item.value;
              break;
            case WAVE_COUNT:
              this.threadGroups[i].waveCount = item.value;
              break;
            case MIN_THREAD_NUMBER:
              this.threadGroups[i].minThreadNumber = item.value;
              break;
            case PEAK_DURATION:
              this.threadGroups[i].peakDuration = item.value;
              break;
            case RESPONSE_TIME:
              this.threadGroups[i].responseTime = item.value;
              break;
            case ERROR_RATE:
              this.threadGroups[i].errorRate = 100 - item.value;
              break;
            default:
              break;
          }
        });
        for (let i = 0; i < this.threadGroups.length; i++) {
          // 恢复成单位需要的值
          switch (this.threadGroups[i].unit) {
            case 'M':
              this.threadGroups[i].duration = this.threadGroups[i].duration / 60;
              break;
            case 'H':
              this.threadGroups[i].duration = this.threadGroups[i].duration / 60 / 60;
              break;
            default:
              break;
          }
        }
        this.calculateTotalChart();
      }
    },
    getLoadConfig() {
      if (!this.report.id) {
        return;
      }
      this.result = this.$get("/performance/report/" + this.report.id, res => {
        let data = res.data;
        if (data) {
          if (data.loadConfiguration) {
            let d = JSON.parse(data.loadConfiguration);
            this.calculateLoadConfiguration(d);
          } else {
            this.$get('/performance/get-load-config/' + this.report.id, (response) => {
              if (response.data) {
                let data = JSON.parse(response.data);
                this.calculateLoadConfiguration(data);
              }
            });
          }
        } else {
          this.$error(this.$t('report.not_exist'));
        }
      });
    },
    getJmxContent() {
      // console.log(this.report.testId);
      if (!this.report.testId) {
        return;
      }
      let threadGroups = [];
      this.result = this.$get('/performance/report/get-jmx-content/' + this.report.id)
        .then((response) => {
          let d = response.data.data;
          threadGroups = threadGroups.concat(findThreadGroup(d.jmx, d.name));
          threadGroups.forEach(tg => {
            tg.options = {};
          });
          this.threadGroups = threadGroups;
          this.getLoadConfig();

          // 兼容数据
          if (!threadGroups || threadGroups.length === 0) {
            this.result = this.$get('/performance/get-jmx-content/' + this.report.testId)
              .then((response) => {
                response.data.data.forEach(d => {
                  threadGroups = threadGroups.concat(findThreadGroup(d.jmx, d.name));
                  threadGroups.forEach(tg => {
                    tg.options = {};
                  });
                  this.threadGroups = threadGroups;
                  this.getLoadConfig();
                });
              })
              .catch(() => {
              });
          }
        }).catch(() => {
        });
    },
    calculateTotalChart() {
      let handler = this;
      if (handler.duration < handler.rampUpTime) {
        handler.rampUpTime = handler.duration;
      }
      if (handler.rampUpTime < handler.step) {
        handler.step = handler.rampUpTime;
      }
      let color = ['#60acfc', '#32d3eb', '#5bc49f', '#feb64d', '#ff7c7c', '#9287e7', '#ca8622', '#bda29a', '#6e7074', '#546570', '#c4ccd3'];
      handler.options = {
        color: color,
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: []
        },
        yAxis: {
          type: 'value'
        },
        tooltip: {
          trigger: 'axis',
        },
        series: []
      };


      for (let i = 0; i < handler.threadGroups.length; i++) {
        if (handler.threadGroups[i].enabled === 'false' ||
          handler.threadGroups[i].deleted === 'true' ||
          handler.threadGroups[i].threadType === 'ITERATION') {
          continue;
        }
        let seriesData = {
          name: handler.threadGroups[i].attributes.testname,
          data: [],
          type: 'line',
          step: 'start',
          smooth: false,
          symbolSize: 5,
          showSymbol: false,
          lineStyle: {
            normal: {
              width: 1
            }
          },
          areaStyle: {
            normal: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
                offset: 0,
                color: hexToRgba(color[i % color.length], 0.3),
              }, {
                offset: 0.8,
                color: hexToRgba(color[i % color.length], 0),
              }], false),
              shadowColor: 'rgba(0, 0, 0, 0.1)',
              shadowBlur: 10
            }
          },
          itemStyle: {
            normal: {
              color: hexToRgb(color[i % color.length]),
              borderColor: 'rgba(137,189,2,0.27)',
              borderWidth: 12
            }
          },
        };

        let tg = handler.threadGroups[i];

        let timePeriod = Math.floor(tg.rampUpTime / tg.step);
        let timeInc = timePeriod;

        let threadPeriod = Math.floor(tg.threadNumber / tg.step);
        let threadInc1 = Math.floor(tg.threadNumber / tg.step);
        let threadInc2 = Math.ceil(tg.threadNumber / tg.step);
        let inc2count = tg.threadNumber - tg.step * threadInc1;

        let times = 1;
        switch (tg.unit) {
          case 'M':
            times *= 60;
            break;
          case 'H':
            times *= 3600;
            break;
          default:
            break;
        }
        let duration = tg.duration * times;
        for (let j = 0; j <= duration; j++) {
          // x 轴
          let xAxis = handler.options.xAxis.data;
          if (xAxis.indexOf(j) < 0) {
            xAxis.push(j);
          }

          if (tg.tgType === 'ThreadGroup') {
            seriesData.step = undefined;

            if (j === 0) {
              seriesData.data.push([0, 0]);
            }
            if (j >= tg.rampUpTime) {
              xAxis.push(duration);

              seriesData.data.push([j, tg.threadNumber]);
              seriesData.data.push([duration, tg.threadNumber]);
              break;
            }
          } else {
            seriesData.step = 'start';
            if (j > timePeriod) {
              timePeriod += timeInc;
              if (inc2count > 0) {
                threadPeriod = threadPeriod + threadInc2;
                inc2count--;
              } else {
                threadPeriod = threadPeriod + threadInc1;
              }
              if (threadPeriod > tg.threadNumber) {
                threadPeriod = tg.threadNumber;
                // 预热结束
                xAxis.push(duration);
                seriesData.data.push([duration, threadPeriod]);
                break;
              }
            }
            seriesData.data.push([j, threadPeriod]);
          }
        }
        handler.options.series.push(seriesData);
      }
    },
    calculateChart(threadGroup) {
      let handler = this;
      if (threadGroup) {
        handler = threadGroup;
      }
      if (handler.duration < handler.rampUpTime) {
        handler.rampUpTime = handler.duration;
      }
      if (handler.rampUpTime < handler.step) {
        handler.step = handler.rampUpTime;
      }
      handler.options = {
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: []
        },
        yAxis: {
          type: 'value'
        },
        tooltip: {
          trigger: 'axis',
          formatter: '{a}: {c0}',
          axisPointer: {
            lineStyle: {
              color: '#57617B'
            }
          }
        },
        series: [{
          name: 'User',
          data: [],
          type: 'line',
          step: 'start',
          smooth: false,
          symbolSize: 5,
          showSymbol: false,
          lineStyle: {
            normal: {
              width: 1
            }
          },
          areaStyle: {
            normal: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
                offset: 0,
                color: 'rgba(137, 189, 27, 0.3)'
              }, {
                offset: 0.8,
                color: 'rgba(137, 189, 27, 0)'
              }], false),
              shadowColor: 'rgba(0, 0, 0, 0.1)',
              shadowBlur: 10
            }
          },
          itemStyle: {
            normal: {
              color: 'rgb(137,189,27)',
              borderColor: 'rgba(137,189,2,0.27)',
              borderWidth: 12
            }
          },
        }]
      };
      let timePeriod = Math.floor(handler.rampUpTime / handler.step);
      let timeInc = timePeriod;

      let threadPeriod = Math.floor(handler.threadNumber / handler.step);
      let threadInc1 = Math.floor(handler.threadNumber / handler.step);
      let threadInc2 = Math.ceil(handler.threadNumber / handler.step);
      let inc2count = handler.threadNumber - handler.step * threadInc1;
      for (let i = 0; i <= handler.duration; i++) {
        // x 轴
        handler.options.xAxis.data.push(i);
        if (handler.tgType === 'ThreadGroup') {
          handler.options.series[0].step = undefined;

          if (i === 0) {
            handler.options.series[0].data.push([0, 0]);
          }
          if (i >= handler.rampUpTime) {
            handler.options.xAxis.data.push(handler.duration);

            handler.options.series[0].data.push([i, handler.threadNumber]);
            handler.options.series[0].data.push([handler.duration, handler.threadNumber]);
            break;
          }
        } else {
          handler.options.series[0].step = 'start';
          if (i > timePeriod) {
            timePeriod += timeInc;
            if (inc2count > 0) {
              threadPeriod = threadPeriod + threadInc2;
              inc2count--;
            } else {
              threadPeriod = threadPeriod + threadInc1;
            }
            if (threadPeriod > handler.threadNumber) {
              threadPeriod = handler.threadNumber;
              handler.options.xAxis.data.push(handler.duration);
              handler.options.series[0].data.push([handler.duration, handler.threadNumber]);
              break;
            }
            handler.options.series[0].data.push([i, threadPeriod]);
          } else {
            handler.options.series[0].data.push([i, threadPeriod]);
          }
        }
      }
      this.calculateTotalChart();
    },
    getUnitLabel(tg) {
      if (tg.unit === 'S') {
        return this.$t('schedule.cron.seconds');
      }
      if (tg.unit === 'M') {
        return this.$t('schedule.cron.minutes');
      }
      if (tg.unit === 'H') {
        return this.$t('schedule.cron.hours');
      }
      return this.$t('schedule.cron.seconds');
    },
  },
  watch: {
    report: {
      handler() {
        this.getJmxContent();
      },
      deep: true
    },
  }
};
</script>


<style scoped>
.pressure-config-container .el-input {
  width: 130px;

}

.pressure-config-container .config-form-label {
  width: 130px;
}

.pressure-config-container .input-bottom-border input {
  border: 0;
  border-bottom: 1px solid #DCDFE6;
}

.chart-container {
  width: 100%;
  height: 300px;
}

.el-col .el-form {
  margin-top: 15px;
  text-align: left;
}

.el-col {
  margin-top: 15px;
  text-align: left;
}

.title {
  margin-left: 60px;
}

.wordwrap {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
