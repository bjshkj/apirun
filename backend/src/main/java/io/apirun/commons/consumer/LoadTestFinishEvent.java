package io.apirun.commons.consumer;

import io.apirun.base.domain.LoadTestReport;
import org.springframework.scheduling.annotation.Async;

public interface LoadTestFinishEvent {
    @Async
    void execute(LoadTestReport loadTestReport);
}
