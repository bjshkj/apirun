package io.metersphere.streaming.service;

import io.metersphere.streaming.base.domain.FileContent;
import io.metersphere.streaming.base.domain.FileMetadata;
import io.metersphere.streaming.base.mapper.FileContentMapper;
import io.metersphere.streaming.base.mapper.FileMetadataMapper;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;

@Service
public class FileService {
    @Resource
    private FileMetadataMapper fileMetadataMapper;
    @Resource
    private FileContentMapper fileContentMapper;

    public FileMetadata saveFile(File file, String reportId) {
        final FileMetadata fileMetadata = new FileMetadata();
        fileMetadata.setId(reportId);
        fileMetadata.setName(file.getName());
        fileMetadata.setSize(FileUtils.sizeOf(file));
        fileMetadata.setCreateTime(System.currentTimeMillis());
        fileMetadata.setUpdateTime(System.currentTimeMillis());
        fileMetadata.setType("JTL");
        if (fileMetadataMapper.updateByPrimaryKeySelective(fileMetadata) == 0) {
            fileMetadataMapper.insert(fileMetadata);
        }

        FileContent fileContent = new FileContent();
        fileContent.setFileId(fileMetadata.getId());
        try {
            fileContent.setFile(FileUtils.readFileToByteArray(file));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (fileContentMapper.updateByPrimaryKeySelective(fileContent) == 0) {
            fileContentMapper.insert(fileContent);
        }

        return fileMetadata;
    }

}