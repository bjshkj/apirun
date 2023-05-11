package io.apirun.base.domain;

import java.io.Serializable;
import lombok.Data;

@Data
public class UserHeader implements Serializable {
    private String id;

    private String userId;

    private String props;

    private String type;

    private static final long serialVersionUID = 1L;
}