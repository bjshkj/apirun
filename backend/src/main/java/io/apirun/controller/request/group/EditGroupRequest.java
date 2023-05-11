package io.apirun.controller.request.group;

import io.apirun.base.domain.Group;
import io.apirun.dto.GroupPermission;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class EditGroupRequest extends Group {

    private int goPage;
    private int pageSize;
    private List<String> types = new ArrayList<>();
    private List<String> scopes = new ArrayList<>();

    /**
     * 是否是全局用户组
     */
    private Boolean global;

    private List<GroupPermission> permissions;
    private String userGroupId;

}
