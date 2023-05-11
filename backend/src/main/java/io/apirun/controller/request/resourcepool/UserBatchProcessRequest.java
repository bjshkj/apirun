package io.apirun.controller.request.resourcepool;

import io.apirun.controller.request.UserRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author song.tianyang
 * @Date 2021/3/3 5:21 下午
 * @Description
 */
@Getter
@Setter
public class UserBatchProcessRequest {
    List<String> ids;
    String projectId;
    String batchType;
    List<String> batchProcessValue;
    String organizationId;
    UserRequest condition;
}

