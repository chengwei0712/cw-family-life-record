package com.cw.picture.controller;

import com.cw.picture.entity.UserInfo;
import com.cw.picture.request.LoginRequest;
import com.cw.picture.response.CommonResponse;
import com.cw.picture.response.LoginResponse;
import com.cw.picture.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * 登录控制器
 *
 * @author chengwei
 * @since 2025-12-30
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class LoginController {

    @Resource
    private UserService userService;

    @PostMapping("/login")
    public CommonResponse<?> login(@RequestBody LoginRequest loginRequest) {
        UserInfo user = userService.findByUsername(loginRequest.getUsername());
        
        if (user != null && userService.validatePassword(user, loginRequest.getPassword())) {
            // 密码验证成功
            log.info("用户:{}登录成功", loginRequest.getUsername());
            
            // 生成简单的token（实际项目中应使用JWT）
            String token = UUID.randomUUID().toString().replace("-", "");
            
            LoginResponse response = LoginResponse.builder()
                    .id(user.getId())
                    .name(user.getName())
                    .email(user.getEmail())
                    .token(token)
                    .build();
            
            return CommonResponse.success(response, "登录成功");
        } else {
            return CommonResponse.failed("用户名或密码错误");
        }
    }

    @PostMapping("/register")
    public CommonResponse<?> register(@RequestBody LoginRequest registerRequest) {
        try {
            UserInfo user = userService.registerUser(
                registerRequest.getUsername(), 
                registerRequest.getPassword(), 
                registerRequest.getEmail()
            );
            log.info("用户注册成功：{}", user);
            
            LoginResponse response = LoginResponse.builder()
                    .id(user.getId())
                    .name(user.getName())
                    .email(user.getEmail())
                    .build();
            
            return CommonResponse.success(response, "注册成功");
        } catch (RuntimeException e) {
            log.error("注册失败：{}", e.getMessage());
            return CommonResponse.failed(e.getMessage());
        }
    }

    @PostMapping("/test")
    public String test() {
        System.out.println("测试");
        return "测试成功";
    }
}