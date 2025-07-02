package com.dahuaboke.grlc.dao;

import com.dahuaboke.grlc.core.aop.DataSourceGroup;
import com.dahuaboke.grlc.core.aop.DataSourceName;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TestDao {

    @DataSourceGroup("dev")
    @DataSourceName("dev-1")
    @Select("select name from public.user where id = 'a'")
    String test1();

    @DataSourceGroup("sit")
    @DataSourceName("sit-1")
    @Select("select name from public.user")
    List<String> test();
}
