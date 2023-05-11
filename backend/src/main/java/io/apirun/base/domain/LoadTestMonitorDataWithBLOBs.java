package io.apirun.base.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class LoadTestMonitorDataWithBLOBs extends LoadTestMonitorData implements Serializable {
    private String monitorData;

    private String timeStamp;

    private static final long serialVersionUID = 1L;
}