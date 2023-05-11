package io.apirun.dto;

import io.apirun.base.domain.Group;
import io.apirun.base.domain.UserGroupPermission;
import lombok.Data;

import java.util.List;

@Data
public class GroupResourceDTO {
    private GroupResource resource;
    private List<GroupPermission> permissions;
    private String type;

    private Group group;
    private List<UserGroupPermission> userGroupPermissions;
}
