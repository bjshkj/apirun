package io.apirun.common.pojo;

import lombok.Data;

@Data
public class ResponseAssertionResult {

    private String name;

    private String message;

    private boolean pass;
}
