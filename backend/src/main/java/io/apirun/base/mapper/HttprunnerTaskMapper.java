package io.apirun.base.mapper;

import io.apirun.base.domain.HttprunnerTask;
import io.apirun.base.domain.HttprunnerTaskExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface HttprunnerTaskMapper {
    long countByExample(HttprunnerTaskExample example);

    int deleteByExample(HttprunnerTaskExample example);

    int deleteByPrimaryKey(@Param("id") String id);

    int insert(HttprunnerTask record);

    int insertSelective(HttprunnerTask record);

    List<HttprunnerTask> selectByExampleWithBLOBs(HttprunnerTaskExample example);

    List<HttprunnerTask> selectByExample(HttprunnerTaskExample example);

    HttprunnerTask selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") HttprunnerTask record, @Param("example") HttprunnerTaskExample example);

    int updateByExampleWithBLOBs(@Param("record") HttprunnerTask record, @Param("example") HttprunnerTaskExample example);

    int updateByExample(@Param("record") HttprunnerTask record, @Param("example") HttprunnerTaskExample example);

    int updateByPrimaryKeySelective(HttprunnerTask record);

    int updateByPrimaryKeyWithBLOBs(HttprunnerTask record);

    int updateByPrimaryKey(HttprunnerTask record);
}