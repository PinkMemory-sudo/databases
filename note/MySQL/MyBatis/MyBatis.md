* ORM框架
* 使用流程
* 配置
* 映射
* 从XML创建SqlSessionFactory
* 指定Mappe.XML位置，加载Mapper的方式
* 动态SQL
* 懒加载



原始的流程

1. 根据XML(全局配置文件)创建一个SqlSessionFactory
2. 通过SqlSessionFactory来获得一个SqlSession
3. 通过SqlSession执行SQL(SQL写在XML中，并且路径要配置到全局配置文件中)
4. 关闭SqlSession





XML

通过namespace和Id使得XML与接口绑定，XML生成一个代理对象



#与&





# 全局配置文件

* datasource
* 连接池
* 事务管理器
* 别名 



引入dtd约束，在写全局配置的XML时会有提示

**properties**

使用properties标签引入外部properties文件，比如将数据源的配置信息抽取到另一个配置文件然后再导入进来，就可以引用properties中的变量。



**settings**

用来设置mybatis的运行行为

map。。。

下划线与驼峰转换



**typeAliases**

主要解决Java类需要全限定名(太长)的问题

给**Java类**起一个简单的别名,这样使用全类名时可以用别名替换。可以为某个/批量起别名。别名不区分大小写。还可以在实体类上使用@Aliass来其别名。

已经默认存在的别名：

日期

基本类型及包装类型



**typeHandlers**

类型处理器，Java数据类型与数据库类型的转化

：JSR310



**plugins**

拦截SQl的执行，在执行前后进行增强 (Executor,parameterHandle,ResultSetHandle,statementHandler)



**enveroments**

测试和开发用的环境不同，可以配置多个环境，每个环境必须有dataSource和transactionManager配置



**databaseIdProvider**

MyBatis可以执行不同厂商的SQL。在mapper.xml中可以指定databaseId来指明该SQL语句由哪个厂商执行



**mappers**

指定SQL映射文件(mapper.xml)的位置



这些标签都是有顺序的





**注解**

@select



# 映射文件

映射文件通过namespace与接口关联，通过标签的id与对应的接口的方法关联。



**增删改查标签**



```xml
<insert id="方法名" parameterType="",>
    sql语句
</insert>
```

* 取对象的值可以直接使用#{属性名}来获取
* parameterType可以省略



```xml
<update id="方法名">
    SQL语句
</update>
```



```xml
<delete id="方法名">
    SQL语句
</delete>
```



增删改可以直接返回int，long，boolean。无需在mapper文件中指定，接口中指定就行。



**insert时获得自增的id**

insert标签添加配置

* useGenerateKeys告诉mybatis使用主键自增策略
* ketProperty，指定主键值交给谁

```xml
<insert id="方法名" parameterType="" useGenerateKeys="true" ketProperty="id”>
    sql语句
</insert>
```



## 参数处理

接口中可以指定多个参数或者封装成一个pojo，在mapper中都可以通过#{参数名}或者#{属性名}来获得参数值。



1. **接口只有一个参数时**

#{x}x随意写都可以获得这个参数值



2. **接口的方法中有多个参数时**

接口的方法中有多个参数时，MyBatis会把他封装成一个map。封装成map时，key就是param0...paramN

#{}就是从map中取值。所以多个参数时通过param或者索引下标获得(#{param0}或者#{0})。

如果不想使用#{param0}或者#{0}，可以在接口方法的参数前加@Param来指定参数的key。



3. **封装成Map或者类**

而如果自己封装成map时(或对象)，就可以使用自己map中的key来获得值。

所以，**需要多个参数时建议封装一下**



**取数组(或者集合)中的参数**

```
参数名[下标]
```



todo

**#{}更丰富的取值用法**

指定取值时参数相关的规则：



## **结果集映射**



### **resultType**

用来自动ORM转换，直接指定结果集要转换成的类型

***1. 返回List***

resultType中指定集合的泛型而不是List

***2. 返回Map***

resultType指定为map

***3. 封装成<String,实体类>***

resultType中指定实体类，在接口的方法中添加@MapKey注解指定谁用来做Map的key



### **resultMap**

结果集不能直接指定映射类型的，需要我们指定怎么映射，用来封装更加复杂的结果集映射

与resultType**只能二选一**

使用resultMap自定义映射规则，在selecte标签中指定resultMap的Id

```xml
<resultMap id="MyMap" type>
</resultMap>
```

```xml
<select id="" resultMap="MyMap">
</select>
```

resultMap的子标签：

| 子标签   | 描述             |
| -------- | ---------------- |
| <id>     | 指定主键列的映射 |
| <result> | 指定普通列的映射 |
|          |                  |

而resultMap中没有指定的列会尝试自动映射



### **一对一映射**

员工对象Employee，每个员工属于一个部门Department，所以Employee中都一个Department对象，这时候结果集中的字段该如何与Employee实体类映射？

方法一：*使用级联属性封装*

```xml
<resultMap type="" id="">
    <result column="字段名" property="Department.属性名"></result>
</resultMap>
```

方法二：使用*association指定联合的JavaBean*

```xml
<resultMap type="" id="">
    <association property="哪个属性名时需要联结的" javaType="联结成什么JavaBean">
    <!--根据情况在对嵌套的JavaBean进行映射-->
    </association>
</resultMap>
```



方法三：*分步查询*

在关联映射时，不再是一下查出来，而是通过association的select标签在去查一次，将结果封装成嵌套的Department对象

1. 先查出Employee然后根据部门编号查出Department
2. 分布查询支持延迟加载，用到Department时再去查询

传递多个值

```xml
coumn="{key1=colume1...}"
```





### 一对多映射

一个部门中有多个员工，Department有个List\<Employee>

方法一：*使用collection指定List的映射*

```xml
<collection property="实体类中List的属性名" ofType="List的泛型">
	<!--根据情况是狗需要指定List泛型的映射-->
</collection>
```

方法二：分布查询



**鉴别器**

根据结果集某列值的不同改变映射行为

discriminator



# 动态SQL

简化动态拼接SQL

**遇见特殊符号需要使用XML的转义字符**，比如&&





## if

有时候需要根据过滤条件去拼接SQL的查询条件，有哪些过滤条件就拼上对应的SQL语句

```xml
<if test="判断表达式(OGNL表达式)"></if>
```



**问题一：** 第一个if不满足，第二个if满足时，前面就会多个and

方案1：最前面加一个1=1，以后的条件就都可以在前面加and了

方案2：使用where标签，将if标签都放在where标签中，如果where中第一个语句多出and会自动去掉。使用where标签的前提是每个and都写在语句的前面。



**set标签**

主要用来封装修改条件，带了哪些属性就修改哪些值

set field=value,field2=value2, 可能最后会多一个,。把更新条件写在set标签中



## choose

分支选择，多选一

```xml

```





## trim

trim用来处理拼装完成的SQL语句，去掉/加上一些前缀或后缀

| 属性             | 描述         |
| ---------------- | ------------ |
| prefix           | 添加指定前缀 |
| prefisOverrides  | 去掉指定前缀 |
| suffix           | 添加指定后缀 |
| suffixOverirides | 去掉指定后缀 |



## foreach

遍历出的元素赋值给指定元素

使用场景：

1. in中

比如需要`select * from table_name where id in (...)`,就需要遍历集合拼接出in语句

2. 批量保存/修改中



## bind

将绑定到一个变量方便以后引用



## sql

抽取可以重用的sql语句



## 内置参数

除了传进来的参数，mybatis中还有两个默认参数

_parameter代表整个参数

——databaseId



# 批量操作

upserter











# 逆向工程



# 与SpringBoot集成