package io.apirun.api.dto.scenario.environment;

import io.apirun.api.dto.scenario.DatabaseConfig;
import io.apirun.api.dto.scenario.HttpConfig;
import io.apirun.api.dto.scenario.TCPConfig;
import io.apirun.api.dto.ssl.KeyStoreConfig;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class EnvironmentConfig {
    private String apiEnvironmentid;
    private CommonConfig commonConfig;
    private HttpConfig httpConfig;
    private List<DatabaseConfig> databaseConfigs;
    private TCPConfig tcpConfig;
    private KeyStoreConfig sslConfig;

    public EnvironmentConfig() {
        this.commonConfig = new CommonConfig();
        this.httpConfig = new HttpConfig();
        this.databaseConfigs = new ArrayList<>();
        this.tcpConfig = new TCPConfig();
        this.sslConfig = new KeyStoreConfig();
    }
}
