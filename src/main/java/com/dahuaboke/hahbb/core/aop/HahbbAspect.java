package com.dahuaboke.hahbb.core.aop;

import com.dahuaboke.hahbb.core.config.DynamicDataSource;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.util.List;

@Aspect
@Component
@Order(1000)
public class HahbbAspect {

    @Autowired
    private DataSource dataSource;

    @Pointcut("@within(org.apache.ibatis.annotations.Mapper)")
    public void mapper() {
    }

    @Pointcut("@within(org.springframework.transaction.annotation.Transactional) || @annotation(org.springframework.transaction.annotation.Transactional)")
    public void transactional() {
    }

    @Before("mapper()")
    public void beforeMapper(JoinPoint joinPoint) {
        hahbb(joinPoint, false);
    }

    @Before("transactional()")
    public void beforeTransactional(JoinPoint joinPoint) {
        hahbb(joinPoint, true);
    }


    private void hahbb(JoinPoint joinPoint, boolean isTransactional) {
        if (dataSource instanceof DynamicDataSource) {
            DynamicDataSource dynamicDataSource = (DynamicDataSource) dataSource;
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            DataSourceGroup dataSourceGroup = method.getAnnotation(DataSourceGroup.class);
            if (dataSourceGroup != null) {
                String group = dataSourceGroup.value();
                DataSourceName dataSourceName = method.getAnnotation(DataSourceName.class);
                if (dataSourceName != null) {
                    String name = dataSourceName.value();
                    dynamicDataSource.setKey(group, name);
                    if (isTransactional) {
                        List<DynamicDataSource.Key> keys = dynamicDataSource.getKeys(group, name);
                        dynamicDataSource.setKey(keys.get(0));
                    }
                } else {
                    dynamicDataSource.setKey(group);
                    if (isTransactional) {
                        throw new RuntimeException("transactional mode need datasource group and name");
                    }
                }
            } else {
                throw new RuntimeException("datasource group is empty");
            }
        } else {
            throw new RuntimeException("dynamicDataSource is not DynamicDataSource");
        }
    }
}
