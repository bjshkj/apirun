package io.apirun.assemblyline.dto;

import io.apirun.assemblyline.constants.AsseLineStatus;
import lombok.Data;

@Data
public class AsseLineResponseDto {
    private String message;
    private String url;
    private AsseLineStatus status;
}
