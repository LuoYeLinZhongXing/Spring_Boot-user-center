package com.luoye.usercenter.service;

import com.luoye.usercenter.config.RedissonConfig;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.redisson.api.RList;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SpringBootTest
public class RedissonTest {

    @Autowired
    private RedissonClient redissonClient;

    @Test
    void test(){
        //list

        //jvm
        ArrayList<String> objects = new ArrayList<>();

        //redis
        RList<String> list = redissonClient.getList("test-list");

        //map

        //jvm
        HashMap<String, Object> map = new HashMap<>();
        //redis
        RMap<Object, Object> map1 = redissonClient.getMap("test-map");

        //set


    }
}
