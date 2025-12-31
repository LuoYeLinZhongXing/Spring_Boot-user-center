package com.luoye.usercenter.service.impl;

import cn.hutool.core.lang.Pair;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.reflect.TypeToken;
import com.luoye.usercenter.common.exception.BusinessException;
import com.luoye.usercenter.contant.UserConstant;
import com.luoye.usercenter.mapper.UserMapper;
import com.luoye.usercenter.model.domain.User;
import com.luoye.usercenter.service.UserService;
import com.luoye.usercenter.utils.AlgorithmUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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



    @Autowired
    private UserMapper userMapper;


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
        user1.setUserProfile(user.getUserProfile());
        user1.setAvatarUrl(user.getAvatarUrl());
        user1.setGender(user.getGender());
        user1.setPhone(user.getPhone());
        user1.setEmail(user.getEmail());
        user1.setUserRole(user.getUserRole());
        user1.setTags(user.getTags());
        user1.setUserStatus(user.getUserStatus());
        user1.setUpdateTime(user.getUpdateTime());
        user1.setPlanetCode(user.getPlanetCode());
        return user1;
    }

    /**
     * 用户注销
     * @param request
     * @return
     */
    @Override
    public int userLogout(HttpServletRequest request) {
        //移除登录态
        request.getSession().removeAttribute(UserConstant.USER_LOGIN_STATE);
        return 1;
    }

    /**
     * 根据标签搜索用户(sql查询)
     * @param tagNameList
     * @return
     */
    @Deprecated
    private List<User> searchuserByTagBySql(List<String> tagNameList){
        if(tagNameList ==null || tagNameList.size() == 0){
            throw new BusinessException("参数为空");
        }

        //在数据库中查询

        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        for (String tag : tagNameList) {

            userQueryWrapper= userQueryWrapper.like("tags", tag);
        }

        List<User> list = this.list(userQueryWrapper);

        List<User> collect = list.stream().map(user -> {
            return getSafeUser(user);
        }).collect(Collectors.toList());

//        //在业务代码中搜索
//        ArrayList<User> collect = new ArrayList<>();
//        List<User> list = this.list(new QueryWrapper<User>());
//        for (User user : list) {
//            String tags = user.getTags();
//            if (tags == null || tags.isEmpty()) {
//                continue; // 跳过没有标签的用户
//            }
//            JSONArray objects = JSONUtil.parseArray(tags);
//            if (objects == null) {
//                continue; // 跳过解析失败的标签
//            }
//            for (Object object : objects) {
//                if(tagNameList.contains(object)){
//                    collect.add(user);
//                    break;
//                }
//            }
//        }


        return collect;
    }

    /**
     * 根据标签搜索用户(内存过滤)
     * @param tagNameList
     * @return
     */
    @Override
    public List<User> searchuserByTag(List<String> tagNameList){
        if(tagNameList ==null || tagNameList.size() == 0){
            throw new BusinessException("参数为空");
        }

        //在业务代码中搜索
        ArrayList<User> collect = new ArrayList<>();
        List<User> list = this.list(new QueryWrapper<User>());
        for (User user : list) {
            String tags = user.getTags();
            if (tags == null || tags.isEmpty()) {
                continue; // 跳过没有标签的用户
            }
            JSONArray objects = JSONUtil.parseArray(tags);
            if (objects == null) {
                continue; // 跳过解析失败的标签
            }
            for (Object object : objects) {
                if(tagNameList.contains(object)){
                    user = getSafeUser(user);
                    collect.add(user);
                    break;
                }
            }
        }


        return collect;
    }

    @Override
    public List<User> matchUsers(long num, User loginUser) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "tags");
        queryWrapper.isNotNull("tags");
        List<User> userList = this.list(queryWrapper);
        String tags = loginUser.getTags();
        Gson gson = new Gson();
        List<String> tagList = gson.fromJson(tags, new TypeToken<List<String>>() {
        }.getType());
        // 用户列表的下标 => 相似度
        List<Pair<User, Long>> list = new ArrayList<>();
        // 依次计算所有用户和当前用户的相似度
        for (int i = 0; i < userList.size(); i++) {
            User user = userList.get(i);
            String userTags = user.getTags();
            // 无标签或者为当前用户自己
            if (StringUtils.isBlank(userTags) || user.getId() == loginUser.getId()) {
                continue;
            }
            List<String> userTagList = gson.fromJson(userTags, new TypeToken<List<String>>() {
            }.getType());
            // 计算分数
            long distance = AlgorithmUtils.minDistance(tagList, userTagList);
            list.add(new Pair<>(user, distance));
        }
        // 按编辑距离由小到大排序
        List<Pair<User, Long>> topUserPairList = list.stream()
                .sorted((a, b) -> (int) (a.getValue() - b.getValue()))
                .limit(num)
                .collect(Collectors.toList());
        // 原本顺序的 userId 列表
        List<Long> userIdList = topUserPairList.stream().map(pair -> pair.getKey().getId()).collect(Collectors.toList());
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.in("id", userIdList);
        // 1, 3, 2
        // User1、User2、User3
        // 1 => User1, 2 => User2, 3 => User3
        Map<Long, List<User>> userIdUserListMap = this.list(userQueryWrapper)
                .stream()
                .map(user -> getSafeUser(user))
                .collect(Collectors.groupingBy(User::getId));
        List<User> finalUserList = new ArrayList<>();
        for (Long userId : userIdList) {
            finalUserList.add(userIdUserListMap.get(userId).get(0));
        }
        return finalUserList;
    }
    @Override
    public int Updateuser(User user,User loginUser) {

        Long userid = user.getId();
        if(userid == null) throw new BusinessException("用户id为空");
        // 检查用户是否存在
        User oldUser = userMapper.selectById(userid);
        if (oldUser == null) {
            throw new BusinessException("用户不存在");
        }

        // 管理员可以更新任何用户
        if (isAdmin(loginUser)) {
            int result = userMapper.updateById(user);
            if (result <= 0) {
                throw new BusinessException("更新失败");
            }
            return result;
        }

        // 普通用户只能更新自己的信息
        if (!userid.equals(loginUser.getId())) {
            throw new BusinessException("无权限");
        }

        int result = userMapper.updateById(user);
        if (result <= 0) {
            throw new BusinessException("更新失败");
        }
        return result;
    }

    @Override
    public User getLoginUser(HttpServletRequest request){
        if(request == null) throw new BusinessException("请求为空");
        User loginUser = (User) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        if(loginUser == null) throw new BusinessException("未登录");
        return loginUser;
    }

    @Override
    public boolean isAdmin(HttpServletRequest request) {
        //仅管理员可进行添加或删除
        User user =(User) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        return user == null || user.getUserRole() != UserConstant.ADMIN_ROLE;
    }

    @Override
    public boolean isAdmin(User loginUser) {
        //仅管理员可进行添加或删除
        return loginUser == null || loginUser.getUserRole() != UserConstant.ADMIN_ROLE;
    }
}




