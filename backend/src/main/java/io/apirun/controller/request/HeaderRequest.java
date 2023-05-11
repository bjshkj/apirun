package io.apirun.controller.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HeaderRequest {
    private String userId;
    private String type;
}
