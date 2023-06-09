package io.apirun.commons.constants;

public enum ReportTriggerMode {
    MANUAL,
    SCHEDULE,
    API,
    /**
     * 性能测试用例执行触发报告
     */
    CASE,
    TEST_PLAN_SCHEDULE,
    ASSEMBLY_LINE
}
