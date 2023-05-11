package io.metersphere.streaming.report.graph;

import org.apache.commons.lang3.StringUtils;
import org.apache.jmeter.report.core.Sample;
import org.apache.jmeter.report.processor.graph.CountValueSelector;
import org.apache.jmeter.report.utils.MetricUtils;
import org.apache.jmeter.samplers.SampleSaveConfiguration;
import org.apache.jmeter.util.JMeterUtils;

public class ErrorCountValueSelector extends CountValueSelector {
    private static final Double ONE = 1.0d;
    private static final Double ZERO = 0.0d;
    private static final boolean ASSERTION_RESULTS_FAILURE_MESSAGE = JMeterUtils
            .getPropDefault(
                    SampleSaveConfiguration.ASSERTION_RESULTS_FAILURE_MESSAGE_PROP,
                    true);

    public ErrorCountValueSelector(boolean ignoreTransactionController) {
        super(ignoreTransactionController);
    }


    @Override
    public Double select(String series, Sample sample) {
        if (sample.getSuccess()) {
            return ZERO;
        }
        if (MetricUtils.isSuccessCode(sample.getResponseCode())) {
            if (!StringUtils.isEmpty(sample.getFailureMessage())) {
                if (ASSERTION_RESULTS_FAILURE_MESSAGE) {
                    String msg = sample.getFailureMessage();
                    if (!StringUtils.isEmpty(msg)) {
                        return ONE;
                    }
                }
            }
            return ZERO;
        }
        if (isIgnoreTransactionController()) {
            if (!sample.isController()) {
                return ONE;
            }
        } else {
            if (!sample.isEmptyController()) {
                return ONE;
            }
        }
        return ZERO;
    }

}
