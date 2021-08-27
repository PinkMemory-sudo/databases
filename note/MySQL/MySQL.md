* 理论
* 索引
* 事务
* 隔离级别
* 优化
* 存储引擎





B 树的一种变形，它是基于 B Tree 和叶子节点顺序访问指针进行实现，通常用于数据库和操作系统的文件系统中。

两种节点：内部节点，叶子节点。内部节点不存储数据，只存储索引，数据都存在叶子节点。



















































备份



迁移

mysqldump，会进行锁表



**安装**

[参考](https://www.cnblogs.com/sjzxs/p/11362064.html)



**配置文件**

datadir





**远程访问授权**







# BASIC



## 安装



[安装参考](https://www.runoob.com/mysql/mysql-install.html)



登录

创建用户

设置root密码



空格？

本地省略

docker



**配置文件**

```
[mysqld]
datadir=/var/lib/mysql
socket=/var/lib/mysql/mysql.sock

[mysql.server]
user=mysql
basedir=/var/lib

[safe_mysqld]
err-log=/var/log/mysqld.log
pid-file=/var/run/mysqld/mysqld.pid
```



[激活](https://www.jianshu.com/p/e6665db44328)



**常见错误**



`is not allowed to connect tothis mmysql server`

用户不允许远程连接，解决方案https://blog.csdn.net/iiiiiilikangshuai/article/details/100905996





规范：

* 建议关键字大写，表名，字段名等小写
* 建议关键字单独一行(语句太长的情况下)
* 单行注释用#或`-- `(有空格)，多行注释用/**/



**添加用户**

[添加用户](https://www.runoob.com/mysql/mysql-administration.html)

1. 在mysql的user表中添加用户(最后FLUSH PRIVILEGES;否则需要重启MySQL)
2. 使用GRANT

**注意**

*  PASSWORD() 函数来对密码进行加密

  

**查看MySQL版本**

登录MySQL后使用select version();

或者不登录，直接使用 mysql --version获得mysql -V



DML



## **数据类型**



MySQL的数据类型大致分为三类：数值、日期/时间和字符串(字符)类型。

[MySQL数据类型参考](https://www.runoob.com/mysql/mysql-data-types.html)



## 数据库相关操作



**创建数据量**

```mysql
CREATE DATABASE 数据库名;
```





**查看都有哪些数据库**

`show databases`

可以看到四个系统自带的数据库：

`information_schema`  保存元数据信息。 MySQL服务有多少个数据库，各个数据库有哪些表，各个表中的字段是什么数据类型，各个表中有哪些索引，各个数据库要什么权限才能访问 。

`mysql`  保存用户信息

`performace_schema`  收集数据库服务器性能参数； 提供进程等待的详细信息，包括锁、互斥变量、文件信息 

`test`  空的数据库



**切换数据库**

`use 数据库名`

`select database()` 查看当前所在数据库



**删除数据库**

```
drop database <数据库名>;
```





**表相关操作**



**创建数据表**

```mysql
CREATE TABLE table_name (column_name column_type...)ENGINE=InnoDB DEFAULT CHARSET=utf8;;
```



**删除数据表**

```mysql
DROP TABLE table_name ;
```



**查看都有哪些table**

`show tables` 查看当前数据库中都有哪些表

`show table from 数据库名` 查看指定数据库中有哪些表



**插入数据**

```mysql
INSERT INTO 
table_name ( field1, field2,...fieldN )
VALUES
( value1, value2,...valueN );
```



**查看字段**

```mysql
desc 表名;
```

或者

```mysql
show columns from 表名
```



**查看索引**

```mysql
show index from 表名
```



**性能**

```mysql
 SHOW TABLE STATUS [FROM db_name] [LIKE 'pattern'];
```



# DML







# DQL



**查询语句的格式**

```mysql
SELECT column_name,column_name
FROM table_name1[,table_name1...]
[WHERE Clause]
[LIMIT N][ OFFSET M]
```



**别名**

```mysql
column_name as alias
```

as可以省略



多表查询时，MySQL执行顺序



**where**

用来声明查询条件来限定行；可以通过AND、OR来组合条件。

*计算次序*

MySQL的计算次序是AND>OR,可以通过()改变优先级。



**between**

包括两端的值



**in**

与OR等价

```
where id in(值1,值2);
```

```
where id=值1 OR id=值2;
```

当多个值时in更直观，而且in速度一般要快，常用来连接子查询。





**空值检查**

一个列不包含值时就是NULL，可以使用`IS NULL`判断。

不为空且不是空字符串

```
where ISNULL(name)=0 and LENGTH(trim(name))>0;
```



**NOT**



**通配符查询**



**LIKE**

| 通配符 | 描述               |
| ------ | ------------------ |
| %      | 任意字段出现任意次 |
| _      | 单个字符           |
|        |                    |





**排序**

order by

通过order by,来指定一个或多个字段来进行排序

```mysql
order by 字段1 [desc], 字段2 [desc];
```

a和A的比较取决于数据库如何设置



**limit**

取前n行

```mysql
limit n;
```

从第n行开始的m行(第一行是1)

```mysql
limit n,m;
```

排序与limit组合使用来取最符合条件的n个。





destinct

**作用范围？**

select distinct (字段名)表示指定字段不能相同





**正则表达式查询**





# **计算字段**

有许多存储在数据库中的值和程序需要的字段值在格式或者组合上是不一致的，需要检索出来后经过格式转换，计算等再返回给客户端。

计算字段是运行SELECT语句时创建的。



**字符串计算**

**concat()**

将括号里面的所有值拼接成一个字符串

**RTrim()**



**数字计算**

加减乘除





# 函数



函数的移植行不强，不赞成使用函数





# 聚合函数



有时我们只需要统计信息而不是具体的数据。

| 聚合函数 | 描述   |
| -------- | ------ |
| AVG()    | 平均值 |
| COUNT()  | 个数   |
| MAX()    | 最大值 |
| MIN()    | 最小值 |
| SUN()    | 总和   |

**COUNT(*) 与COUNT(列名)的区别**

COUNT(*)中包含NULL，COUNT(列名)不包含

使用聚合函数是，可以与distinct两用

```
SUM(distinct num)
```

表示不想同的num之和



# 分组查询

GROUP BY来分组

* 可以按照多个字段进行分组
* select的字段都必须是在group by中给出的
* 如果分组有NULL，NULL也会作为一组



**分组过滤**

用来排除一些分组

HAVING来过滤分组



**分组排序**



# 子查询

一个查询的结果可以作为另一个查询的条件，执行顺序是先执行内部的查询



# 联结

联结是MySQL最强大的功能之一



# 主键和外键



外键是另一张表的主键，用来表示两张表之间的关系

定义主外键来实现引用的合法性。

先做笛卡尔积，然后按照条件进行过滤

**保证联结一定有where语句**



**内联结**

INNER JOIN(等值联结)



大小表数据，where的对比





**自联结**



**外联结**

















# DDL





## 服务器相关命令



**服务器状态**

```mysql
show status;
```



查看建表语句

```
show create table table_name
```

查看建库语句

```
show create database database_name
```

查看权限

```
show grants;
```

查看错误/警告日志

```
show errors/warnings;
```







# 存储引擎



用来干嘛的

存储引擎用来管理处理数据，负责创建数据库，创建表，处理查询请求等。

MySQL打包了三个引擎，具有不同的使用场景。

**InnoDB**

支持事务，不支持全文索引

**MEMORY**

数据存储在内存，所以速度快，适合做临时表，功能上等同于MyISAM

**MyISAM**

支持全文索引，不支持事务。







# 索引



**AVL Tree**



**红黑树**





## B Tree 



Balance Tree，平衡树是一颗查找树，并且所有叶子节点位于同一层。

































## B+ Tree



全文索引





























**主键**





# 锁





# 事务



有些操作必须是原子性的，要成功都成功，要失败都失败，保证数据库的完整性。

事务的四个特性：

* 原子性 就是把一个事务中的所有操作当成一个操作(原子)，这个操作要么都执行成功，要么失败时回滚
* 一致性
* 隔离性
* 持久性



事务的隔离级别



分库分表

读写分离





# SQL 优化



视图，游标



**乐观锁，悲观锁**



