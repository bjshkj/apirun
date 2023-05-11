package io.apirun.base.mapper;

import io.apirun.base.domain.LoadTestFile;
import io.apirun.base.domain.LoadTestFileExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface LoadTestFileMapper {
    long countByExample(LoadTestFileExample example);

    int deleteByExample(LoadTestFileExample example);

    int insert(LoadTestFile record);

    int insertSelective(LoadTestFile record);

    List<LoadTestFile> selectByExample(LoadTestFileExample example);

    int updateByExampleSelective(@Param("record") LoadTestFile record, @Param("example") LoadTestFileExample example);

    int updateByExample(@Param("record") LoadTestFile record, @Param("example") LoadTestFileExample example);
}