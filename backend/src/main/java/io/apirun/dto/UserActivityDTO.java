package io.apirun.dto;

import lombok.Data;

@Data
public class UserActivityDTO {
    private int activityUserNum; // 用户活跃数量
    private int newUserNum;   // 新增用户数量
    private String activityTime;  //活动时间
}
