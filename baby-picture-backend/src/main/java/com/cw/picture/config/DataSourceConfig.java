package com.cw.picture.config;

import com.cw.picture.entity.DataSourceProperties;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author chengwei
 * @since 2025/6/24 11:00
 */
@Configuration
public class DataSourceConfig {

    @Value(value = "${spring.datasource.url:}")
    private String springDatasourceUrl;

    @Value(value = "${spring.datasource.username:}")
    private String springDatasourceUsername;

    @Value(value = "${spring.datasource.password:}")
    private String springDatasourcePassword;

    @Value(value = "${spring.datasource.maxActive:100}")
    private Integer maxActive;

    @Value(value = "${spring.datasource.showSql:true}")
    private Boolean showSql;


    /**
     * 自定义数据库配置
     */
    @PostConstruct
    public DataSourceProperties dataSourceProperties() {

        return DataSourceProperties.builder()
                .datasourceUrl(springDatasourceUrl)
                .datasourceUsername(springDatasourceUsername)
                .datasourcePassword(springDatasourcePassword)
                .maxActive(maxActive)
                .showSql(showSql)
                .mapperResourceLocation("classpath:mapper/*.xml")
                .mybatisConfigLocation("mybatis/mybatis-config.xml")
                .typeAliasesPackage("com.cw.picture.entity")
                .build();
    }

}
