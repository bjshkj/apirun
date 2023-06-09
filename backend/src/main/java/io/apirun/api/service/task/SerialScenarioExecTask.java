/**
 *
 */
package io.apirun.api.service.task;

import io.apirun.api.dto.automation.RunScenarioRequest;
import io.apirun.api.jmeter.JMeterService;
import io.apirun.base.domain.ApiScenarioReport;
import io.apirun.base.mapper.ApiScenarioReportMapper;
import io.apirun.commons.constants.APITestStatus;
import io.apirun.commons.exception.MSException;
import io.apirun.commons.utils.LogUtil;
import org.apache.jorphan.collections.HashTree;

import java.util.concurrent.Callable;

public class SerialScenarioExecTask<T> implements Callable<T> {
    private RunScenarioRequest request;
    private JMeterService jMeterService;
    private ApiScenarioReportMapper apiScenarioReportMapper;
    private HashTree hashTree;
    ApiScenarioReport report = null;
    private String id;

    public SerialScenarioExecTask(JMeterService jMeterService, ApiScenarioReportMapper apiScenarioReportMapper, String id, HashTree hashTree, RunScenarioRequest request) {
        this.jMeterService = jMeterService;
        this.apiScenarioReportMapper = apiScenarioReportMapper;
        this.request = request;
        this.hashTree = hashTree;
        this.id = id;
    }

    @Override
    public T call() {
        try {
            jMeterService.runSerial(id, hashTree, request.getReportId(), request.getRunMode(), request.getConfig());
            // 轮询查看报告状态，最多200次，防止死循环
            int index = 1;
            while (index < 200) {
                Thread.sleep(3000);
                index++;
                report = apiScenarioReportMapper.selectByPrimaryKey(id);
                if (report != null && !report.getStatus().equals(APITestStatus.Running.name())) {
                    break;
                }
            }
            // 执行失败了，恢复报告状态
            if (index == 200 && report != null && report.getStatus().equals(APITestStatus.Running.name())) {
                report.setStatus(APITestStatus.Error.name());
                apiScenarioReportMapper.updateByPrimaryKey(report);
            }
            return (T) report;
        } catch (Exception ex) {
            LogUtil.error(ex.getMessage());
            MSException.throwException(ex.getMessage());
            return null;
        }
    }
}