package io.metersphere.streaming.report.graph.consumer;

import org.apache.commons.lang3.StringUtils;
import org.apache.jmeter.report.core.Sample;
import org.apache.jmeter.report.processor.MeanAggregatorFactory;
import org.apache.jmeter.report.processor.graph.AbstractGraphConsumer;
import org.apache.jmeter.report.processor.graph.AbstractSeriesSelector;
import org.apache.jmeter.report.processor.graph.GraphValueSelector;
import org.apache.jmeter.report.processor.graph.GroupInfo;
import org.apache.jmeter.report.processor.graph.impl.ActiveThreadsGraphConsumer;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class DistributedActiveThreadsGraphConsumer extends ActiveThreadsGraphConsumer {
    private final Map<String, Double> sampleCache;

    /*
    线程组-0-ThreadStarter 1-7
    线程组-1-ThreadStarter 1-7
    线程组-ThreadStarter 1-6
     */
    public DistributedActiveThreadsGraphConsumer() {
        this.sampleCache = new ConcurrentHashMap<>();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.jmeter.report.csv.processor.impl.AbstractGraphConsumer#
     * createGroupInfos()
     */
    @Override
    protected Map<String, GroupInfo> createGroupInfos() {
        AbstractSeriesSelector seriesSelector = new AbstractSeriesSelector() {

            @Override
            public Iterable<String> select(Sample sample) {
                if (sample.isEmptyController()) {
                    return Collections.emptyList();
                }
                String threadName = getThreadName(sample);
                return Collections.singletonList(fetchKey(threadName));
            }
        };

        GraphValueSelector graphValueSelector = (series, sample) -> {
            if (!sample.isEmptyController()) {
                String threadName = getThreadName(sample);
                sampleCache.put(threadName, (double) sample.getGroupThreads());
                Map<String, List<String>> collect = sampleCache.keySet().stream().collect(Collectors.groupingBy(this::fetchKey));
                return collect.get(fetchKey(threadName)).stream().mapToDouble(sampleCache::get).sum();
            } else {
                return null;
            }
        };

        return Collections.singletonMap(
                AbstractGraphConsumer.DEFAULT_GROUP,
                new GroupInfo(new MeanAggregatorFactory(), seriesSelector, graphValueSelector, false, false));
    }

    private String fetchKey(String threadName) {
        int index = threadName.lastIndexOf("-");
        if (index >= 0) {
            threadName = threadName.substring(0, index);
        }
        return threadName;
    }

    private String getThreadName(Sample sample) {
        String threadName = sample.getThreadName();
        int index = threadName.lastIndexOf(' ');
        if (index >= 0) {
            threadName = threadName.substring(0, index);
        }
        // 处理后缀
        threadName = StringUtils.removeEnd(threadName, "-ThreadStarter");

        return threadName;
    }

}
