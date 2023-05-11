/**
 *
 */
package io.apirun.api.service.task;

import io.apirun.api.dto.automation.RunScenarioRequest;
import io.apirun.api.jmeter.JMeterService;
import io.apirun.commons.exception.MSException;
import io.apirun.commons.utils.LogUtil;
import org.apache.jorphan.collections.HashTree;

import java.util.concurrent.Callable;

public class ParallelScenarioExecTask<T> implements Callable<T> {
    private RunScenarioRequest request;
    private JMeterService jMeterService;
    private HashTree hashTree;
    private String id;

    public ParallelScenarioExecTask(JMeterService jMeterService, String id, HashTree hashTree, RunScenarioRequest request) {
        this.jMeterService = jMeterService;
        this.request = request;
        this.hashTree = hashTree;
        this.id = id;
    }

    @Override
    public T call() {
        try {
            jMeterService.runSerial(id, hashTree, request.getReportId(), request.getRunMode(), request.getConfig());
            return null;
        } catch (Exception ex) {
            LogUtil.error(ex.getMessage());
            MSException.throwException(ex.getMessage());
            return null;
        }
    }
}
