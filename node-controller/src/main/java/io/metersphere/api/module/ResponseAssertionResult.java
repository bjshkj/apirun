package io.metersphere.api.module;

import lombok.Data;

/**
 *响应断言结果
 */
@Data
public class ResponseAssertionResult {

    private String name;

    private String message;

    private boolean pass;
}
