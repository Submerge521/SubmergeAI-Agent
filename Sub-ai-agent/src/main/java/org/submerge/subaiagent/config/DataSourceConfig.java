package org.submerge.subaiagent.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * ClassName: DataSourceProperties
 * Package: org.submerge.subaiagent.config
 * Description:
 *
 * @Author Submerge--WangDong
 * @Create 2025/7/19 15:05
 * @Version 1.0
 */
@Configuration
public class DataSourceConfig {
    /** 绑定并创建 MySQL 数据源 */
    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.mysql")
    public DataSourceProperties mysqlProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    public DataSource mysqlDataSource() {
        return mysqlProperties()
                .initializeDataSourceBuilder()
                .build();
    }

    /** MySQL 上下文的 JdbcTemplate，其他组件默认注入 */
    @Bean
    @Primary
    public JdbcTemplate mysqlJdbcTemplate(
            @Qualifier("mysqlDataSource") DataSource ds) {
        return new JdbcTemplate(ds);
    }

    /** 绑定并创建 PostgreSQL 数据源 */
    @Bean
    @ConfigurationProperties("spring.datasource.postgresql")
    public DataSourceProperties postgresProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource postgresDataSource() {
        return postgresProperties()
                .initializeDataSourceBuilder()
                .build();
    }

    /** PostgreSQL 专用的 JdbcTemplate，供 PgVectorStore 使用 */
    @Bean
    public JdbcTemplate postgresJdbcTemplate(
            @Qualifier("postgresDataSource") DataSource ds) {
        return new JdbcTemplate(ds);
    }
}
