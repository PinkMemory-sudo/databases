* 创建
* 实体类注解，TableName，TableId，TableFile等
* 一对多，多对一
* 分页，排序
* 条件构造器,QueryWrapper包含了所有SQL语句
* 事务
* 乐观锁



创建

1. 添加依赖，是MySQL的ORM框架，所以需要MySQL驱动依赖和MyBatis-Plus依赖
2. 配置数据源
3. mapper(dao)层继承BaseMapper
4. service接口继承IService
5. serviceImpl继承ServiceImpl
6. 注入Spring容器，在serviceImpl上添加@Service注入Service，mapper有两种方法在启动类上加MapperScan指定dao接口或者在每个dao接口上加Dao



分页

注意分页是M-P的一个插件，所以要添加分页拦截器配置

```java
@Configuration
public class MyBatisPlusConf {
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
```

分页模型接口IPage，实现子类Page

单表分页，直接调用service的page方法。

带排序的单表分页

```java
Page<User> userPage = new Page<>(1, 2);
userPage.addOrder(OrderItem.asc("age"), OrderItem.asc("name"));
userPage = userService.page(userPage);
```

先查询后分页

```java
Page<User> userPage = new Page<>(1, 2);
userPage.addOrder(OrderItem.asc("age"), OrderItem.asc("name"));
QueryWrapper<User> queryWrapper = new QueryWrapper<>();
queryWrapper.select("id", "age", "name");
userPage = userService.page(userPage, queryWrapper);
```

多表联查的分页

mapper接口中传入IPage对象，xml中写SQL语句，但是不用管分页的事情，拦截器会帮我们做。



