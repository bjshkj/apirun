package io.apirun.performance.dto;

import lombok.Getter;

@Getter
public enum MonitorTargetType {
    PRESSURE_SERVER("施压服务器"),
    SERVER("被压服务器"),
    MYSQL("Mysql"),
    REDIS("Redis"),
    MONGO("Mongo");

    private String label;

    MonitorTargetType(String alabel) {
        label = alabel;
    }
}