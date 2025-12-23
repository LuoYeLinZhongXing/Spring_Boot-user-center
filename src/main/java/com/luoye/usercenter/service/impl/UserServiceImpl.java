package com.luoye.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luoye.usercenter.common.ErrorCode;
import com.luoye.usercenter.common.exception.BusinessException;
import com.luoye.usercenter.contant.UserConstant;
import com.luoye.usercenter.mapper.UserMapper;
import com.luoye.usercenter.model.User;
import com.luoye.usercenter.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
* @author Lenovo
* @description 针对表【user(用户表)】的数据库操作Service实现
* @createDate 2025-12-22 16:20:04
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {
    // 盐值
    private final String SALT = "luoye";




    /**
     * 用户注册
     * @param userAccount 用户账户
     * @param userPassword 用户密码
     * @param checkPassword 确认密码
     * @return 新用户ID，失败返回-1
     */
    @Override
    public Long UserRegister(String userAccount, String userPassword, String checkPassword,String planetCode) {
        // 1. 校验参数非空
        if(StringUtils.isAnyBlank(userAccount,userPassword,checkPassword,planetCode)){
            //TODO: 添加异常
            throw new BusinessException("参数为空");
        }
        // 2. 校验账户长度不小于4位
        if(userAccount.length() < 4){
            throw new BusinessException("用户账户长度不小于4位");
        }
        // 3. 校验密码长度不小于8位
        if(userPassword.length() < 8 || checkPassword.length() < 8 ){
            throw new BusinessException("用户密码长度不小于8位");
        }
        if(planetCode.length() > 5){
            throw new BusinessException("星球编号长度不能大于5");
        }
        // 4. 账户不能包含特殊字符
        String validPattern="[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：\"\"''。，、？\\s]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if(matcher.find()){
            throw new BusinessException("账户不能包含特殊字符");
        }
        // 5. 账户不能重复
        if(this.getOne(new QueryWrapper<User>().eq("user_account",userAccount)) != null){
            throw new BusinessException("账户已存在");
        }
        if(this.getOne(new QueryWrapper<User>().eq("planet_code",planetCode))!= null){
            throw new BusinessException("星球编号已存在");
        }
        // 6. 密码与确认密码必须相同
        if(!userPassword.equals(checkPassword)){
            throw new BusinessException("密码与确认密码必须相同");
        }

        // 7. 对密码进行MD5加密加盐处理
        userPassword = DigestUtils.md5DigestAsHex((SALT+userPassword).getBytes());

        // 8. 插入用户数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(userPassword);
        user.setUserStatus(0);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        user.setPlanetCode(planetCode);
        user.setIsDelete(0);
        boolean save = this.save(user);
        if(!save){
            throw new BusinessException("注册失败");
        }
        return user.getId();
    }

    /**
     * 用户登录
     * @param userAccount
     * @param userPassword
     * @param request
     * @return
     */
    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 校验参数非空
        if(StringUtils.isAnyBlank(userAccount,userPassword)){
            throw new BusinessException("参数为空");
        }
        // 2. 校验账户长度不小于4位
        if(userAccount.length() < 4){
            throw new BusinessException("用户账户长度不小于4位");
        }
        // 3. 校验密码长度不小于8位
        if(userPassword.length() < 8 ){
            throw new BusinessException("用户密码长度不小于8位");
        }
        // 4. 账户不能包含特殊字符
        String validPattern="[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：\"\"''。，、？\\s]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if(matcher.find()){
            throw new BusinessException("账户不能包含特殊字符");
        }

        // 7. 对密码进行MD5加密加盐处理
        userPassword = DigestUtils.md5DigestAsHex((SALT+userPassword).getBytes());

        User user = this.getOne(new QueryWrapper<User>().eq("user_account", userAccount).eq("user_password", userPassword));

        if(user == null){
            //log.info("用户不存在或者密码错误");
            throw new BusinessException("用户不存在或者密码错误");
        }

        // 8. 脱敏
        User user1 = getSafeUser(user);

        // 9. 登录成功，记录用户登录态
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE,user1);

        return user1;
    }

    @Override
    public User getSafeUser(User user){
        if(user == null) return null;
        User user1 = new User();
        user1.setId(user.getId());
        user1.setUsername(user.getUsername());
        user1.setUserAccount(user.getUserAccount());
        user1.setAvatarUrl(user.getAvatarUrl());
        user1.setGender(user.getGender());
        user1.setPhone(user.getPhone());
        user1.setEmail(user.getEmail());
        user1.setUserRole(user.getUserRole());
        user1.setUserStatus(user.getUserStatus());
        user1.setUpdateTime(user.getUpdateTime());
        user1.setPlanetCode(user.getPlanetCode());
        return user1;
    }

    @Override
    public int userLogout(HttpServletRequest request) {
        //移除登录态
        request.getSession().removeAttribute(UserConstant.USER_LOGIN_STATE);
        return 1;
    }
}




