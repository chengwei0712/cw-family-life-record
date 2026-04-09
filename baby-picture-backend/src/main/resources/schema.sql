-- 家庭记忆系统数据库初始化脚本

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS picture DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE picture;

-- 用户表
CREATE TABLE IF NOT EXISTS user_info (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    name VARCHAR(50) NOT NULL COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    avatar VARCHAR(255) COMMENT '头像URL',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户信息表';

-- 媒体文件表
CREATE TABLE IF NOT EXISTS picture_info (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    user_id BIGINT COMMENT '用户ID',
    file_name VARCHAR(255) NOT NULL COMMENT '文件名',
    file_path VARCHAR(500) COMMENT '文件路径',
    file_size VARCHAR(50) COMMENT '文件大小',
    file_type VARCHAR(50) COMMENT '文件类型',
    media_type VARCHAR(20) DEFAULT 'photo' COMMENT '媒体类型：photo/video',
    store_name VARCHAR(255) COMMENT '存储名称',
    album_id BIGINT COMMENT '相册ID',
    status INT DEFAULT 0 COMMENT '状态：0正常',
    is_deleted INT DEFAULT 0 COMMENT '是否删除：0否 1是',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='媒体文件表';

-- 日记表
CREATE TABLE IF NOT EXISTS diary_info (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    title VARCHAR(200) COMMENT '日记标题',
    content TEXT COMMENT '日记内容',
    mood VARCHAR(50) COMMENT '心情',
    weather VARCHAR(50) COMMENT '天气',
    is_deleted INT DEFAULT 0 COMMENT '是否删除：0否 1是',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='日记表';

-- 日记媒体关联表
CREATE TABLE IF NOT EXISTS diary_media (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    diary_id BIGINT NOT NULL COMMENT '日记ID',
    media_id BIGINT NOT NULL COMMENT '媒体ID',
    sort_order INT DEFAULT 0 COMMENT '排序顺序'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='日记媒体关联表';

-- 相册表
CREATE TABLE IF NOT EXISTS album_info (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    name VARCHAR(100) NOT NULL COMMENT '相册名称',
    description VARCHAR(500) COMMENT '相册描述',
    cover_image VARCHAR(255) COMMENT '封面图片',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='相册表';

-- 插入测试用户（密码为 md5('123456')）
-- INSERT INTO user_info (name, password, email) VALUES ('admin', 'e10adc3949ba59abbe56e057f20f883e', 'admin@example.com');
-- INSERT INTO user_info (name, password, email) VALUES ('test', 'e10adc3949ba59abbe56e057f20f883e', 'test@example.com');