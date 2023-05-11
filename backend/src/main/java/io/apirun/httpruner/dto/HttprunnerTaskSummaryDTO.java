package io.apirun.httpruner.dto;

import lombok.Data;

@Data
public class HttprunnerTaskSummaryDTO {

    private String taskName;

    private String gitVersion;

    private String gitVerDesc;

    private String createUser;

    private String taskStatus;

    private Long ctime;

    private Long utime;

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

    private String httprunnerVersion;

    private String pythonVersion;

    private String platform;

}
