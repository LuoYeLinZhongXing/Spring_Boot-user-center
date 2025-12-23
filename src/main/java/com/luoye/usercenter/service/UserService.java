package com.luoye.usercenter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.luoye.usercenter.model.User;

import javax.servlet.http.HttpServletRequest;

/**
* @author Lenovo
* @description 针对表【user(用户表)】的数据库操作Service
* @createDate 2025-12-22 16:20:04
*/
public interface UserService extends IService<User> {


    Long UserRegister(String userAccount, String userPassword, String checkPassword,String planetCode);

    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 获取脱敏用户信息
     * @param user
     * @return
     */
    User getSafeUser(User user);

    /**
     * 用户注销
     * @param request
     * @return
     */
    int userLogout(HttpServletRequest request);
}
