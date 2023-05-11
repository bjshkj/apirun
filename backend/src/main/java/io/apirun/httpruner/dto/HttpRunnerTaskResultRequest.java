package io.apirun.httpruner.dto;

import io.apirun.base.domain.HttprunnerTask;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class HttpRunnerTaskResultRequest extends HttprunnerTask {

    private String statusMsg;

}
