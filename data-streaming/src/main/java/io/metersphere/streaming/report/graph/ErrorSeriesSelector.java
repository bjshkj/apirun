package io.metersphere.streaming.report.graph;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.jmeter.report.core.Sample;
import org.apache.jmeter.report.processor.graph.AbstractSeriesSelector;
import org.apache.jmeter.report.utils.MetricUtils;
import org.apache.jmeter.samplers.SampleSaveConfiguration;
import org.apache.jmeter.util.JMeterUtils;

import java.util.Collections;

public class ErrorSeriesSelector extends AbstractSeriesSelector {
    private static final boolean ASSERTION_RESULTS_FAILURE_MESSAGE = JMeterUtils
            .getPropDefault(
                    SampleSaveConfiguration.ASSERTION_RESULTS_FAILURE_MESSAGE_PROP,
                    true);

    @Override
    public Iterable<String> select(Sample sample) {
        return Collections.singletonList(getErrorKey(sample));
    }

    static String getErrorKey(Sample sample) {
        String responseCode = sample.getResponseCode();
        String responseMessage = sample.getResponseMessage();
        String key = responseCode + (!StringUtils.isEmpty(responseMessage) ?
                "/" + StringEscapeUtils.escapeJson(StringEscapeUtils.escapeHtml4(responseMessage)) : "");
        if (MetricUtils.isSuccessCode(responseCode) ||
                (StringUtils.isEmpty(responseCode) &&
                        !StringUtils.isEmpty(sample.getFailureMessage()))) {
            if (ASSERTION_RESULTS_FAILURE_MESSAGE) {
                String msg = sample.getFailureMessage();
                if (!StringUtils.isEmpty(msg)) {
                    key = StringEscapeUtils.escapeJson(StringEscapeUtils.escapeHtml4(msg));
                }
            }
        }
        return StringEscapeUtils.unescapeJava(key);
    }
}
