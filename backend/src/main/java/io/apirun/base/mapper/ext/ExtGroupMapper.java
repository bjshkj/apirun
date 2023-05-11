package io.apirun.base.mapper.ext;

import io.apirun.controller.request.group.EditGroupRequest;
import io.apirun.dto.GroupDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtGroupMapper {

    List<GroupDTO> getGroupList(@Param("request") EditGroupRequest request);


}
