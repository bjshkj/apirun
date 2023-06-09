package io.apirun.base.mapper;

import io.apirun.base.domain.TestCaseTest;
import io.apirun.base.domain.TestCaseTestExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface TestCaseTestMapper {
    long countByExample(TestCaseTestExample example);

    int deleteByExample(TestCaseTestExample example);

    int insert(TestCaseTest record);

    int insertSelective(TestCaseTest record);

    List<TestCaseTest> selectByExample(TestCaseTestExample example);

    int updateByExampleSelective(@Param("record") TestCaseTest record, @Param("example") TestCaseTestExample example);

    int updateByExample(@Param("record") TestCaseTest record, @Param("example") TestCaseTestExample example);
}