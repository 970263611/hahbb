package com.dahuaboke.hahbb.core.mybatis;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.defaults.DefaultSqlSessionFactory;

import javax.sql.DataSource;

public class CustomSqlSessionFactory extends DefaultSqlSessionFactory {

    private DataSource dataSource;

    public CustomSqlSessionFactory(Configuration configuration, DataSource dataSource) {
        super(configuration);
        this.dataSource = dataSource;
    }

    @Override
    protected SqlSession createSqlSession(Configuration configuration, Executor executor, boolean autoCommit) {
        return new CustomSqlSession(configuration, executor, autoCommit, dataSource);
    }
}
