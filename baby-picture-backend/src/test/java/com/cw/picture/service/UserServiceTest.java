package com.cw.picture.service;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTest {

    @Resource
    private UserService userService;

    @Test
    void encryptPassword() {
        String password = "123456";
        String encryptedPassword = userService.encryptPassword(password);
        Assertions.assertEquals("e10adc3949ba59abbe56e057f20f883e", encryptedPassword);
    }
}