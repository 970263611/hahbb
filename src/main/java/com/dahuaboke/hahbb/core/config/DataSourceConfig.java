package com.dahuaboke.hahbb.core.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.dahuaboke.hahbb.core.mybatis.CustomSqlSessionFactoryBuilder;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class DataSourceConfig {

    @Bean
    public DataSource dataSource(DataSourceProperties dataSourceProperties) {
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        Map<Object, Object> targetDataSources = new HashMap<>();
        List<DataSourceProperties.Group> datasourceGroups = dataSourceProperties.getDatasource();
        if (datasourceGroups == null || datasourceGroups.isEmpty()) {
            throw new RuntimeException("DataSource config is null or empty");
        }
        for (DataSourceProperties.Group group : datasourceGroups) {
            List<DataSourceProperties.Config> datasourceConfigs = group.getDatasource();
            for (DataSourceProperties.Config config : datasourceConfigs) {
                try {
                    DruidDataSource druidDataSource = getDruidDataSource(config);
                    DynamicDataSource.Key dataSourceKey =
                            dynamicDataSource.createKey(group.getGroup(), config.getName());
                    targetDataSources.put(dataSourceKey, druidDataSource);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        dynamicDataSource.setTargetDataSources(targetDataSources);
        return dynamicDataSource;
    }

    private DruidDataSource getDruidDataSource(DataSourceProperties.Config config) throws SQLException {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setDriverClassName(config.getDriverClassName());
        druidDataSource.setUrl(config.getUrl());
        druidDataSource.setUsername(config.getUsername());
        druidDataSource.setPassword(config.getPassword());
        druidDataSource.setMaxActive((int) config.getMaxActive());
        druidDataSource.setInitialSize((int) config.getInitialSize());
        druidDataSource.setMinIdle((int) config.getMinIdle());
        druidDataSource.setName(config.getName());
        druidDataSource.init();
        return druidDataSource;
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        sqlSessionFactoryBean.setSqlSessionFactoryBuilder(new CustomSqlSessionFactoryBuilder(dataSource));
        return sqlSessionFactoryBean.getObject();
    }
}
