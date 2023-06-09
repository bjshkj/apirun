<template>
  <div>
    <el-table
      border
      :data="data"
      @sort-change="sort"
      @filter-change="filter"
      @select-all="handleSelectAll"
      @select="handleSelect"
      @header-dragend="headerDragend"
      @cell-mouse-enter="showPopover"
      class="test-content adjust-table ms-table"
      :class="{'ms-select-all-fixed':showSelectAll}"
      :height="screenHeight"
      ref="table" @row-click="handleRowClick">

      <el-table-column v-if="enableSelection" width="50" type="selection"/>

      <ms-table-header-select-popover v-if="enableSelection && showSelectAll" ref="selectPopover"
                                      :page-size="pageSize > total ? total : pageSize"
                                      :total="total"
                                      @selectPageAll="isSelectDataAll(false)"
                                      @selectAll="isSelectDataAll(true)"/>

      <el-table-column v-if="enableSelection && batchOperators && batchOperators.length > 0" width="40"
                       :resizable="false" align="center">
        <template v-slot:default="scope">
          <!-- 选中记录后浮现的按钮，提供对记录的批量操作 -->
          <show-more-btn :is-show="scope.row.showMore" :buttons="batchOperators" :size="selectDataCounts"/>
        </template>
      </el-table-column>

      <slot></slot>

      <el-table-column
        v-if="operators && operators.length > 0"
        :min-width="operatorWidth"
        fixed="right"
        :label="$t('commons.operating')">
        <template slot="header">
          <header-label-operate @exec="openCustomHeader"/>
        </template>
        <template v-slot:default="scope">
          <ms-table-operators :buttons="operators" :row="scope.row" :index="scope.$index"/>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import {
  _filter,
  _handleSelect,
  _handleSelectAll, _sort, getLabel,
  getSelectDataCounts,
  setUnSelectIds,
  toggleAllSelection,
  checkTableRowIsSelect,
} from "@/common/js/tableUtils";
import MsTableHeaderSelectPopover from "@/business/components/common/components/table/MsTableHeaderSelectPopover";
import {TEST_CASE_LIST} from "@/common/js/constants";
import MsTablePagination from "@/business/components/common/pagination/TablePagination";
import ShowMoreBtn from "@/business/components/track/case/components/ShowMoreBtn";
import MsTableColumn from "@/business/components/common/components/table/Ms-table-column";
import MsTableOperators from "@/business/components/common/components/MsTableOperators";
import HeaderLabelOperate from "@/business/components/common/head/HeaderLabelOperate";
import HeaderCustom from "@/business/components/common/head/HeaderCustom";

export default {
  name: "MsTable",
  components: {
    HeaderLabelOperate,
    MsTableOperators, MsTableColumn, ShowMoreBtn, MsTablePagination, MsTableHeaderSelectPopover, HeaderCustom
  },
  data() {
    return {
      selectDataCounts: 0,
      selectRows: new Set(),
      selectIds: []
    };
  },
  props: {
    screenHeight: {
      type: [String, Number],
      default: 400,
    },
    selectNodeIds: {
      type: Array,
      default() {
        return [];
      }
    },
    data: {
      type: Array,
      default() {
        return [];
      }
    },
    condition: {
      type: Object,
      default() {
        return {};
      }
    },
    pageSize: {
      type: Number,
      default() {
        return 10;
      }
    },
    total: {
      type: Number,
      default() {
        return 10;
      }
    },
    // 操作按钮
    operators: {
      type: Array,
      default() {
        return [];
      }
    },
    //批量操作按钮
    batchOperators: {
      type: Array,
      default() {
        return [];
      }
    },
    // 操作列的宽度
    operatorWidth: {
      type: String,
      default() {
        return "150px";
      }
    },
    //开启全选
    enableSelection: {
      type: Boolean,
      default() {
        return true;
      }
    }, //开启全选
    showSelectAll: {
      type: Boolean,
      default() {
        return true;
      }
    }
  },
  mounted() {
    getLabel(this, TEST_CASE_LIST);
  },
  created() {
  },
  watch: {
    selectNodeIds() {
      this.selectDataCounts = 0;
    },
  },
  methods: {
    openCustomHeader() {
      this.$emit("openCustomHeader");
    },
    handleSelectAll(selection) {
      _handleSelectAll(this, selection, this.data, this.selectRows);
      setUnSelectIds(this.data, this.condition, this.selectRows);
      this.selectDataCounts = getSelectDataCounts(this.condition, this.total, this.selectRows);
      this.selectIds = Array.from(this.selectRows).map(o => o.id);
    },
    handleSelect(selection, row) {
      _handleSelect(this, selection, row, this.selectRows);
      setUnSelectIds(this.data, this.condition, this.selectRows);
      this.selectDataCounts = getSelectDataCounts(this.condition, this.total, this.selectRows);
      this.selectIds = Array.from(this.selectRows).map(o => o.id);
    },
    isSelectDataAll(data) {
      this.condition.selectAll = data;
      //设置勾选
      toggleAllSelection(this.$refs.table, this.data, this.selectRows);
      //显示隐藏菜单
      _handleSelectAll(this, this.data, this.data, this.selectRows);
      //设置未选择ID(更新)
      this.condition.unSelectIds = [];
      //更新统计信息
      this.selectDataCounts = getSelectDataCounts(this.condition, this.total, this.selectRows);
      this.selectIds = Array.from(this.selectRows).map(o => o.id);
    },
    headerDragend(newWidth, oldWidth, column, event) {
      // let finalWidth = newWidth;
      // if (column.minWidth > finalWidth) {
      //   finalWidth = column.minWidth;
      // }
      // column.width = finalWidth;
      // column.realWidth = finalWidth;
    },
    showPopover(row, column, cell) {
      if (column.property === 'name') {
        this.currentCaseId = row.id;
      }
    },
    doLayout() {
      setTimeout(this.$refs.table.doLayout(), 200);
    },
    filter(filters) {
      _filter(filters, this.condition);
      this.handleRefresh();
    },
    sort(column) {
      // 每次只对一个字段排序
      if (this.condition.orders) {
        this.condition.orders = [];
      }
      _sort(column, this.condition);
      this.handleRefresh();
    },
    handleBatchEdit() {
      this.getMaintainerOptions();
      this.$refs.batchEdit.open();
    },
    handleBatchMove() {
      this.$refs.testBatchMove.open(this.treeNodes, Array.from(this.selectRows).map(row => row.id), this.moduleOptions);
    },
    handleRowClick() {
      this.$emit("handleRowClick");
    },
    handleRefresh() {
      this.clear();
      this.$emit('refresh');
    },
    handlePageChange() {
      this.$emit('pageChange');
    },
    clear() {
      this.clearSelectRows();
    },
    checkTableRowIsSelect() {
      checkTableRowIsSelect(this, this.condition, this.data, this.$refs.table, this.selectRows);
    },
    clearSelection() {
      this.clearSelectRows();
    },
    getSelectRows() {
      return this.selectRows;
    },
    clearSelectRows() {
      this.selectRows.clear();
      this.selectIds = [];
      this.selectDataCounts = 0;
      if (this.$refs.table) {
        this.$refs.table.clearSelection();
      }
    },
  }
};
</script>

<style scoped>

</style>
