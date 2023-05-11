package io.apirun.assemblyline.dto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class JmeterTaskResponseDTO extends AsseLineResponseDto{
    private int scenarioTotalNum; //场景总数
    private int scenarioSuccessNum; //成功的场景数量
    private int scenarioFailedNum; //失败的场景数量
    private int scenarioRequestTotalNum; //场景请求总数
    private int scenarioRequestSuccessNum; //成功的场景请求数量
    private int scenarioRequestFailedNum; //失败的场景请求数量
}