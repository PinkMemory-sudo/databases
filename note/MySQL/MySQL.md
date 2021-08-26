

# 安装



**配置文件**

```

```



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



 GRANT  



DML



数据库相关



**查看MySQL版本**

登录MySQL后使用select version();

或者不登录，直接使用 mysql --version获得mysql -V



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



**查看都有哪些table**

`show tables` 查看当前数据库中都有哪些表

`show table from 数据库名` 查看指定数据库中有哪些表



**创建数据量**



表



**创建表**

```mysql
create table 表名(列名 数据类型,...);
```



**查看表结构**

```mysql
odesc 表名;
```







创建表

create



# DQL



# DDL



# TCL



# 存储引擎