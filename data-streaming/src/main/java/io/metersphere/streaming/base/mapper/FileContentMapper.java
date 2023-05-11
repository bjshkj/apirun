package io.metersphere.streaming.base.mapper;

import io.metersphere.streaming.base.domain.FileContent;
import io.metersphere.streaming.base.domain.FileContentExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FileContentMapper {
    long countByExample(FileContentExample example);

    int deleteByExample(FileContentExample example);

    int deleteByPrimaryKey(String fileId);

    int insert(FileContent record);

    int insertSelective(FileContent record);

    List<FileContent> selectByExampleWithBLOBs(FileContentExample example);

    List<FileContent> selectByExample(FileContentExample example);

    FileContent selectByPrimaryKey(String fileId);

    int updateByExampleSelective(@Param("record") FileContent record, @Param("example") FileContentExample example);

    int updateByExampleWithBLOBs(@Param("record") FileContent record, @Param("example") FileContentExample example);

    int updateByExample(@Param("record") FileContent record, @Param("example") FileContentExample example);

    int updateByPrimaryKeySelective(FileContent record);

    int updateByPrimaryKeyWithBLOBs(FileContent record);
}