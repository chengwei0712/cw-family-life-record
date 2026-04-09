package com.cw.picture.entity;

import lombok.Builder;
import lombok.Data;

/**
 * @author chengwei
 * @version 1.0
 * @since  2025/8/11 10:00 上午
 * @description 数据库连接配置
 */
@Data
@Builder
public class DataSourceProperties {


    /**
     * 连接地址
     */
    private String datasourceUrl;

    /**
     * 用户名
     */
    private String datasourceUsername;

    /**
     * 密码
     */
    private String datasourcePassword;

    /**
     * 最大连接数据库连接数,设 0 为没有限制
     */
    private Integer maxActive;

    /**
     * 是否打印sql
     */
    private Boolean showSql;

    /**
     * 配置加密key，16位随机字符串，是否密码加密，不为空则需要进行解密
     */
    private String publicKey = "";

    /**
     * 分表规则，格式：t_vehicle&2&create_user&create_user.hashCode().abs()@_@t_vehicle_user_ref&2&create_user&create_user.hashCode().abs()
     * 逻辑表名&分表个数&分表依据字段名称&分表规则，@_@分隔多个规则
     * 数据库分表名称：t_vehicle_0,t_vehicle_1等等，根据数量区分
     */
    private String tableShards;

    /**
     * 类型包名
     */
    private String typeAliasesPackage;

    /**
     * mapper定义文件位置
     */
    private String mapperResourceLocation;

    /**
     * mybatis配置文件
     */
    private String mybatisConfigLocation;
}
