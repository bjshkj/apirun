package io.metersphere.streaming.base.mapper;

import io.metersphere.streaming.base.domain.FileMetadata;
import io.metersphere.streaming.base.domain.FileMetadataExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FileMetadataMapper {
    long countByExample(FileMetadataExample example);

    int deleteByExample(FileMetadataExample example);

    int deleteByPrimaryKey(String id);

    int insert(FileMetadata record);

    int insertSelective(FileMetadata record);

    List<FileMetadata> selectByExample(FileMetadataExample example);

    FileMetadata selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") FileMetadata record, @Param("example") FileMetadataExample example);

    int updateByExample(@Param("record") FileMetadata record, @Param("example") FileMetadataExample example);

    int updateByPrimaryKeySelective(FileMetadata record);

    int updateByPrimaryKey(FileMetadata record);
}