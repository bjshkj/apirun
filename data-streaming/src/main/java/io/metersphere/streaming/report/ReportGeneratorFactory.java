package io.metersphere.streaming.report;

import io.metersphere.streaming.commons.utils.LogUtil;
import io.metersphere.streaming.report.impl.AbstractReport;
import org.reflections8.Reflections;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ReportGeneratorFactory {

    public static List<AbstractReport> getReportGenerators() {
        Reflections reflections = new Reflections(ReportGeneratorFactory.class);
        Set<Class<? extends AbstractReport>> subTypes = reflections.getSubTypesOf(AbstractReport.class);
        List<AbstractReport> reportGenerators = new ArrayList<>();
        subTypes.forEach(s -> {
            try {
                reportGenerators.add(s.newInstance());
            } catch (Exception e) {
                LogUtil.error(e);
            }
        });

        return reportGenerators;
    }
}

