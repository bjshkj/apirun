package io.metersphere.streaming.job;

import com.github.pagehelper.PageHelper;
import io.metersphere.streaming.base.domain.LoadTestReport;
import io.metersphere.streaming.base.domain.LoadTestReportDetailExample;
import io.metersphere.streaming.base.domain.LoadTestReportExample;
import io.metersphere.streaming.base.mapper.LoadTestReportDetailMapper;
import io.metersphere.streaming.base.mapper.LoadTestReportMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

import static io.metersphere.streaming.commons.constants.TestStatus.Completed;

@Component
public class LoadTestReportDetailCleanJob {
    @Resource
    private LoadTestReportDetailMapper loadTestReportDetailMapper;
    @Resource
    private LoadTestReportMapper loadTestReportMapper;

    /**
     * 每天处理一次清理任务
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void cleanCompletedTestDetail() {
        LoadTestReportExample example = new LoadTestReportExample();
        example.createCriteria().andStatusEqualTo(Completed.name());
        long count = loadTestReportMapper.countByExample(example);
        for (int i = 0; i < count / 10; i++) {
            PageHelper.startPage(i, 10);
            List<LoadTestReport> loadTestReports = loadTestReportMapper.selectByExample(example);
            loadTestReports.forEach(report -> {
                // 清理文件
                LoadTestReportDetailExample example2 = new LoadTestReportDetailExample();
                example2.createCriteria().andReportIdEqualTo(report.getId());
                loadTestReportDetailMapper.deleteByExample(example2);
            });
        }
    }
}
