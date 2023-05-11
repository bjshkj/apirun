package io.apirun.base.mapper;

import io.apirun.base.domain.SystemHeader;
import io.apirun.base.domain.SystemHeaderExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SystemHeaderMapper {
    long countByExample(SystemHeaderExample example);

    int deleteByExample(SystemHeaderExample example);

    int deleteByPrimaryKey(String type);

    int insert(SystemHeader record);

    int insertSelective(SystemHeader record);

    List<SystemHeader> selectByExample(SystemHeaderExample example);

    SystemHeader selectByPrimaryKey(String type);

    int updateByExampleSelective(@Param("record") SystemHeader record, @Param("example") SystemHeaderExample example);

    int updateByExample(@Param("record") SystemHeader record, @Param("example") SystemHeaderExample example);

    int updateByPrimaryKeySelective(SystemHeader record);

    int updateByPrimaryKey(SystemHeader record);
}