package com.dahuaboke.hahbb.core.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.dahuaboke.hahbb.core.mybatis.CustomSqlSessionFactory;
import com.dahuaboke.hahbb.core.mybatis.CustomSqlSessionFactoryBuilder;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
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
    public DataSource dataSource(DataSourceProperties dataSourceProperties) throws Exception {
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        Map<DynamicDataSource.Key, DruidDataSource> targetDataSources = new HashMap<>();
        List<DataSourceProperties.Group> datasourceGroups = dataSourceProperties.getDatasource();
        if (datasourceGroups == null || datasourceGroups.isEmpty()) {
            throw new RuntimeException("DataSource config is null or empty");
        }
        for (DataSourceProperties.Group group : datasourceGroups) {
            List<DataSourceProperties.Config> datasourceConfigs = group.getDatasource();
            for (DataSourceProperties.Config config : datasourceConfigs) {
                DruidDataSource druidDataSource = getDruidDataSource(config);
                DynamicDataSource.Key dataSourceKey =
                        dynamicDataSource.createKey(group.getGroup(), config.getName());
                targetDataSources.put(dataSourceKey, druidDataSource);
            }
        }
        dynamicDataSource.setDataSources(targetDataSources);
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
        if (dataSource instanceof DynamicDataSource) {
            SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
            sqlSessionFactoryBean.setDataSource(dataSource);
            sqlSessionFactoryBean.setSqlSessionFactoryBuilder(new CustomSqlSessionFactoryBuilder(dataSource));
            SqlSessionFactory sqlSessionFactory = sqlSessionFactoryBean.getObject();
            org.apache.ibatis.session.Configuration configuration = sqlSessionFactory.getConfiguration();
            DynamicDataSource dynamicDataSource = (DynamicDataSource) dataSource;
            Map<DynamicDataSource.Key, SqlSessionTemplate> sessionTemplateMap = new HashMap<>();
            SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
            bean.setConfiguration(configuration);
            Map<DynamicDataSource.Key, DruidDataSource> dataSources = dynamicDataSource.getDataSources();
            for (Map.Entry entry : dataSources.entrySet()) {
                DynamicDataSource.Key dataSourceKey = (DynamicDataSource.Key) entry.getKey();
                DruidDataSource druidDataSource = (DruidDataSource) entry.getValue();
                sqlSessionFactoryBean.setDataSource(druidDataSource);
                sessionTemplateMap.put(dataSourceKey, new SqlSessionTemplate(sqlSessionFactoryBean.getObject()));
            }
            dynamicDataSource.setSqlSessionTemplates(sessionTemplateMap);
            return sqlSessionFactory;
        }
        throw new RuntimeException("dataSource is not DynamicDataSource");
    }
}
