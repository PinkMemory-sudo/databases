**什么是mybatis**

mybatis是一个半自动的ORM框架，对JDBC进行了封装，是的我们只需要关心SQL语句和结果集的映射，不需要我们再去加载驱动，创建联结，获得statement等繁琐的步骤。



**#{}与${}取值的区别**

* #{}是以预编译的形式，会将#{}替换成?,然后调用PreparedStatement来执行；${}是将参数拼接到SQL语句中，会有SQL注入的风向
* 大多情况下都应该使用#{}，对于原生SQL不能预编译的表名等使用${}
* #{}会加双引号，${}是直接显示的。可以拼接字符串



**hibernate 和 mybatis 的区别？**

全自动与半自动，耦合性，移植性

* hibernate 是全自动的ORM框架，最常用的SpringDataJpa，可以自动为我们生成常用·的CRUD和对象关系映射；mybatis是半自动的ORM框架，需要我们手动写SQL和定义对象关系影视

* hibernate 是JPA的一个实现，跨方言的，移植性要比mybatis高

* hibernate 查询实体对象时会得到全部的字段，而mybatis的SQL和映射是可以手动写的(所以优化SQL方便)，可以根据需求指定，所以mybatis的耦合性要低。



**mybatis的使用场景**

适合在关系复杂，性能要求高的项目中使用



**mybatis的优点**

* 减少了JDBC相关操作

* 耦合性低，基于SQL编程，方便我们对SQL进行优化和结果集映射



**mybatis的缺点**

* 移植性差，无法做到数据库无关性
* 字段多或进行表关联时，SQL编写的工作量大



**mybatis工作原理**



**mybatis如何进行分页，分页的原理**



**mybatis支持延迟加载吗，实现原理是什么**



**mybatis的一二级缓存**



**mybatis的like查询应该怎么写**

"%#{name}%"是错的，#{}自带的有双引号

"%${name}%"可以，但是有SQL注入风险

推荐在service曾将name的值变成"%value%"，然后在xml文件中使用#{}引用



**mapper中传递多个参数**

* 多个单个参数实际会自动封装成一个map，通过map的key(paramX)或者下标引用
* 多个参数手动封装成Map或者POJO时，通过key或者属性名引用
* 多个单个参数前加@Param注解指定参数的名字



**mybatis如何进行批量操作**

 foreach作用于in,insert等



**插入操作时如何获得自增主键的Id，批量查询时怎么获得**

useGeneratedKey打开，指定id属性

```xml
<insert id="xxx" useGeneratedKey="true" keyProperty="userId">
</insert>
```



**字段名与属性名不相同时怎么办**

1. select时起别名
2. 通过resultMap的result标签指定



**mapper接口中的方法可以重载吗**

不能。因为需要通过全限定类名和方法名来寻找对象的SQL语句。



**mybatis怎么映射枚举类**





**mapper的xml文件中常用的有哪些标签**

SQL语句相关的select，insert，delete，update

ORM映射相关的resultMap

动态SQL相关的if，foreach，trim，where，choose等



**mybatis常用的注解**



