package io.metersphere.streaming.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Log {
    private String reportId;
    private String resourceId;
    private int resourceIndex;
    private String content;
}
