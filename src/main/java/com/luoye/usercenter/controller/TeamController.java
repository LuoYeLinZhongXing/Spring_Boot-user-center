package com.luoye.usercenter.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.luoye.usercenter.common.Result;
import com.luoye.usercenter.common.exception.BusinessException;
import com.luoye.usercenter.model.domain.Team;
import com.luoye.usercenter.model.domain.User;
import com.luoye.usercenter.model.domain.UserTeam;
import com.luoye.usercenter.model.dto.TeamQuery;
import com.luoye.usercenter.model.request.AddTeamRequest;
import com.luoye.usercenter.model.request.TeamJoinRequest;
import com.luoye.usercenter.model.request.TeamQuitRequest;
import com.luoye.usercenter.model.request.TeamUpdateRequest;
import com.luoye.usercenter.model.vo.TeamUserVo;
import com.luoye.usercenter.service.TeamService;
import com.luoye.usercenter.service.UserService;
import com.luoye.usercenter.service.UserTeamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/team")
@CrossOrigin(origins = {"http://localhost:3000"})
@Slf4j
public class TeamController {


    @Autowired
    private TeamService teamService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserTeamService userTeamService;

    @PostMapping("/add")
    public Result<Long> addTeam(@RequestBody AddTeamRequest addTeamRequest, HttpServletRequest request){
        if(addTeamRequest == null) throw new BusinessException("查询数据为空");
        User loginUser = userService.getLoginUser(request);
        Team team = new Team();
        BeanUtil.copyProperties(addTeamRequest,team);
        Long teamId = teamService.addTeam(team, loginUser);
        return Result.success(teamId);
    }



    @PostMapping("/update")
    public Result<Boolean> updateTeam(@RequestBody TeamUpdateRequest teamUpdateRequest, HttpServletRequest request){
        if(teamUpdateRequest==null) throw new BusinessException("参数为空");
        User loginUser = userService.getLoginUser(request);
        boolean b = teamService.updateTeam(teamUpdateRequest,loginUser);
        if(!b) throw new BusinessException("更新失败");
        return Result.success(b);
    }

    @GetMapping("/get")
    public Result<Team> getTeamById(Long id){
        if(id==null) throw new BusinessException("参数为空");
        Team byId = teamService.getById(id);
        if(byId==null) throw new BusinessException("数据不存在");
        return Result.success(byId);
    }

    @GetMapping("/list")
    public Result<List<TeamUserVo>> getTeamList(@RequestBody TeamQuery teamQuery,HttpServletRequest request){
        if(teamQuery==null) throw new BusinessException("参数为空");
        User loginUser = userService.getLoginUser(request);
        boolean isAdmin = userService.isAdmin(loginUser);
        List<TeamUserVo> list = teamService.TeamUserlist(teamQuery,isAdmin);
        if(list ==null) throw new BusinessException("数据不存在");
        return Result.success(list);
    }


    /**
     * 加入队伍
     * @param teamJoinRequest
     * @param request
     * @return
     */
    @PostMapping("/join")
    public Result<Boolean> joinTeam(@RequestBody TeamJoinRequest teamJoinRequest, HttpServletRequest request){
        if(teamJoinRequest==null) throw new BusinessException("参数为空");
        User loginUser = userService.getLoginUser(request);
        return teamService.joinTeam(teamJoinRequest,loginUser);
    }

    /**
     * 退出队伍
     * @param teamQuitRequest
     * @param request
     * @return
     */
    @PostMapping("/quit")
    public Result<Boolean> quitTeam(@RequestBody TeamQuitRequest teamQuitRequest, HttpServletRequest request){
        if(teamQuitRequest==null) throw new BusinessException("参数为空");
        User loginUser = userService.getLoginUser(request);
        boolean b = teamService.quitTeam(teamQuitRequest, loginUser);
        return Result.success(b);
    }

    /**
     * 删除队伍
     * @param id
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public Result<Boolean> deleteTeam(@RequestBody Long id,HttpServletRequest request){
        if(id== null) throw new BusinessException("参数为空");
        User loginUser = userService.getLoginUser(request);
        boolean b = teamService.deleteTeamById(id, loginUser);
        if(!b) throw new BusinessException("删除失败");
        return Result.success(b);
    }

    /**
     * 获取当前用户创建的队伍
     * @param request
     * @return
     */
    @GetMapping("/getMyTeam")
    public Result<List<TeamUserVo>> getMyTeam(@RequestBody TeamQuery teamQuery,HttpServletRequest request){
        if(teamQuery==null) throw new BusinessException("参数为空");
        User loginUser = userService.getLoginUser(request);
        boolean isAdmin = userService.isAdmin(loginUser);
        teamQuery.setUserId(loginUser.getId());
        List<TeamUserVo> list = teamService.TeamUserlist(teamQuery,true);
        if(list ==null) throw new BusinessException("数据不存在");
        return Result.success(list);
    }

    @GetMapping("/getMyJoinTeam")
    public Result<List<TeamUserVo>> getMyJoinTeam(@RequestBody TeamQuery teamQuery,HttpServletRequest request){
        if(teamQuery==null) throw new BusinessException("参数为空");
        User loginUser = userService.getLoginUser(request);
        teamQuery.setUserId(loginUser.getId());
        List<UserTeam> userTeamList = userTeamService.list(new QueryWrapper<UserTeam>().eq("userId", loginUser.getId()));
        Map<Long, List<UserTeam>> collect = userTeamList.stream()
                .collect(Collectors.groupingBy(UserTeam::getTeamId));
        List<Long> idlist = new ArrayList<>(collect.keySet());
        teamQuery.setIds(idlist);
        List<TeamUserVo> list = teamService.TeamUserlist(teamQuery,true);
        return Result.success(list);

    }

}
