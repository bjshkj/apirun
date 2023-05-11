package io.apirun.base.mapper.ext;

import io.apirun.dto.OrganizationMemberDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtOrganizationMapper {

    int checkSourceRole(@Param("sourceId") String sourceId,@Param("userId") String userId,@Param("groupId") String groupId);

    List<OrganizationMemberDTO> findIdAndNameByOrganizationId(@Param("organizationId")String organizationID);
}
