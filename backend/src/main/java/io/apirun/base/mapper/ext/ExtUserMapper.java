package io.apirun.base.mapper.ext;

import io.apirun.base.domain.User;
import io.apirun.controller.request.UserRequest;
import io.apirun.notice.domain.UserDetail;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ExtUserMapper {

    List<User> getUserList(@Param("userRequest") UserRequest request);

    int updatePassword(User record);

    String getDefaultLanguage(String paramKey);

    List<User> searchUser(String condition);

    List<UserDetail> queryTypeByIds(List<String> userIds);

    @MapKey("id")
    Map<String, User> queryNameByIds(List<String> userIds);

    List<String> selectAllId();

    List<String> selectIdsByQuery(@Param("request") UserRequest request);

    void updateLastProjectIdIfNull(@Param("projectId") String projectId, @Param("userId") String userId);

    void updateLastWorkspaceIdIfNull(@Param("workspaceId") String workspaceId, @Param("userId") String userId);
}
