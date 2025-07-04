# hahbb
[![996.icu](https://img.shields.io/badge/link-996.icu-red.svg)](https://996.icu) 

多库操作简化，一次操作可以执行多库，支持单库事务。基于mybatis。

### 配置示例：

```yaml
spring:
  application:
    name: hahbb
  datasource:
  	#多个同样类型库的抽象名称：组
    - group: sit
      datasource:
      	#组下具体某一个库的名称
        - name: sit-1
          driverClassName: org.postgresql.Driver
          url: jdbc:postgresql://localhost:5432/postgres
          username: postgres
          password: 123456
          maxActive: 20
          initialSize: 5
          maxWait: 6000
          minIdle: 5
        - name: sit-2
          driverClassName: org.postgresql.Driver
          url: jdbc:postgresql://localhost:5433/postgres
          username: postgres
          password: 123456
          maxActive: 20
          initialSize: 5
          maxWait: 6000
          minIdle: 5
```

### 使用方式：

在mapper层，此种方式不支持事务。

```java
//类上需要有Mapper注解
@Mapper
public interface TestDao {
    //sql使用注解形式，这里配置了DataSourceGroup和DataSourceName，本次查询限定了查询sit组下的sit-1库
    @DataSourceGroup("sit")
    @DataSourceName("sit-1")
    @Select("select name from public.user")
    List<String> test();

    //sql使用xml形式，这里仅配置了DataSourceGroup，本次查询将查询sit组下所有库
    @DataSourceGroup("sit")
    List<String> test();
    
    //sql使用注解形式，这里配置了DataSourceGroup和DataSourceName，本次插入限定了插入到sit组下的sit-1库
    @DataSourceGroup("sit")
    @DataSourceName("sit-1")
    @Insert("insert into public.user values ('test','test')")
    int test();
    
    //sql使用xml形式，这里配置了DataSourceGroup，本次插入将插入到sit组下所有库
    @DataSourceGroup("sit")
    int test();
}
```

在service层使用，此种方式支持事务，但只支持单库事务。**如果使用这种方式，被调用的mapper上注解将不生效**。

```java
@Service
@Transactional
public class TestServiceForTransactional {
	@Autowired
    private TestDao testDao;
    
    //这里需要在方法上标记DataSourceGroup和DataSourceName注解，两者必须同时存在
    //此次数据库操作限定在sit组的sit-1库执行，即使mapper层配置了注解也将失效
    @DataSourceGroup("sit")
    @DataSourceName("sit-1")
    //如果类上没有标记Transactional注解，在方法上标记Transactional注解也可以
//    @Transactional
    public void transactional() {
        testDao.test();
        //如果这里触发了异常，test方法将回滚
//        int a = 1 / 0;
        testDao.test();
    }
}
```

[![LICENSE](https://img.shields.io/badge/license-Anti%20996-blue.svg)](https://github.com/996icu/996.ICU/blob/master/LICENSE)
