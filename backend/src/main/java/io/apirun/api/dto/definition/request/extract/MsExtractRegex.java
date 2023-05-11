package io.apirun.api.dto.definition.request.extract;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class MsExtractRegex extends MsExtractCommon {
    private String useHeaders;

    public MsExtractRegex() {
        setType(MsExtractType.REGEX);
    }
}
