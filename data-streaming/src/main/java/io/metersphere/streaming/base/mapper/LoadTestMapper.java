package io.metersphere.streaming.base.mapper;

import io.metersphere.streaming.base.domain.LoadTest;
import io.metersphere.streaming.base.domain.LoadTestExample;
import io.metersphere.streaming.base.domain.LoadTestWithBLOBs;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface LoadTestMapper {
    long countByExample(LoadTestExample example);

    int deleteByExample(LoadTestExample example);

    int deleteByPrimaryKey(String id);

    int insert(LoadTestWithBLOBs record);

    int insertSelective(LoadTestWithBLOBs record);

    List<LoadTestWithBLOBs> selectByExampleWithBLOBs(LoadTestExample example);

    List<LoadTest> selectByExample(LoadTestExample example);

    LoadTestWithBLOBs selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") LoadTestWithBLOBs record, @Param("example") LoadTestExample example);

    int updateByExampleWithBLOBs(@Param("record") LoadTestWithBLOBs record, @Param("example") LoadTestExample example);

    int updateByExample(@Param("record") LoadTest record, @Param("example") LoadTestExample example);

    int updateByPrimaryKeySelective(LoadTestWithBLOBs record);

    int updateByPrimaryKeyWithBLOBs(LoadTestWithBLOBs record);

    int updateByPrimaryKey(LoadTest record);
}