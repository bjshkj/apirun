package io.apirun.base.mapper;

import io.apirun.base.domain.TestStepResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface HttprunnerTestStepResultMapper {
    void saveTestStepResult(TestStepResult testStepResult);

    List<TestStepResult> selectByTaskIdAndTestCaseIndex(@Param("taskId") String reportId, @Param("testcaseId") int index);

    void delTestStepResultByTaskId(@Param("taskId") String taskId);

    List<TestStepResult> selectByTaskId(@Param("taskId") String taskId);
}
