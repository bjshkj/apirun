package io.apirun.api.dto.scenario.environment;

import io.apirun.api.dto.scenario.KeyValue;
import lombok.Data;

import java.util.List;

@Data
public class CommonConfig {
    private List<KeyValue> variables;
    private boolean enableHost;
    private List<Host> hosts;
}
