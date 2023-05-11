package io.metersphere.streaming.report;

public interface Report {
    String getReportKey();

    void execute();
}
