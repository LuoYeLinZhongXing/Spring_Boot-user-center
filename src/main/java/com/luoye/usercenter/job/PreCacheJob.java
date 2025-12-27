package com.luoye.usercenter.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.luoye.usercenter.common.exception.BusinessException;
import com.luoye.usercenter.mapper.UserMapper;
import com.luoye.usercenter.model.User;
import com.luoye.usercenter.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class PreCacheJob {

    @Autowired
    private UserService userService;
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    private List<Long> mainUserIdList= Arrays.asList(1L);

    @Scheduled(cron = "0 45 23 ? * ? ")
    public void doCacheRecommendUser() {
        RLock lock = redissonClient.getLock("user:timedTasks:Lock");
        try {
            if (lock.tryLock(0, 3000L, TimeUnit.MILLISECONDS)) {
                for (Long mainUserId : mainUserIdList) {
                    String key = String.format("user:recommend:%s", mainUserId);
                    ValueOperations valueOperations = redisTemplate.opsForValue();
                    Page<User> page = userService.page(new Page<>(1, 20), new QueryWrapper<>());
                    try {
                        valueOperations.set(key, page, 60, TimeUnit.SECONDS);
                    } catch (Exception e) {
                        log.error("redis set key error", e);
                    }
                }

            }
        } catch (InterruptedException e) {
            log.error("doCacheRecommendUser error", e);
        }finally {
            //释放锁
            if(lock.isHeldByCurrentThread()){
                lock.unlock();
            }
        }
    }
}
