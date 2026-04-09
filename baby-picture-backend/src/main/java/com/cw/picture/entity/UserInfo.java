package com.cw.picture.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.time.LocalDateTime;
/**
 * 用户信息
 *
 * @author chengwei
 * @since 2025-12-30
 */

@Data
@TableName("user_info")
public class UserInfo implements java.io.Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String password;

    private String email;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

}