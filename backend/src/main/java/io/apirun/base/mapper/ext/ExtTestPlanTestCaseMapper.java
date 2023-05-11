package io.apirun.base.mapper.ext;

import io.apirun.track.dto.TestCaseReportStatusResultDTO;
import io.apirun.track.dto.TestCaseTestDTO;
import io.apirun.track.dto.TestPlanCaseDTO;
import io.apirun.track.request.testplancase.QueryTestPlanCaseRequest;
import io.apirun.track.request.testplancase.TestPlanFuncCaseConditions;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtTestPlanTestCaseMapper {

    List<TestCaseReportStatusResultDTO> getReportMetric(@Param("planId") String planId);

    List<String> getExecutors(@Param("planId") String planId);

    List<TestPlanCaseDTO> list(@Param("request") QueryTestPlanCaseRequest request);

    List<TestPlanCaseDTO> listByPlanId(@Param("request") QueryTestPlanCaseRequest request);

    List<TestPlanCaseDTO> listByNode(@Param("request") QueryTestPlanCaseRequest request);

    List<TestPlanCaseDTO> listByNodes(@Param("request") QueryTestPlanCaseRequest request);

    List<String> findRelateTestPlanId(@Param("userId") String userId, @Param("workspaceId") String workspaceId, @Param("projectId") String projectId);

    List<TestPlanCaseDTO> getRecentTestedTestCase(@Param("request") QueryTestPlanCaseRequest request);

    List<TestPlanCaseDTO> getPendingTestCases(@Param("request") QueryTestPlanCaseRequest request);

    List<String> getStatusByPlanId(String planId);

    int updateTestCaseStates(@Param("ids") List<String> ids, @Param("reportStatus") String reportStatus);

    List<String> getTestPlanTestCaseIds(String testId);

    /**
     * 根据项目 ids 查询 TestPlanCaseDTO 列表
     * @param ids project id list
     * @return List<TestPlanCaseDTO>
     */
    List<TestPlanCaseDTO> listTestCaseByProjectIds(@Param("ids") List<String> ids);

    TestPlanCaseDTO get(String testPlanTestCaseId);

    void deleteByTestCaseID(String id);

    List<String> getExecResultByPlanId(String planId);

    List<TestPlanCaseDTO> listForMinder(@Param("request") QueryTestPlanCaseRequest request);

    List<TestCaseTestDTO> listTestCaseTest(@Param("caseId") String caseId);

    List<String> selectIds(@Param("request") TestPlanFuncCaseConditions conditions);
}
