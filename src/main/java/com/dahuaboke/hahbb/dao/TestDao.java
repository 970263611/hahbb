package com.dahuaboke.hahbb.dao;

import com.dahuaboke.hahbb.core.aop.DataSourceGroup;
import com.dahuaboke.hahbb.core.aop.DataSourceName;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TestDao {

    @DataSourceGroup("sit")
    @Select("select name from public.user")
    List<String> test1();

    @DataSourceGroup("sit")
    List<String> test2();

    @DataSourceGroup("sit")
    @DataSourceName("sit-1")
    @Select("select name from public.user")
    List<String> test3();

    @DataSourceGroup("sit")
    @DataSourceName("sit-2")
    List<String> test4();

    //由于mybatis底层除了select都走update，所以这里测试insert即可

    @DataSourceGroup("sit")
    int test5();

    @DataSourceGroup("sit")
    @DataSourceName("sit-1")
    @Insert("insert into public.user values ('test6','6test')")
    int test6();

    @DataSourceGroup("sit")
    int test7();

    @DataSourceGroup("sit")
    @DataSourceName("sit-1")
    @Insert("insert into public.user values ('test8','8test')")
    int test8();
}
