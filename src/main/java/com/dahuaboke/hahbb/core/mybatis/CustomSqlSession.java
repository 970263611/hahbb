package com.dahuaboke.hahbb.core.mybatis;

import com.dahuaboke.hahbb.core.config.DynamicDataSource;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.exceptions.ExceptionFactory;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.reflection.ParamNameResolver;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.defaults.DefaultSqlSession;
import org.mybatis.spring.SqlSessionTemplate;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 不支持任务事务操作，需要事务勿用此类
 */
public class CustomSqlSession extends DefaultSqlSession {

//    private static final ExecutorService pool = Executors.newFixedThreadPool(5);

    private final Configuration configuration;
    private final Executor executor;

    private final boolean autoCommit;
    private boolean dirty;
    private List<Cursor<?>> cursorList;
    private final DynamicDataSource dataSource;

    public CustomSqlSession(Configuration configuration, Executor executor, boolean autoCommit, DataSource dataSource) {
        super(configuration, executor, autoCommit);
        this.configuration = configuration;
        this.executor = executor;
        this.dirty = false;
        this.autoCommit = autoCommit;
        if (dataSource instanceof DynamicDataSource) {
            this.dataSource = (DynamicDataSource) dataSource;
        } else {
            throw new RuntimeException("dataSource is not DynamicDataSource");
        }
    }

    @Override
    public <E> List<E> selectList(String statement, Object parameter, RowBounds rowBounds) {
        try {
            boolean template = dataSource.isTemplate();
            if (template) {
                return super.selectList(statement, parameter, rowBounds);
            } else {
                try {
                    dataSource.setTemplate(true);
                    List<DynamicDataSource.Key> keys = dataSource.getKeys(dataSource.getKey());
                    List<E> result = new ArrayList<>();
                    for (DynamicDataSource.Key key : keys) {
                        dataSource.setKey(key);
                        SqlSessionTemplate sqlSessionTemplate = dataSource.getSqlSessionTemplate(key);
                        result.addAll(sqlSessionTemplate.selectList(statement, parameter, rowBounds));
                    }
                    return result;
                } finally {
                    dataSource.clearTemplate();
                }
            }
        } catch (Exception e) {
            throw ExceptionFactory.wrapException("Error querying database.  Cause: " + e, e);
        } finally {
            ErrorContext.instance().reset();
        }
    }

    @Override
    public <T> Cursor<T> selectCursor(String statement, Object parameter, RowBounds rowBounds) {
        //由于事务问题，不支持cursor
        throw new RuntimeException("unsupported cursor");
    }

    @Override
    public int update(String statement, Object parameter) {
        try {
            boolean template = dataSource.isTemplate();
            if (template) {
                return super.update(statement, wrapCollection(parameter));
            } else {
                try {
                    dataSource.setTemplate(true);
                    List<DynamicDataSource.Key> keys = dataSource.getKeys(dataSource.getKey());
                    int updateSize = 0;
                    for (DynamicDataSource.Key key : keys) {
                        dataSource.setKey(key);
                        SqlSessionTemplate sqlSessionTemplate = dataSource.getSqlSessionTemplate(key);
                        updateSize += sqlSessionTemplate.update(statement, parameter);
                    }
                    return updateSize;
                } finally {
                    dataSource.clearTemplate();
                }
            }
        } catch (Exception e) {
            throw ExceptionFactory.wrapException("Error updating database.  Cause: " + e, e);
        } finally {
            ErrorContext.instance().reset();
        }
    }

    //----------------------------------------------------------------------------
    // 以下代码无需修改

    @Override
    public void commit(boolean force) {
        try {
            executor.commit(isCommitOrRollbackRequired(force));
            dirty = false;
        } catch (Exception e) {
            throw ExceptionFactory.wrapException("Error committing transaction.  Cause: " + e, e);
        } finally {
            ErrorContext.instance().reset();
        }
    }

    @Override
    public void rollback(boolean force) {
        try {
            executor.rollback(isCommitOrRollbackRequired(force));
            dirty = false;
        } catch (Exception e) {
            throw ExceptionFactory.wrapException("Error rolling back transaction.  Cause: " + e, e);
        } finally {
            ErrorContext.instance().reset();
        }
    }

    @Override
    public void close() {
        try {
            executor.close(isCommitOrRollbackRequired(false));
            closeCursors();
            dirty = false;
        } finally {
            ErrorContext.instance().reset();
        }
    }

    private void closeCursors() {
        if (cursorList != null && !cursorList.isEmpty()) {
            for (Cursor<?> cursor : cursorList) {
                try {
                    cursor.close();
                } catch (IOException e) {
                    throw ExceptionFactory.wrapException("Error closing cursor.  Cause: " + e, e);
                }
            }
            cursorList.clear();
        }
    }

    private boolean isCommitOrRollbackRequired(boolean force) {
        return !autoCommit && dirty || force;
    }

    private Object wrapCollection(final Object object) {
        return ParamNameResolver.wrapToMapIfCollection(object, null);
    }
}
