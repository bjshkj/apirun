package io.apirun.performance.engine;

public interface Engine {
    Long getStartTime();

    String getReportId();

    void start();

    void stop();
}
