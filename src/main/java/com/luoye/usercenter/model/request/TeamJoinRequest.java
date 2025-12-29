package com.luoye.usercenter.model.request;

import lombok.Data;

@Data
public class TeamJoinRequest {
    /**
     * 队伍id
     */
    private Long teamId;
    /**
     * 用户id
     */
    private String password;
}
