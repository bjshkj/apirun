package io.metersphere.streaming.service;

import io.metersphere.streaming.base.domain.LoadTestReportLog;
import io.metersphere.streaming.base.domain.LoadTestReportLogExample;
import io.metersphere.streaming.base.mapper.LoadTestReportLogMapper;
import io.metersphere.streaming.model.Log;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.UUID;

@Service
public class LogResultService {
    @Resource
    private LoadTestReportLogMapper loadTestReportLogMapper;


    public void savePartContent(Log log) {
        String resourceId = log.getResourceId() + "_" + log.getResourceIndex();
        LoadTestReportLogExample example = new LoadTestReportLogExample();
        example.createCriteria().andReportIdEqualTo(log.getReportId()).andResourceIdEqualTo(resourceId);
        long part = loadTestReportLogMapper.countByExample(example);
        // 保存日志内容
        LoadTestReportLog record = new LoadTestReportLog();
        record.setId(UUID.randomUUID().toString());
        record.setReportId(log.getReportId());
        record.setResourceId(resourceId);
        record.setPart(part + 1);
        record.setContent(log.getContent());
        loadTestReportLogMapper.insert(record);
    }
}
