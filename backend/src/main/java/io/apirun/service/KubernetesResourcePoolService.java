package io.apirun.service;

import io.apirun.dto.TestResourcePoolDTO;

public interface KubernetesResourcePoolService {
    boolean validate(TestResourcePoolDTO testResourcePool);
}
