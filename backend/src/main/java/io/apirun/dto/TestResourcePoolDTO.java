package io.apirun.dto;

import io.apirun.base.domain.TestResource;
import io.apirun.base.domain.TestResourcePool;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TestResourcePoolDTO extends TestResourcePool {

    private List<TestResource> resources;

}
