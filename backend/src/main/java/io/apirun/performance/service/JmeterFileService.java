package io.apirun.performance.service;


import com.alibaba.excel.util.CollectionUtils;
import io.apirun.base.domain.LoadTestReportWithBLOBs;
import io.apirun.base.domain.LoadTestWithBLOBs;
import io.apirun.base.mapper.LoadTestMapper;
import io.apirun.base.mapper.ext.ExtLoadTestReportMapper;
import io.apirun.commons.exception.MSException;
import io.apirun.commons.utils.LogUtil;
import io.apirun.performance.engine.EngineContext;
import io.apirun.performance.engine.EngineFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@Transactional(rollbackFor = Exception.class)
public class JmeterFileService {

    @Resource
    private LoadTestMapper loadTestMapper;
    @Resource
    private ExtLoadTestReportMapper extLoadTestReportMapper;

    public byte[] downloadZip(String testId, double[] ratios, String reportId, int resourceIndex) {
        try {
            LoadTestWithBLOBs loadTest = loadTestMapper.selectByPrimaryKey(testId);
            EngineContext context = EngineFactory.createContext(loadTest, ratios, reportId, resourceIndex);
            return zipFilesToByteArray(context);
        } catch (MSException e) {
            LogUtil.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LogUtil.error(e.getMessage(), e);
            MSException.throwException(e);
        }
        return null;
    }

    private byte[] zipFilesToByteArray(EngineContext context) throws IOException {
        String testId = context.getTestId();
        String fileName = testId + ".jmx";

        Map<String, byte[]> files = new HashMap<>();

        //  每个测试生成一个文件夹
        files.put(fileName, context.getContent().getBytes(StandardCharsets.UTF_8));
        // 保存jmx
        LoadTestReportWithBLOBs record = new LoadTestReportWithBLOBs();
        record.setId(context.getReportId());
        record.setJmxContent(context.getContent());
        extLoadTestReportMapper.updateJmxContentIfAbsent(record);
        // 保存 byte[]
        Map<String, byte[]> jarFiles = context.getTestResourceFiles();
        if (!CollectionUtils.isEmpty(jarFiles)) {
            for (String k : jarFiles.keySet()) {
                byte[] v = jarFiles.get(k);
                files.put(k, v);
            }
        }

        return listBytesToZip(files);
    }


    private byte[] listBytesToZip(Map<String, byte[]> mapReport) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);
        for (Map.Entry<String, byte[]> report : mapReport.entrySet()) {
            ZipEntry entry = new ZipEntry(report.getKey());
            entry.setSize(report.getValue().length);
            zos.putNextEntry(entry);
            zos.write(report.getValue());
        }
        zos.closeEntry();
        zos.close();
        return baos.toByteArray();
    }
}
