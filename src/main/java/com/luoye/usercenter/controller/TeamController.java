package com.luoye.usercenter.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.luoye.usercenter.common.Result;
import com.luoye.usercenter.common.exception.BusinessException;
import com.luoye.usercenter.model.domain.Team;
import com.luoye.usercenter.model.domain.User;
import com.luoye.usercenter.model.dto.TeamQuery;
import com.luoye.usercenter.model.request.AddTeamRequest;
import com.luoye.usercenter.service.TeamService;
import com.luoye.usercenter.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/team")
@CrossOrigin(origins = {"http://localhost:3000"})
@Slf4j
public class TeamController {


    @Autowired
    private TeamService teamService;
    @Autowired
    private UserService userService;

    @PostMapping("/add")
    public Result<Long> addTeam(@RequestBody AddTeamRequest addTeamRequest, HttpServletRequest request){
        if(addTeamRequest == null) throw new BusinessException("查询数据为空");
        User loginUser = userService.getLoginUser(request);
        Team team = new Team();
        BeanUtil.copyProperties(addTeamRequest,team);
        Long teamId = teamService.addTeam(team, loginUser);
        return Result.success(teamId);
    }

    @PostMapping("/delete")
    public Result<Boolean> deleteTeam(Long id){
        if(id== null) throw new BusinessException("参数为空");

        boolean b = teamService.removeById(id);
        if(!b) throw new BusinessException("删除失败");
        return Result.success(b);
    }

    @PostMapping("/update")
    public Result<Boolean> updateTeam(@RequestBody Team team){
        if(team==null) throw new BusinessException("参数为空");
        boolean b = teamService.updateById(team);
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

    @GetMapping("/page")
    public Result<Page<Team>> getTeamPage(@RequestBody TeamQuery teamQuery){
        if(teamQuery==null) throw new BusinessException("参数为空");
        Team team = new Team();
        BeanUtil.copyProperties(teamQuery,team);
        Page<Team> tPage = new Page<>(teamQuery.getPageNum(), teamQuery.getPageSize());
        Page<Team> page = teamService.page(tPage, new QueryWrapper<>(team));
        if(page==null) throw new BusinessException("数据不存在");
        return Result.success(page);
    }
}
