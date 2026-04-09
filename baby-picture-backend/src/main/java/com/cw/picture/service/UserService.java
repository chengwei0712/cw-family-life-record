package com.cw.picture.service;

import ch.qos.logback.core.util.MD5Util;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.cw.picture.entity.UserInfo;
import com.cw.picture.mapper.UserInfoMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 用户服务类
 *
 * @author chengwei
 * @since 2025-12-30
 */
@Service
public class UserService {

    @Resource
    private UserInfoMapper userInfoMapper;

    public UserInfo registerUser(String username, String password, String email) {
        // 检查用户名是否已存在
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", username);
        UserInfo existingUser = userInfoMapper.selectOne(queryWrapper);
        
        if (existingUser != null) {
            throw new RuntimeException("用户名已存在");
        }

        UserInfo user = new UserInfo();
        user.setName(username);
        user.setPassword(encryptPassword(password));
        user.setEmail(email);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        userInfoMapper.insert(user);
        return user;
    }

    public UserInfo findByUsername(String username) {
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", username);
        return userInfoMapper.selectOne(queryWrapper);
    }
    
    public boolean validatePassword(UserInfo user, String rawPassword) {
        return StringUtils.equals(encryptPassword(rawPassword), user.getPassword());
    }

    public String encryptPassword(String password) {
        // 使用密码加密算法对密码进行加密
        return DigestUtil.md5Hex(password);
    }
}