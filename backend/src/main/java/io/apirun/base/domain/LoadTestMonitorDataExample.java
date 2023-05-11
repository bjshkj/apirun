package io.apirun.base.domain;

import java.util.ArrayList;
import java.util.List;

public class LoadTestMonitorDataExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public LoadTestMonitorDataExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andIdIsNull() {
            addCriterion("id is null");
            return (Criteria) this;
        }

        public Criteria andIdIsNotNull() {
            addCriterion("id is not null");
            return (Criteria) this;
        }

        public Criteria andIdEqualTo(Long value) {
            addCriterion("id =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(Long value) {
            addCriterion("id <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(Long value) {
            addCriterion("id >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(Long value) {
            addCriterion("id >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(Long value) {
            addCriterion("id <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(Long value) {
            addCriterion("id <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<Long> values) {
            addCriterion("id in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<Long> values) {
            addCriterion("id not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(Long value1, Long value2) {
            addCriterion("id between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(Long value1, Long value2) {
            addCriterion("id not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andReportIdIsNull() {
            addCriterion("report_id is null");
            return (Criteria) this;
        }

        public Criteria andReportIdIsNotNull() {
            addCriterion("report_id is not null");
            return (Criteria) this;
        }

        public Criteria andReportIdEqualTo(String value) {
            addCriterion("report_id =", value, "reportId");
            return (Criteria) this;
        }

        public Criteria andReportIdNotEqualTo(String value) {
            addCriterion("report_id <>", value, "reportId");
            return (Criteria) this;
        }

        public Criteria andReportIdGreaterThan(String value) {
            addCriterion("report_id >", value, "reportId");
            return (Criteria) this;
        }

        public Criteria andReportIdGreaterThanOrEqualTo(String value) {
            addCriterion("report_id >=", value, "reportId");
            return (Criteria) this;
        }

        public Criteria andReportIdLessThan(String value) {
            addCriterion("report_id <", value, "reportId");
            return (Criteria) this;
        }

        public Criteria andReportIdLessThanOrEqualTo(String value) {
            addCriterion("report_id <=", value, "reportId");
            return (Criteria) this;
        }

        public Criteria andReportIdLike(String value) {
            addCriterion("report_id like", value, "reportId");
            return (Criteria) this;
        }

        public Criteria andReportIdNotLike(String value) {
            addCriterion("report_id not like", value, "reportId");
            return (Criteria) this;
        }

        public Criteria andReportIdIn(List<String> values) {
            addCriterion("report_id in", values, "reportId");
            return (Criteria) this;
        }

        public Criteria andReportIdNotIn(List<String> values) {
            addCriterion("report_id not in", values, "reportId");
            return (Criteria) this;
        }

        public Criteria andReportIdBetween(String value1, String value2) {
            addCriterion("report_id between", value1, value2, "reportId");
            return (Criteria) this;
        }

        public Criteria andReportIdNotBetween(String value1, String value2) {
            addCriterion("report_id not between", value1, value2, "reportId");
            return (Criteria) this;
        }

        public Criteria andTargetIsNull() {
            addCriterion("target is null");
            return (Criteria) this;
        }

        public Criteria andTargetIsNotNull() {
            addCriterion("target is not null");
            return (Criteria) this;
        }

        public Criteria andTargetEqualTo(String value) {
            addCriterion("target =", value, "target");
            return (Criteria) this;
        }

        public Criteria andTargetNotEqualTo(String value) {
            addCriterion("target <>", value, "target");
            return (Criteria) this;
        }

        public Criteria andTargetGreaterThan(String value) {
            addCriterion("target >", value, "target");
            return (Criteria) this;
        }

        public Criteria andTargetGreaterThanOrEqualTo(String value) {
            addCriterion("target >=", value, "target");
            return (Criteria) this;
        }

        public Criteria andTargetLessThan(String value) {
            addCriterion("target <", value, "target");
            return (Criteria) this;
        }

        public Criteria andTargetLessThanOrEqualTo(String value) {
            addCriterion("target <=", value, "target");
            return (Criteria) this;
        }

        public Criteria andTargetLike(String value) {
            addCriterion("target like", value, "target");
            return (Criteria) this;
        }

        public Criteria andTargetNotLike(String value) {
            addCriterion("target not like", value, "target");
            return (Criteria) this;
        }

        public Criteria andTargetIn(List<String> values) {
            addCriterion("target in", values, "target");
            return (Criteria) this;
        }

        public Criteria andTargetNotIn(List<String> values) {
            addCriterion("target not in", values, "target");
            return (Criteria) this;
        }

        public Criteria andTargetBetween(String value1, String value2) {
            addCriterion("target between", value1, value2, "target");
            return (Criteria) this;
        }

        public Criteria andTargetNotBetween(String value1, String value2) {
            addCriterion("target not between", value1, value2, "target");
            return (Criteria) this;
        }

        public Criteria andTargetTypeIsNull() {
            addCriterion("target_type is null");
            return (Criteria) this;
        }

        public Criteria andTargetTypeIsNotNull() {
            addCriterion("target_type is not null");
            return (Criteria) this;
        }

        public Criteria andTargetTypeEqualTo(String value) {
            addCriterion("target_type =", value, "targetType");
            return (Criteria) this;
        }

        public Criteria andTargetTypeNotEqualTo(String value) {
            addCriterion("target_type <>", value, "targetType");
            return (Criteria) this;
        }

        public Criteria andTargetTypeGreaterThan(String value) {
            addCriterion("target_type >", value, "targetType");
            return (Criteria) this;
        }

        public Criteria andTargetTypeGreaterThanOrEqualTo(String value) {
            addCriterion("target_type >=", value, "targetType");
            return (Criteria) this;
        }

        public Criteria andTargetTypeLessThan(String value) {
            addCriterion("target_type <", value, "targetType");
            return (Criteria) this;
        }

        public Criteria andTargetTypeLessThanOrEqualTo(String value) {
            addCriterion("target_type <=", value, "targetType");
            return (Criteria) this;
        }

        public Criteria andTargetTypeLike(String value) {
            addCriterion("target_type like", value, "targetType");
            return (Criteria) this;
        }

        public Criteria andTargetTypeNotLike(String value) {
            addCriterion("target_type not like", value, "targetType");
            return (Criteria) this;
        }

        public Criteria andTargetTypeIn(List<String> values) {
            addCriterion("target_type in", values, "targetType");
            return (Criteria) this;
        }

        public Criteria andTargetTypeNotIn(List<String> values) {
            addCriterion("target_type not in", values, "targetType");
            return (Criteria) this;
        }

        public Criteria andTargetTypeBetween(String value1, String value2) {
            addCriterion("target_type between", value1, value2, "targetType");
            return (Criteria) this;
        }

        public Criteria andTargetTypeNotBetween(String value1, String value2) {
            addCriterion("target_type not between", value1, value2, "targetType");
            return (Criteria) this;
        }

        public Criteria andPointerTypeIsNull() {
            addCriterion("pointer_type is null");
            return (Criteria) this;
        }

        public Criteria andPointerTypeIsNotNull() {
            addCriterion("pointer_type is not null");
            return (Criteria) this;
        }

        public Criteria andPointerTypeEqualTo(String value) {
            addCriterion("pointer_type =", value, "pointerType");
            return (Criteria) this;
        }

        public Criteria andPointerTypeNotEqualTo(String value) {
            addCriterion("pointer_type <>", value, "pointerType");
            return (Criteria) this;
        }

        public Criteria andPointerTypeGreaterThan(String value) {
            addCriterion("pointer_type >", value, "pointerType");
            return (Criteria) this;
        }

        public Criteria andPointerTypeGreaterThanOrEqualTo(String value) {
            addCriterion("pointer_type >=", value, "pointerType");
            return (Criteria) this;
        }

        public Criteria andPointerTypeLessThan(String value) {
            addCriterion("pointer_type <", value, "pointerType");
            return (Criteria) this;
        }

        public Criteria andPointerTypeLessThanOrEqualTo(String value) {
            addCriterion("pointer_type <=", value, "pointerType");
            return (Criteria) this;
        }

        public Criteria andPointerTypeLike(String value) {
            addCriterion("pointer_type like", value, "pointerType");
            return (Criteria) this;
        }

        public Criteria andPointerTypeNotLike(String value) {
            addCriterion("pointer_type not like", value, "pointerType");
            return (Criteria) this;
        }

        public Criteria andPointerTypeIn(List<String> values) {
            addCriterion("pointer_type in", values, "pointerType");
            return (Criteria) this;
        }

        public Criteria andPointerTypeNotIn(List<String> values) {
            addCriterion("pointer_type not in", values, "pointerType");
            return (Criteria) this;
        }

        public Criteria andPointerTypeBetween(String value1, String value2) {
            addCriterion("pointer_type between", value1, value2, "pointerType");
            return (Criteria) this;
        }

        public Criteria andPointerTypeNotBetween(String value1, String value2) {
            addCriterion("pointer_type not between", value1, value2, "pointerType");
            return (Criteria) this;
        }

        public Criteria andNumberIcalUnitIsNull() {
            addCriterion("number_ical_unit is null");
            return (Criteria) this;
        }

        public Criteria andNumberIcalUnitIsNotNull() {
            addCriterion("number_ical_unit is not null");
            return (Criteria) this;
        }

        public Criteria andNumberIcalUnitEqualTo(String value) {
            addCriterion("number_ical_unit =", value, "numberIcalUnit");
            return (Criteria) this;
        }

        public Criteria andNumberIcalUnitNotEqualTo(String value) {
            addCriterion("number_ical_unit <>", value, "numberIcalUnit");
            return (Criteria) this;
        }

        public Criteria andNumberIcalUnitGreaterThan(String value) {
            addCriterion("number_ical_unit >", value, "numberIcalUnit");
            return (Criteria) this;
        }

        public Criteria andNumberIcalUnitGreaterThanOrEqualTo(String value) {
            addCriterion("number_ical_unit >=", value, "numberIcalUnit");
            return (Criteria) this;
        }

        public Criteria andNumberIcalUnitLessThan(String value) {
            addCriterion("number_ical_unit <", value, "numberIcalUnit");
            return (Criteria) this;
        }

        public Criteria andNumberIcalUnitLessThanOrEqualTo(String value) {
            addCriterion("number_ical_unit <=", value, "numberIcalUnit");
            return (Criteria) this;
        }

        public Criteria andNumberIcalUnitLike(String value) {
            addCriterion("number_ical_unit like", value, "numberIcalUnit");
            return (Criteria) this;
        }

        public Criteria andNumberIcalUnitNotLike(String value) {
            addCriterion("number_ical_unit not like", value, "numberIcalUnit");
            return (Criteria) this;
        }

        public Criteria andNumberIcalUnitIn(List<String> values) {
            addCriterion("number_ical_unit in", values, "numberIcalUnit");
            return (Criteria) this;
        }

        public Criteria andNumberIcalUnitNotIn(List<String> values) {
            addCriterion("number_ical_unit not in", values, "numberIcalUnit");
            return (Criteria) this;
        }

        public Criteria andNumberIcalUnitBetween(String value1, String value2) {
            addCriterion("number_ical_unit between", value1, value2, "numberIcalUnit");
            return (Criteria) this;
        }

        public Criteria andNumberIcalUnitNotBetween(String value1, String value2) {
            addCriterion("number_ical_unit not between", value1, value2, "numberIcalUnit");
            return (Criteria) this;
        }

        public Criteria andMonitorNameIsNull() {
            addCriterion("monitor_name is null");
            return (Criteria) this;
        }

        public Criteria andMonitorNameIsNotNull() {
            addCriterion("monitor_name is not null");
            return (Criteria) this;
        }

        public Criteria andMonitorNameEqualTo(String value) {
            addCriterion("monitor_name =", value, "monitorName");
            return (Criteria) this;
        }

        public Criteria andMonitorNameNotEqualTo(String value) {
            addCriterion("monitor_name <>", value, "monitorName");
            return (Criteria) this;
        }

        public Criteria andMonitorNameGreaterThan(String value) {
            addCriterion("monitor_name >", value, "monitorName");
            return (Criteria) this;
        }

        public Criteria andMonitorNameGreaterThanOrEqualTo(String value) {
            addCriterion("monitor_name >=", value, "monitorName");
            return (Criteria) this;
        }

        public Criteria andMonitorNameLessThan(String value) {
            addCriterion("monitor_name <", value, "monitorName");
            return (Criteria) this;
        }

        public Criteria andMonitorNameLessThanOrEqualTo(String value) {
            addCriterion("monitor_name <=", value, "monitorName");
            return (Criteria) this;
        }

        public Criteria andMonitorNameLike(String value) {
            addCriterion("monitor_name like", value, "monitorName");
            return (Criteria) this;
        }

        public Criteria andMonitorNameNotLike(String value) {
            addCriterion("monitor_name not like", value, "monitorName");
            return (Criteria) this;
        }

        public Criteria andMonitorNameIn(List<String> values) {
            addCriterion("monitor_name in", values, "monitorName");
            return (Criteria) this;
        }

        public Criteria andMonitorNameNotIn(List<String> values) {
            addCriterion("monitor_name not in", values, "monitorName");
            return (Criteria) this;
        }

        public Criteria andMonitorNameBetween(String value1, String value2) {
            addCriterion("monitor_name between", value1, value2, "monitorName");
            return (Criteria) this;
        }

        public Criteria andMonitorNameNotBetween(String value1, String value2) {
            addCriterion("monitor_name not between", value1, value2, "monitorName");
            return (Criteria) this;
        }

        public Criteria andStartTimeIsNull() {
            addCriterion("start_time is null");
            return (Criteria) this;
        }

        public Criteria andStartTimeIsNotNull() {
            addCriterion("start_time is not null");
            return (Criteria) this;
        }

        public Criteria andStartTimeEqualTo(Long value) {
            addCriterion("start_time =", value, "startTime");
            return (Criteria) this;
        }

        public Criteria andStartTimeNotEqualTo(Long value) {
            addCriterion("start_time <>", value, "startTime");
            return (Criteria) this;
        }

        public Criteria andStartTimeGreaterThan(Long value) {
            addCriterion("start_time >", value, "startTime");
            return (Criteria) this;
        }

        public Criteria andStartTimeGreaterThanOrEqualTo(Long value) {
            addCriterion("start_time >=", value, "startTime");
            return (Criteria) this;
        }

        public Criteria andStartTimeLessThan(Long value) {
            addCriterion("start_time <", value, "startTime");
            return (Criteria) this;
        }

        public Criteria andStartTimeLessThanOrEqualTo(Long value) {
            addCriterion("start_time <=", value, "startTime");
            return (Criteria) this;
        }

        public Criteria andStartTimeIn(List<Long> values) {
            addCriterion("start_time in", values, "startTime");
            return (Criteria) this;
        }

        public Criteria andStartTimeNotIn(List<Long> values) {
            addCriterion("start_time not in", values, "startTime");
            return (Criteria) this;
        }

        public Criteria andStartTimeBetween(Long value1, Long value2) {
            addCriterion("start_time between", value1, value2, "startTime");
            return (Criteria) this;
        }

        public Criteria andStartTimeNotBetween(Long value1, Long value2) {
            addCriterion("start_time not between", value1, value2, "startTime");
            return (Criteria) this;
        }

        public Criteria andEndTimeIsNull() {
            addCriterion("end_time is null");
            return (Criteria) this;
        }

        public Criteria andEndTimeIsNotNull() {
            addCriterion("end_time is not null");
            return (Criteria) this;
        }

        public Criteria andEndTimeEqualTo(Long value) {
            addCriterion("end_time =", value, "endTime");
            return (Criteria) this;
        }

        public Criteria andEndTimeNotEqualTo(Long value) {
            addCriterion("end_time <>", value, "endTime");
            return (Criteria) this;
        }

        public Criteria andEndTimeGreaterThan(Long value) {
            addCriterion("end_time >", value, "endTime");
            return (Criteria) this;
        }

        public Criteria andEndTimeGreaterThanOrEqualTo(Long value) {
            addCriterion("end_time >=", value, "endTime");
            return (Criteria) this;
        }

        public Criteria andEndTimeLessThan(Long value) {
            addCriterion("end_time <", value, "endTime");
            return (Criteria) this;
        }

        public Criteria andEndTimeLessThanOrEqualTo(Long value) {
            addCriterion("end_time <=", value, "endTime");
            return (Criteria) this;
        }

        public Criteria andEndTimeIn(List<Long> values) {
            addCriterion("end_time in", values, "endTime");
            return (Criteria) this;
        }

        public Criteria andEndTimeNotIn(List<Long> values) {
            addCriterion("end_time not in", values, "endTime");
            return (Criteria) this;
        }

        public Criteria andEndTimeBetween(Long value1, Long value2) {
            addCriterion("end_time between", value1, value2, "endTime");
            return (Criteria) this;
        }

        public Criteria andEndTimeNotBetween(Long value1, Long value2) {
            addCriterion("end_time not between", value1, value2, "endTime");
            return (Criteria) this;
        }

        public Criteria andTargetLabelIsNull() {
            addCriterion("target_label is null");
            return (Criteria) this;
        }

        public Criteria andTargetLabelIsNotNull() {
            addCriterion("target_label is not null");
            return (Criteria) this;
        }

        public Criteria andTargetLabelEqualTo(String value) {
            addCriterion("target_label =", value, "targetLabel");
            return (Criteria) this;
        }

        public Criteria andTargetLabelNotEqualTo(String value) {
            addCriterion("target_label <>", value, "targetLabel");
            return (Criteria) this;
        }

        public Criteria andTargetLabelGreaterThan(String value) {
            addCriterion("target_label >", value, "targetLabel");
            return (Criteria) this;
        }

        public Criteria andTargetLabelGreaterThanOrEqualTo(String value) {
            addCriterion("target_label >=", value, "targetLabel");
            return (Criteria) this;
        }

        public Criteria andTargetLabelLessThan(String value) {
            addCriterion("target_label <", value, "targetLabel");
            return (Criteria) this;
        }

        public Criteria andTargetLabelLessThanOrEqualTo(String value) {
            addCriterion("target_label <=", value, "targetLabel");
            return (Criteria) this;
        }

        public Criteria andTargetLabelLike(String value) {
            addCriterion("target_label like", value, "targetLabel");
            return (Criteria) this;
        }

        public Criteria andTargetLabelNotLike(String value) {
            addCriterion("target_label not like", value, "targetLabel");
            return (Criteria) this;
        }

        public Criteria andTargetLabelIn(List<String> values) {
            addCriterion("target_label in", values, "targetLabel");
            return (Criteria) this;
        }

        public Criteria andTargetLabelNotIn(List<String> values) {
            addCriterion("target_label not in", values, "targetLabel");
            return (Criteria) this;
        }

        public Criteria andTargetLabelBetween(String value1, String value2) {
            addCriterion("target_label between", value1, value2, "targetLabel");
            return (Criteria) this;
        }

        public Criteria andTargetLabelNotBetween(String value1, String value2) {
            addCriterion("target_label not between", value1, value2, "targetLabel");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}