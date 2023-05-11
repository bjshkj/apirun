package io.apirun.base.domain;

import java.io.Serializable;
import lombok.Data;

@Data
public class HttprunnerTask implements Serializable {
    private String id;

    private String taskName;

    private String projectId;

    private String gitVersion;

    private String debugMode;

    private String gitVerDesc;

    private String execCasePaths;

    private String dotEnvPath;

    private String hostsPath;

    private String triggerModel;

    private String createUser;

    private String taskStatus;

    private Long ctime;

    private Long utime;

    private String nodeId;

    private String image;

    private Long stime;

    private Long ftime;

    private Integer tcasesTotal;

    private Integer tcasesSuccess;

    private Integer tcasesFail;

    private Integer tstepsTotal;

    private Integer tstepsFailures;

    private Integer tstepsErrors;

    private Integer tstepsSkipped;

    private Integer tstepsExpectedFailures;

    private Integer tstepsUnexpectedSuccesses;

    private Integer tstepsSuccesses;

    private Double duration;

    private String testLogId;

    private String platform;

    private static final long serialVersionUID = 1L;
}