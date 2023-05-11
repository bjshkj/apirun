package io.apirun.dto;

import io.apirun.base.domain.Group;
import io.apirun.base.domain.UserGroup;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class UserGroupPermissionDTO {
    List<GroupResourceDTO> list = new ArrayList<>();
    List<Group> groups = new ArrayList<>();
    List<UserGroup> userGroups = new ArrayList<>();
}
