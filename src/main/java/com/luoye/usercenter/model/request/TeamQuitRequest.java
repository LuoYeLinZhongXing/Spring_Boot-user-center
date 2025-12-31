package com.luoye.usercenter.model.request;

import lombok.Data;

/**
 * 退出队伍请求
 */
@Data
public class TeamQuitRequest {
    /**
     * 队伍id
     */
    private Long teamId;
}
