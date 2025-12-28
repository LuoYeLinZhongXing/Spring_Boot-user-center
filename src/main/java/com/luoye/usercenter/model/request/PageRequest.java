package com.luoye.usercenter.model.request;

import lombok.Data;

/**
 * 分页请求
 */
@Data
public class PageRequest {
    /**
     * 当前页号
     */
    private Integer pageNum =0;
    /**
     * 页面大小
     */
    private Integer pageSize =10;

}
