package io.apirun.service;


import io.apirun.performance.request.TestPlanRequest;

import java.util.Set;

public interface QuotaService {

    void checkAPITestQuota();

    void checkLoadTestQuota(TestPlanRequest request, boolean checkPerformance);

    Set<String> getQuotaResourcePools();
}
