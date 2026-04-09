package com.cw.picture.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录响应数据
 *
 * @author chengwei
 * @since 2026-04-03
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponse {
    
    /**
     * 用户ID
     */
    private Long id;
    
    /**
     * 用户名
     */
    private String name;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * Token
     */
    private String token;
}