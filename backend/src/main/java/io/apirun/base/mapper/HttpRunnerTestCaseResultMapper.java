package io.apirun.base.mapper;

import io.apirun.base.domain.TestCaseResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface HttpRunnerTestCaseResultMapper {

    void saveTestCaseResult(TestCaseResult testCaseResult);

    List<TestCaseResult> selectByTaskId(@Param("reportId") String reportId);

    void delTestCaseResultByTaskId(@Param("reportId") String reportId);
}
