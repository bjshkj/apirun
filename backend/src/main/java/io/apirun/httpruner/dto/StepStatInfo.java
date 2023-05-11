package io.apirun.httpruner.dto;

import lombok.Data;

@Data
public class StepStatInfo {

    private int total = 0;
    private int failures = 0;
    private int errors = 0;
    private int skipped = 0;
    private int expectedFailures = 0;
    private int unexpectedSuccesses = 0;
    private int successes = 0;
}
