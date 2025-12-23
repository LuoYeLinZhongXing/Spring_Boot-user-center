package com.luoye.usercenter.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.luoye.usercenter.common.Result;
import com.luoye.usercenter.common.exception.BusinessException;
import com.luoye.usercenter.contant.UserConstant;
import com.luoye.usercenter.model.User;
import com.luoye.usercenter.model.request.UserLoginRequest;
import com.luoye.usercenter.model.request.UserRegisterRequest;
import com.luoye.usercenter.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;


    @PostMapping("/register")
    public Result<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest){
        //log.info("开始注册，参数为:{}", userRegisterRequest);
        if(userRegisterRequest == null){
            throw new BusinessException("参数错误");
        }
        Long userid = userService.UserRegister(userRegisterRequest.getUserAccount(), userRegisterRequest.getUserPassword(),
                                                userRegisterRequest.getCheckPassword(),userRegisterRequest.getPlanetCode());
        return Result.success(userid);
    }

    @PostMapping("/login")
    public Result<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest HttpServletRequest){
        //log.info("开始登录，参数为:{}", userLoginRequest);
        if(userLoginRequest == null){
            throw new BusinessException("参数错误");
        }
        User user = userService.userLogin(userLoginRequest.getUserAccount(), userLoginRequest.getUserPassword(), HttpServletRequest);
        return Result.success( user);
    }

    @GetMapping("/getCurrent")
    public Result<User> getCurrentUser(HttpServletRequest request){
        //log.info("开始查询当前用户");
        User attribute =(User) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        if(attribute == null) throw new BusinessException("未登录");
        //获取当前用户最新信息
        User byId = userService.getById(attribute.getId());
        //TODO 校验用户是否合法
        User safeUser = userService.getSafeUser(byId);
        return Result.success(safeUser);
    }

    @GetMapping("/search")
    public Result<List<User>> searchUsers(String username,HttpServletRequest request){
        //仅管理员可进行添加或删除
        if (isAdmin(request)) throw new BusinessException("无权限");
        //log.info("开始查询用户");
        if (StringUtils.isAnyBlank(username)) throw new BusinessException("参数错误");

        List<User> username1 = userService.list(new QueryWrapper<User>().like("username", username)).stream().map(user -> {
                    return userService.getSafeUser(user);
                }
        ).collect(Collectors.toList());
        return Result.success(username1);
    }

    @DeleteMapping("/delete")
    public Result<Boolean> deleteUser(Long id,HttpServletRequest request){
        if (isAdmin(request)) throw new BusinessException("无权限");
        if (id == null) throw new BusinessException("参数错误");
        boolean b = userService.removeById(id);
        return Result.success(b);
    }

    @PostMapping("/logout")
    public Result<Integer> userLogout(HttpServletRequest HttpServletRequest){
        //log.info("开始登录，参数为:{}", userLoginRequest);
        if(HttpServletRequest == null){
            throw new BusinessException("参数错误");
        }
        int i = userService.userLogout(HttpServletRequest);
        return Result.success(i);

    }
    private static boolean isAdmin(HttpServletRequest request) {
        //仅管理员可进行添加或删除
        User user =(User) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        return user == null || user.getUserRole() != UserConstant.ADMIN_ROLE;
    }
}
