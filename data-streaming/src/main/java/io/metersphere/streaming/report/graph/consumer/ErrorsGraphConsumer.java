

package io.metersphere.streaming.report.graph.consumer;

import io.metersphere.streaming.report.graph.ErrorCountValueSelector;
import io.metersphere.streaming.report.graph.ErrorSeriesSelector;
import org.apache.jmeter.report.processor.TimeRateAggregatorFactory;
import org.apache.jmeter.report.processor.graph.AbstractGraphConsumer;
import org.apache.jmeter.report.processor.graph.AbstractOverTimeGraphConsumer;
import org.apache.jmeter.report.processor.graph.GroupInfo;
import org.apache.jmeter.report.processor.graph.TimeStampKeysSelector;

import java.util.Collections;
import java.util.Map;


public class ErrorsGraphConsumer extends AbstractOverTimeGraphConsumer {


    @Override
    protected TimeStampKeysSelector createTimeStampKeysSelector() {
        TimeStampKeysSelector keysSelector = new TimeStampKeysSelector();
        keysSelector.setSelectBeginTime(false);
        return keysSelector;
    }

    @Override
    protected Map<String, GroupInfo> createGroupInfos() {
        return Collections.singletonMap(
                AbstractGraphConsumer.DEFAULT_GROUP,
                new GroupInfo(
                        new TimeRateAggregatorFactory(),
                        new ErrorSeriesSelector(),
                        // We ignore Transaction Controller results
                        new ErrorCountValueSelector(true),
                        false,
                        false));
    }

    @Override
    public void initialize() {
        super.initialize();
        // Override the granularity of the aggregators factory
        ((TimeRateAggregatorFactory) getGroupInfos()
                .get(AbstractGraphConsumer.DEFAULT_GROUP)
                .getAggregatorFactory())
                .setGranularity(getGranularity());
    }


    @Override
    public void setGranularity(long granularity) {
        super.setGranularity(granularity);
    }
}
