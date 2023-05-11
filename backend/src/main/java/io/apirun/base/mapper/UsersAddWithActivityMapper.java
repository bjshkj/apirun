package io.apirun.base.mapper;

import io.apirun.dto.UserActivityDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UsersAddWithActivityMapper {
    List<UserActivityDTO> getUserAddWithActivityMapper(@Param("begin_time") String begin_time,@Param("end_time") String end_time,@Param("format") String format);

}
