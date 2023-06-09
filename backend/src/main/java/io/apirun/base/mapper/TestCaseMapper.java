package io.apirun.base.mapper;

import io.apirun.base.domain.TestCase;
import io.apirun.base.domain.TestCaseExample;
import io.apirun.base.domain.TestCaseWithBLOBs;
import java.util.List;

import io.apirun.controller.request.BaseQueryRequest;
import org.apache.ibatis.annotations.Param;

public interface TestCaseMapper {
    long countByExample(TestCaseExample example);

    int deleteByExample(TestCaseExample example);

    int deleteByPrimaryKey(String id);

    int insert(TestCaseWithBLOBs record);

    int insertSelective(TestCaseWithBLOBs record);

    List<TestCaseWithBLOBs> selectByExampleWithBLOBs(TestCaseExample example);

    List<TestCase> selectByExample(TestCaseExample example);

    TestCaseWithBLOBs selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") TestCaseWithBLOBs record, @Param("example") TestCaseExample example);

    int updateByExampleWithBLOBs(@Param("record") TestCaseWithBLOBs record, @Param("example") TestCaseExample example);

    int updateByExample(@Param("record") TestCase record, @Param("example") TestCaseExample example);

    int updateByPrimaryKeySelective(TestCaseWithBLOBs record);

    int updateByPrimaryKeyWithBLOBs(TestCaseWithBLOBs record);

    int updateByPrimaryKey(TestCase record);

    List<String> selectIdsByQuery(BaseQueryRequest query);
}
