<template>
  <el-dialog
    :close-on-click-modal="false"
    title="添加监控"
    :visible.sync="dialogVisible"
    width="70%"
    @closed="closeFunc"
    :destroy-on-close="true"
    v-loading="result.loading"
  >
    <el-form :model="form" label-position="right" label-width="140px" size="small" :rules="rule" ref="monitorForm">
      <el-form-item :label="$t('commons.name')" prop="name">
        <el-input v-model="form.name" autocomplete="off"/>
      </el-form-item>
      <h4 style="margin-left: 80px;">监控配置</h4>
      <el-row>
<!--        <el-col :span="12">
          <el-form-item label="IP" prop="ip">
            <el-input v-model="form.ip" autocomplete="off"/>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="Port" prop="port">
            <el-input-number v-model="form.port" :min="1" :max="65535"/>
          </el-form-item>
        </el-col>-->
        <el-col :span="12">
          <el-form-item label="监控项" prop="targetType">
            <el-select v-model="form.targetType" placeholder="请选择">
              <el-option
                v-for="item in options"
                :key="item.value"
                :label="item.label"
                :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="ip或端口号" prop="config">
            <el-input v-model="form.config" placeholder="请输入HULK中的机器名或IP或数据库端口号"/>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="描述" prop="description">
        <el-input v-model="form.description" autocomplete="off"/>
      </el-form-item>
    </el-form>

    <template v-slot:footer>
      <ms-dialog-footer
        v-if="index !== '' && index !== undefined"
        @cancel="dialogVisible = false"
        @confirm="update"/>
      <ms-dialog-footer
        v-else
        @cancel="dialogVisible = false"
        @confirm="create"/>
    </template>

  </el-dialog>
</template>

<script>

import MsDialogFooter from "@/business/components/common/components/MsDialogFooter";
import {CONFIG_TYPE} from "@/common/js/constants";

export default {
  name: "EditMonitor",
  components: {MsDialogFooter},
  props: {
    testId: String,
    list: Array,
  },
  data() {
    return {
      result: {},
      form: {},
      dialogVisible: false,
      rule: {
        name: {required: true, message: "名称必填", trigger: 'blur'},
        ip: {required: true, message: "ip必填", trigger: 'blur'},
        port: {required: true, message: "port必填", trigger: 'blur'},
        targetType: {required: true, message: "监控项必填", trigger: 'blur'},
        config: {required: true, message: "配置信息必填", trigger: 'blur'}
      },
      index: '',
      options: [{
        value: 'SERVER',
        label: '服务器地址'
      },{
        value: 'MYSQL',
        label: 'Mysql'
      },{
        value: 'REDIS',
        label: 'Redis'
      },{
        value: 'MONGO',
        label: 'Mongo'
      }],
    };
  },
  methods: {
    open(data, index) {
      this.index = '';
      this.dialogVisible = true;
      if (data) {
        const copy = JSON.parse(JSON.stringify(data));
        this.form = copy;
      }
      if (index !== '' && index !== undefined) {
        this.index = index;
      }
    },
    closeFunc() {
      this.form = {};
      this.dialogVisible = false;
    },
    update() {
      this.$refs.monitorForm.validate(valid => {
        if (valid) {
          this.list.splice(this.index, 1, this.form);
          this.$emit("update:list", this.list);
          this.dialogVisible = false;
        } else {
          return false;
        }
      });
    },
    create() {
      this.$refs.monitorForm.validate(valid => {
        if (valid) {
          // this.form.monitorStatus = CONFIG_TYPE.NOT;
          this.list.push(this.form);
          this.$emit("update:list", this.list);
          this.dialogVisible = false;
        } else {
          return false;
        }
      });
    },
  }
};
</script>

<style scoped>
.box {
  padding-left: 5px;
}
</style>
