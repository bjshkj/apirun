package io.apirun.dto;

import io.apirun.base.domain.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class UserDTO extends User {

    private List<Role> roles = new ArrayList<>();

    private List<UserRole> userRoles = new ArrayList<>();

    private List<UserGroup> userGroups = new ArrayList<>();
    private List<Group> groups = new ArrayList<>();
    private List<GroupResourceDTO> groupPermissions = new ArrayList<>();

    private static final long serialVersionUID = 1L;

}
