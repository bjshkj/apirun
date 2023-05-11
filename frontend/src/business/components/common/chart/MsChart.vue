<template>
  <chart
    v-if="loaded"
    :style="{'height': chartHeight, 'width': chartWidth}"
    class="ms-chart"
    :init-options="defaultInitOptions"
    :options="options"
    :theme="theme"
    :group="group"
    @click="onClick"
    @datazoom="datazoom"
    :watch-shallow="watchShallow"
    :manual-update="manualUpdate"
    :autoresize="autoresize"/>
</template>

<script>
export default {
  name: "MsChart",
  props: {
    options: Object,
    theme: [String, Object],
    initOptions: {
      type: Object,
      default() {
        return {
          renderer: 'canvas'
        }
      }
    },
    group: String,
    autoresize: Boolean,
    watchShallow: Boolean,
    manualUpdate: Boolean,
    height: {
      type: [Number, String],
      default() {
        return 400
      }
    },
    width: [Number, String]
  },
  data() {
    return {
      loaded: true,
      defaultInitOptions: this.initOptions
    }
  },
  computed: {
    chartHeight() {
      if (this.height.indexOf) {
        return this.height;
      } else {
        return this.height + 'px';
      }
    },
    chartWidth() {
      if (!this.width) {
        return this.width;
      }
      if (this.width.indexOf) {
        return this.width;
      } else {
        return this.width + 'px';
      }
    }
  },
  mounted() {

    //this.defaultInitOptions = this.defaultInitOptions || {};
    // 默认渲染svg
    // BUG: 渲染svg之后 图上的legend 太多会不显示
    // if (!this.defaultInitOptions.renderer) {
    // this.defaultInitOptions.renderer = 'svg';
    // }
  },
  methods: {
    onClick(params){
      this.$emit('onClick', params.data)
    },
    reload() {
      this.loaded = false;
      this.$nextTick(() => {
        this.loaded = true;
      })
    },
    datazoom(params) {
      this.$emit('datazoom', params);
    }
  }
}
</script>

<style scoped>

</style>
