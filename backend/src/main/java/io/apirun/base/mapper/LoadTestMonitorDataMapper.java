package io.apirun.base.mapper;

import java.util.List;

import io.apirun.base.domain.LoadTestMonitorData;
import io.apirun.base.domain.LoadTestMonitorDataExample;
import io.apirun.base.domain.LoadTestMonitorDataWithBLOBs;
import org.apache.ibatis.annotations.Param;

public interface LoadTestMonitorDataMapper {
    long countByExample(LoadTestMonitorDataExample example);

    int deleteByExample(LoadTestMonitorDataExample example);

    int deleteByPrimaryKey(Long id);

    int insert(LoadTestMonitorDataWithBLOBs record);

    int insertSelective(LoadTestMonitorDataWithBLOBs record);

    List<LoadTestMonitorDataWithBLOBs> selectByExampleWithBLOBs(LoadTestMonitorDataExample example);

    List<LoadTestMonitorData> selectByExample(LoadTestMonitorDataExample example);

    LoadTestMonitorDataWithBLOBs selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") LoadTestMonitorDataWithBLOBs record, @Param("example") LoadTestMonitorDataExample example);

    int updateByExampleWithBLOBs(@Param("record") LoadTestMonitorDataWithBLOBs record, @Param("example") LoadTestMonitorDataExample example);

    int updateByExample(@Param("record") LoadTestMonitorData record, @Param("example") LoadTestMonitorDataExample example);

    int updateByPrimaryKeySelective(LoadTestMonitorDataWithBLOBs record);

    int updateByPrimaryKeyWithBLOBs(LoadTestMonitorDataWithBLOBs record);

    int updateByPrimaryKey(LoadTestMonitorData record);
}