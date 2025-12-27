package com.luoye.usercenter.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000"},maxAge = 3600)
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

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
        User safeUser = userService.getSafeUser(byId);
        if(safeUser.getPlanetCode()==null) throw new BusinessException("非法用户");
        return Result.success(safeUser);
    }

    @GetMapping("/search")
    public Result<List<User>> searchUsers(String username,HttpServletRequest request){
        //仅管理员可进行添加或删除
        if (userService.isAdmin(request)) throw new BusinessException("无权限");
        //log.info("开始查询用户");
        if (StringUtils.isAnyBlank(username)) throw new BusinessException("参数错误");

        List<User> username1 = userService.list(new QueryWrapper<User>().like("username", username)).stream().map(user -> {
                    return userService.getSafeUser(user);
                }
        ).collect(Collectors.toList());
        return Result.success(username1);
    }

    // todo 推荐多个，未实现
    @GetMapping("/recommend")
    public Result<Page<User>> recommendUsers(long pageSize, long pageNum, HttpServletRequest request) {
        if(pageSize <= 0){pageSize = 10;}
        if(pageNum <= 0){pageNum = 1;}

        String key = String.format("user:recommend:%s", userService.getLoginUser(request).getId());
        ValueOperations valueOperations = redisTemplate.opsForValue();
        Page<User> page =(Page<User>)valueOperations.get(key);
        if(page != null) return Result.success(page);

        page = userService.page(new Page<>((pageNum - 1)*pageSize, pageSize), new QueryWrapper<>());
        try {
            valueOperations.set(key, page,60, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("redis set key error", e);
        }
        return Result.success(page);
    }

    @PostMapping("/update")
    public Result<Integer> updateUser(@RequestBody User user,HttpServletRequest request){

        // 判断前端传来的数据中除了id字段是否还包含其他字段
        if (user.getId() == null) {
            throw new BusinessException("id不能为空");
        }
        // 检查user对象是否只有id字段有值，其他字段都为null
        if (StringUtils.isBlank(user.getUsername()) && StringUtils.isBlank(user.getUserAccount())
                && StringUtils.isBlank(user.getUserProfile()) && StringUtils.isBlank(user.getAvatarUrl())
                && StringUtils.isBlank(user.getPhone()) && StringUtils.isBlank(user.getEmail())
                && StringUtils.isBlank(user.getPlanetCode()) && StringUtils.isBlank(user.getTags())
                && user.getGender() == null && user.getUserStatus() == null
                && user.getUserRole() == null && user.getIsDelete() == null) {
            throw new BusinessException("不能只更新id字段");
        }
        if(user == null){throw new BusinessException("参数为空");}
        User loginUser = userService.getLoginUser(request);
        Integer update = userService.Updateuser(user,loginUser);
        return Result.success(update);
    }

    @GetMapping("/search/tags")
    public Result<List<User>> searchUsersByTags(@RequestParam(required = false) List<String> tagNameList){
        //log.info("开始查询用户");
        if (tagNameList == null) throw new BusinessException("参数错误");
        List<User> list = userService.searchuserByTag(tagNameList);
        return Result.success(list);
    }
    @DeleteMapping("/delete")
    public Result<Boolean> deleteUser(Long id,HttpServletRequest request){
        if (userService.isAdmin(request)) throw new BusinessException("无权限");
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

}
