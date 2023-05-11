package io.apirun.httpruner.dto;

import io.apirun.assemblyline.dto.AsseLineResponseDto;
import lombok.Data;

@Data
public class TestCaseStatInfo extends AsseLineResponseDto {
    private int total = 0;
    private int failures = 0;
    private int success = 0;
}
