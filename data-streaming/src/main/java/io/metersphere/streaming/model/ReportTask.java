package io.metersphere.streaming.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.concurrent.Future;

@Data
@AllArgsConstructor
public class ReportTask {
    private String reportId;
    private Future<?> task;
}
