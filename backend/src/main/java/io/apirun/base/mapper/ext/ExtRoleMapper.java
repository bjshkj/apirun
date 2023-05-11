package io.apirun.base.mapper.ext;

import io.apirun.base.domain.Role;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtRoleMapper {

    List<Role> getRoleList(@Param("sign") String sign);
}
