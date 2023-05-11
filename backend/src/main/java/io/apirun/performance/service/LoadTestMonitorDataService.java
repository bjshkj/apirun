package io.apirun.performance.service;

import io.apirun.base.domain.LoadTestMonitorDataExample;
import io.apirun.base.domain.LoadTestMonitorDataWithBLOBs;
import io.apirun.base.mapper.LoadTestMonitorDataMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
@Service
@Transactional(rollbackFor = Exception.class)
public class LoadTestMonitorDataService {
    @Resource
    private LoadTestMonitorDataMapper loadTestMonitorDataMapper;

    public void deleteMonitorData(String reportId){
        List<LoadTestMonitorDataWithBLOBs> list = queryByReport(reportId);
        if(list.isEmpty()){
            return;
        }
        LoadTestMonitorDataExample example = new LoadTestMonitorDataExample();
        example.createCriteria().andReportIdEqualTo(reportId);
        loadTestMonitorDataMapper.deleteByExample(example);
    }

    public List<LoadTestMonitorDataWithBLOBs> getOnlyOneMonitorData(String reportId, String target, String targetType, String pointerType){
        LoadTestMonitorDataExample example = new LoadTestMonitorDataExample();
        example.createCriteria().andReportIdEqualTo(reportId).andTargetEqualTo(target).andTargetTypeEqualTo(targetType).andPointerTypeEqualTo(pointerType);
        List<LoadTestMonitorDataWithBLOBs> monitorData = loadTestMonitorDataMapper.selectByExampleWithBLOBs(example);
        return monitorData;
    }

    public List<LoadTestMonitorDataWithBLOBs> queryByReport(String reportId){
        LoadTestMonitorDataExample example = new LoadTestMonitorDataExample();
        example.createCriteria().andReportIdEqualTo(reportId);
        List<LoadTestMonitorDataWithBLOBs> monitorData = loadTestMonitorDataMapper.selectByExampleWithBLOBs(example);
        return monitorData;
    }
}
