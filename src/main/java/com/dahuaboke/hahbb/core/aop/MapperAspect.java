package com.dahuaboke.hahbb.core.aop;

import com.dahuaboke.hahbb.core.config.DynamicDataSource;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.lang.reflect.Method;

@Aspect
@Component
public class MapperAspect {

    private static final Logger logger = LoggerFactory.getLogger(MapperAspect.class);

    @Autowired
    private DataSource dataSource;

    @Pointcut("@within(org.apache.ibatis.annotations.Mapper)")
    public void mapperMethods() {
    }

    @Before("mapperMethods()")
    public void beforeMethod(JoinPoint joinPoint) {
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
                } else {
                    dynamicDataSource.setKey(group);
                }
            } else {
                throw new RuntimeException("datasource group is empty");
            }
        } else {
            throw new RuntimeException("dynamicDataSource is not DynamicDataSource");
        }
    }
}
