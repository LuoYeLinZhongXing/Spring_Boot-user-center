package com.luoye.usercenter.service;

import com.luoye.usercenter.model.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;


    @Test
    public void testAddUser() {
        User user = new User();
        //生成user假数据
        user.setUsername("testUser");
        user.setUserAccount("testAccount");
        user.setAvatarUrl("https://example.com/avatar.jpg");
        user.setGender(1);
        user.setUserPassword("password123");
        user.setPhone("12345678901");
        user.setEmail("test@example.com");

        boolean result = userService.save(user);
        System.out.println("用户ID: " + user.getId());
        Assertions.assertTrue(result);
     }


    @Test
    void userRegister() {
        String userAccount = "test_" + System.currentTimeMillis(); // 使用时间戳确保唯一性
        String userPassword = "12345678";
        String checkPassword = "12345678";
        String planetCode = "1";
        Long result = userService.UserRegister(userAccount, userPassword, checkPassword, planetCode);
        Assertions.assertTrue(result > 0);
    }
    @Test
    void userLogin() {
    }

    @Test
    void getSafeUser() {
    }

    @Test
    void userLogout() {
    }

    @Test
    void testearchuserByTag() {
        ArrayList<String> list = new ArrayList<>();
        list.add("java");
        list.add("python");
        List<User> users = userService.searchuserByTag(list);
        for (User user : users) {
            System.out.println(user.toString());
        }
        Assertions.assertNotNull(users);
    }
}
