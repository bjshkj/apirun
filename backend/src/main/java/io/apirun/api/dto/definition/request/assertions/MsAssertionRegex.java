package io.apirun.api.dto.definition.request.assertions;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

@EqualsAndHashCode(callSuper = true)
@Data
public class MsAssertionRegex extends MsAssertionType {
    private String subject;
    private String expression;
    private String description;
    private boolean assumeSuccess;

    public MsAssertionRegex() {
        setType(MsAssertionType.REGEX);
    }

    public boolean isValid() {
        return StringUtils.isNotBlank(subject) && StringUtils.isNotBlank(expression);
    }
}
