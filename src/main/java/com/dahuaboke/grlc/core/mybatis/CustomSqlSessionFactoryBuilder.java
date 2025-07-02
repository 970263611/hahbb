package com.dahuaboke.grlc.core.mybatis;

import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import javax.sql.DataSource;

public class CustomSqlSessionFactoryBuilder extends SqlSessionFactoryBuilder {

    private DataSource dataSource;

    public CustomSqlSessionFactoryBuilder(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public SqlSessionFactory build(Configuration config) {
        return new CustomSqlSessionFactory(config, dataSource);
    }
}
