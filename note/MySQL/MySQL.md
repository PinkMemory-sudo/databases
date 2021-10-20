* 理论
* 索引
* 事务
* 隔离级别
* 优化
* 存储引擎





B 树的一种变形，它是基于 B Tree 和叶子节点顺序访问指针进行实现，通常用于数据库和操作系统的文件系统中。

两种节点：内部节点，叶子节点。内部节点不存储数据，只存储索引，数据都存在叶子节点。



# 





备份



迁移

mysqldump，会进行锁表



**安装**

[参考](https://www.cnblogs.com/sjzxs/p/11362064.html)



**配置文件**

datadir





**远程访问授权**

* 别名与原名





# BASIC



## 安装



[安装参考](https://www.runoob.com/mysql/mysql-install.html)



设置root密码

创建用户

登录

登录命令的空格？

本地省略host

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



**添加用户**

[添加用户](https://www.runoob.com/mysql/mysql-administration.html)

1. 在mysql的user表中添加用户(最后FLUSH PRIVILEGES;否则需要重启MySQL)

```mysql
INSERT INTO user 
  			(host, user, password,select_priv, insert_priv, update_priv) 
VALUES  ('localhost', 'guest',PASSWORD('guest123'), 'Y', 'Y', 'Y');
FLUSH PRIVILEGES;
```

**注意**

*  PASSWORD() 函数来对密码进行加密
*  password() 加密函数已经在 8.0.11 中移除了，可以使用 MD5() 函数代替
*  一定要**FLUSH PRIVILEGES** 

2. 使用GRANT

```mysql
GRANT SELECT,INSERT,UPDATE,DELETE,CREATE,DROP
ON TUTORIALS.*
TO 'user'@'host'
IDENTIFIED BY 'password';
```





[激活](https://www.jianshu.com/p/e6665db44328)



**常见错误**



`is not allowed to connect tothis mmysql server`

用户不允许远程连接，解决方案https://blog.csdn.net/iiiiiilikangshuai/article/details/100905996





规范：

* 建议关键字大写，表名，字段名等小写
* 建议关键字单独一行(语句太长的情况下)
* 单行注释用#或`-- `(有空格)，多行注释用/**/



  



## **数据类型**



MySQL的数据类型大致分为三类：数值、日期/时间和字符串(字符)类型。

[MySQL数据类型参考](https://www.runoob.com/mysql/mysql-data-types.html)

bigdecimal



## 常用命令



**查看当前所在数据库**

`select database()` 

**性能**

```mysql
 SHOW TABLE STATUS [FROM db_name] [LIKE 'pattern'];
```

**服务器状态**

```mysql
show status;
```

**查看权限**

```
show grants;
```

**查看错误/警告日志**

```
show errors/warnings;
```

**查看MySQL版本**

登录MySQL后使用select version();

或者不登录，直接使用 mysql --version获得mysql -V



# DQL

**通配符查询**

**正则表达式查询**

## 基础查询



**格式**

```mysql
SELECT column_name,column_name
FROM table_name
```



多表查询时，MySQL执行顺序



**where**

用来声明查询条件来限定行；可以通过AND、OR来组合条件。

**计算次序**

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

当多个值时in更直观，而且**in速度一般要快**，常用来连接子查询。

**空值检查**

一个列不包含值时就是NULL，可以使用`IS NULL`判断。

不为空且不是空字符串

```
where ISNULL(name)=0 and LENGTH(trim(name))>0;
```

**NOT**

用于否定后面的条件

**LIKE**

| 通配符 | 描述               |
| ------ | ------------------ |
| %      | 任意字段出现任意次 |
| _      | 单个字符           |

**排序**

order by，对查询结果(基础查询，分组查询，子查询，联结查询)进行排序

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

**destinct**

* 必须放在开头
* 一般用来和count来查询不重复的条数
* 作用域后面的所有字段(即所有字段都相同才算重复)



## 分组查询

通过WHERE获得了相对小的表，然后按照指定的条件将这张表分成多个小表，计算每个小表中的汇总信息，每个小表返回一个汇总信息，最后这些汇总信息形成一张表。



GROUP BY来分组

* 可以按照多个字段进行分组
* select的字段都必须是在group by中给出的
* 如果分组有NULL，NULL也会作为一组
* 支持别名

**按表达式进行分组**



**按多个条件进行筛选**



**HIVING**

where是在分组前对数据进行过滤，而HAVING是来过滤分组后的聚合数据



## 子查询

一个SELECT的查询结果用在另一个SQL语句中。用()将SELECT语句括起来，放到另一个SQL(不止SELECT语句)语句中



**别名**

```mysql
column_name as alias
```

as可以省略



**子查询的分类**

* 一行一列
* 一行多列
* 多行多列

**子查询可以的位置**

* WHERE/HIVING后面
* SELECT后
* FROM
* EXISTS(用来判断子查询有没有值)



| 操作符    | 含义                           |
| --------- | ------------------------------ |
| IN/NOT IN | 符合IN中的都要/不要            |
| ANY/SOME  | 符合ANY/SOME中任意一个值都可以 |
| ALL       | 需要满足ALL中的所有值          |

* ANY/SOME/ALL都可以替代，中文比较难理解，一般用的很少这三个。
* IN后面的子查询，最好看看能不能DISTINCT



```mysql
SELECT *
FROM employees
WHERE id=(SELECT id FROM table_name)
AND name=(SELETC name FROM table_name)

```

当多个子查询是查询的同一张表，条件也相同时，可以考虑合并一下

```mysql
SELECT *
FROM employees
WHERE (id,nam)=(
    SELECT id,name FROM table_name
)
```





## 联结

联结是MySQL最强大的功能之一，多表查询，拿一张表去匹配另一张表。





### **内联结**

先做笛卡尔积，然后按照条件进行过滤

**保证联结一定有where语句**

起别名后就要使用别名，不能再使用原始的表名



**等值联结**

连接条件由`=`组成



**非等值联结**

连接条件不再由`=`连接

比如根据薪水去联结员工等级表，薪水在某一区间的联结到某一等级



**自联结**

(等值联结)，一张表需要查询两次，就可以写成自联结



大小表数据，where的对比





### **外联结**



**左(右)外联结**

左连接：左边的表，不管等不能联结上右边的表，每一行都是需要的。右联结就是右边表的每一行都是需要的。

```mysql
SELECT *
FROM table_name1 t1
LEFT JOIN table_name2 t2
ON 联结条件
WHERE 过滤条件
```



**全外联结**

MySQL不支持全外联结。





### 交叉联接

`CROSS JOIN`,与`FROM table1,table2`相同，取得笛卡尔积











# DDL



## 库的管理



**查看都有哪些数据库**

`show databases`

可以看到四个系统自带的数据库：

`information_schema`  保存元数据信息。 MySQL服务有多少个数据库，各个数据库有哪些表，各个表中的字段是什么数据类型，各个表中有哪些索引，各个数据库要什么权限才能访问 。

`mysql`  保存用户信息

`performace_schema`  收集数据库服务器性能参数； 提供进程等待的详细信息，包括锁、互斥变量、文件信息 

`test`  空的数据库



**切换数据库**

`use 数据库名`



**创建数据库**

```mysql
CREATE DATABASE [if not exists] database_name;
```

**查看建库语句**

```mysql
show create database database_name
```

**修改数据库**

不太可能需要修改

修改字符集

```mysql
ALTER DATABASE database_name CHARACTER SET utf8;
```

**删除数据库**

```mysql
DROP DATABASE [id exists] database_name;
```





## 表的管理



**查看都有哪些table**

`show tables` 查看当前数据库中都有哪些表

`show table from 数据库名` 查看指定数据库中有哪些表



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



**创建表**

```mysql
CREATE TABLE table_name(
	列名 类型 约束 备注,
    ...
);
```

**查看建表语句**

```mysql
show create database table_name
```

**修改表**

可以修改：

* 列名
* 表名
* 类型
* 约束
* 添加列



**修改列**

```mysql
ALTER TABLE table_name CHANGE COLUMN 旧列名 [新列名] 类型 [约束];
```

**添加列**

```mysql
ALTER TABLE table_name ADD COLUMN 旧列名 [新列名] 类型 [约束];
```

**删除列**

```mysql
ALTER TABLE table_name DROP COLUMN 旧列名 [新列名] 类型 [约束];
```

**修改表名**

```mysql
ALTE TABLE table_name RENAME TO new_table_name;
```

**删除表**

```mysql
DROP TABLE [if exists] table_name;
```

**复制表结构**

```mysql
CREATE TABLE table_name1 like table_name2
```

**复制表结构+数据**

```mysql
CREATE TABLE table_name1
SELECT * FROM table_name2;
```

* 复制部分表结构/数据，可以加WHERE



### **约束**

用于限制字段，保证表中数据的完整性和可靠性



**六大约束**

* 非空约束
* 默认约束(自动填充默认值)
* 唯一约束(允许有一个NULL)
* 主键约束
* 外键约束(用于限制两个表的关系，该值必须是另一个表的主键/唯一键)
* check约束(MySQL不支持，是对数据类型，范围等约束  )



**创建约束**

分为列级约束和表级约束

* 列级约束定义外键无效
* 表级约束不支持非空和默认



**添加列级约束**

```mysql
CREATE TABLE table_name(
	column_name type 约束 备注,
    ...
)
```



**添加表级约束**

```mysql
CREATE TABLE table_name(
    ... ,
    CONSTRAINT pk PRIMARY KEY (id),# 主键
    CONSTRAINT uq UNIQUE(account),# 唯一约束
    CONSTRAINT fk_主表_外表 FOREIGN KEY (edit_id) REFERENCES 表名(字段)
)
```

* `CONSTRAINT 约束名` 可以省略
* 一般只在表级约束中添加外键约束，其他约束在列级约束中定义
* 可以添加多个约束



**修改表的时候添加约束**

列级约束

```mysql
ALERT TABLE table_name MODIFY COLUNM colunm_name 类型 约束
```

表级约束

```mysql
ALERT TABLE table_name [CONSTRSINT name] ADD PRIMARY KE(id)
```



**修改表的时候删除约束**



```mysql
ALERT TABLE table_name MODIFY COLUNM colunm_name
```



```mysql
ALERT TABLE table_name DROP PRIMARY KEY
```



```mysql
ALERT TABLE table_name DROP INDEX index_name;
```



# DML



## 插入



* 支持批量插入

* 支持子查询

```mysql
INSERT INTO table_name (filed1_name,...) VALUES(value1,...),(...);
```

插入的值的顺序与指定的列名顺序和个数相同



```mysql
INSERT INTO table_name VALUES(value1,...),(...);
```

插入的值的顺序表中所有列名顺序和个数相同



## 修改



**单表更新**

```mysql
UPDATE table_name
SET column_name1=new_value,column_name2=new _value...
[WHERE ...]
```



级联更新(只支持内联结)

```mysql
UPDATE table_name1,table_name2
SET ...
WHERE 联结条件 AND 筛选条件
```



## 删除



```mysql
DELETE FROM table_name
where ...;
```



**删除整个表**

```mysql
TRUNCATE table table_name
```



**级联删除**

```mysql
DELETE table_name
FROM table1,table2
WHERE 联结条件
AND 筛选条件
```

* DELETE后加要删除的表，两张表都需要删除时就都写上





# TCL

Transaction Control Language

一组SQL语句组成一个单元，要么全执行，要么全失败(即失败的时候不能对数据库产生影响)。



## 事务



有些操作必须是原子性的，要成功都成功，要失败都失败，保证数据库的完整性。

**事务的四个特性**：

* 原子性 
* 一致性
* 隔离性
* 持久性



**隐式事务**

INSERT、DELETE，UPDATE、SELECT都是隐式的事务，MySQL**默认开启了自动提交**。单条语句就会提交。



**显示事务**

1. 关闭自动提交`SET AUTOCOMMIT=0`（只是关闭了当前会话的自动提交）

2. 开启事务`start transaction/BEGIN` （可选的）
3. 执行SQL语句
4. 提交/回滚事务。COMMIT/ROLLBACK

SAVEPOINT

一个事务中可以有多个 SAVEPOINT,ROLLBACK TO identifier 把事务回滚到标记点。

**事务的隔离级别**

当多个事务同时使用数据库中相同的数据时，就可能会相互影响。

***脏读***

事务T1读取到了事务T2还为提交的数据，当T2回滚时，T1读到的数据就是错误的

***不可重读***

不能重复读，重复读取到的两个值不一样。两个事务T1,T2,T1读取了一个字段的值，T2修改了该字段的值，T1再次读取该字段的值，会造成前后两个值不一致。

***幻读***

T1读取表中的字段进行统计，T2新增了记录并提交了事务，当T1在读取时就会多出来几行。与不可重读类似，不可重读时值变了，前后读取的值不相同。幻读时有新纪录插入，前后读取到的数量不同。

可以设置事务的隔离级别来解决这几个现象

MySQL支持四种隔离级别

`select  @@tx_isolation;`查看隔离级别	

| 级别                       | 描述                               |
| -------------------------- | ---------------------------------- |
| 读取未提交READ UNCOMMITTED | 会导致读取未提交、不可重复读和幻读 |
| 读取已提交READ COMMITTED   | 会导致不可重读和幻读               |
| 可重复读REPEATABLE READ    | MySQL默认的，会导致幻读            |
| 串行化SERIALIZABLE         | 可以避免三个问题，但性能低         |

设置隔离级别

```mysql
SET TRANSACTION level;
```







# 存储引擎



查看存储引擎

`SHOW ENGIENS;`

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



什么是索引：索引也是一张表，保存了主键和索引字段，指向实体表的记录。例如利用字典的目录找字

优点：提高检索效率

缺点：索引表也需要维护，在插入更新和删除时，还需要更新索引表。

* 一般来说索引本身也很大，不可能全部存储在内存中，因此**索引往往是存储在磁盘上的文件中的**
* 我们通常所说的索引，包括聚集索引、覆盖索引、组合索引、前缀索引、唯一索引等，没有特别说明，默认都是使用B+树结构组织

**查看当前表中的索引**

```mysql
show index from table_name
```



**索引的分类**

单列索引

组合索引





主键索引

唯一索引

普通索引

全文索引

前缀索引



**创建索引**

直接创建

ALTER创建

CREATE时创建



删除索引







**AVL Tree**

**红黑树**

**B Tree** 

Balance Tree，平衡树是一颗查找树，并且所有叶子节点位于同一层。

**B+ Tree**

全文索引

**主键**



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



函数的移植行不强，不赞成使用函数。



**字符函数**

| 函数名        | 描述                                                 |
| ------------- | ---------------------------------------------------- |
| length()      | 获取字节个数                                         |
| concat()      | 多个字符串拼接成一个                                 |
| upper()/lower | 统一大小写                                           |
| substr()      | 多个重载，用法和Java一样(字符的长度)                 |
| instar()      | 等效于Java的indexOf，没找到返回0                     |
| trim()        | 默认去除空格,  trim('a' from 'absba')表示去除前后的a |
| lpad() rpad   | 添加n个前缀/后缀                                     |
| replace()     | 替换                                                 |



SQL中的索引都是从1开始的



**数学函数**

| 函数       | 描述                                   |
| ---------- | -------------------------------------- |
| round()    | 四舍五入默认保留整数，可以指定小鼠位数 |
| cell()     | 向上取整                               |
| f'loor()   | 向下取整                               |
| truncate() | 截取                                   |
| mod        | 取余                                   |

取余

a%b=a-a/b*b



**日期函数**

| 函数        | 描述                             |
| ----------- | -------------------------------- |
| NOW()       | 返回日期时间                     |
| CURDATE()   | 返回日期                         |
| CURTIME()   | 返回时间                         |
| YEAR()      | 获得日期中的年，传入带日期的参数 |
| MONTH()     | 月                               |
| DAY()       | 日，时分秒相同                   |
| STR_TO_DATE |                                  |
| DATE_TO_STR |                                  |
| DATEDIFF    |                                  |



**其他函数**

| 方法       | 描述 |
| ---------- | ---- |
| version()  |      |
| database() |      |
| user()     |      |



**聚合函数**

有时我们只需要统计信息而不是具体的数据。

| 聚合函数 | 描述   |
| -------- | ------ |
| AVG()    | 平均值 |
| COUNT()  | 个数   |
| MAX()    | 最大值 |
| MIN()    | 最小值 |
| SUN()    | 总和   |

聚合函数都会忽略NULL，所以在WHERE时注意去掉NULL值

聚合函数可以和IDSTINCT连用

SUM(distinct 字段名)表示值不相同的和

**COUNT**

COUNT(column_name)

COUNT(*),用来统计表的行数(不管是不是NULL)

COUNT(随便一个数字或字符) 相当于在表中加了一列，这一列每行都是这个数字或字符，最后统计数字或字符的个数

效率问题：

MYISAM，内部有计数器，COUNT(*)可以直接获得表的列数

INNODA，COUNT(*)和COUNT(1)相差不大，但比COUNT(colunm_name)效率高(多了IS NULL判断)





# 锁







分库分表

读写分离





# SQL 优化



视图，游标



**乐观锁，悲观锁**



































# 备份



[参考](https://www.runoob.com/mysql/mysql-database-import.html)

**导出数据**

**SELECT...INTO OUTFILE**



**mysqldump**

```
mysqldump -u root -p RUNOOB database_name [table_name]> dump.sql
```



**导入数据**

1. 进入MySQL。`source 文件`来导入

2. 直接导入到另一个MySQL

```mysql
mysqldump -u root -p database_name \
       | mysql -h other-host.com database_name
```

3. LOAD DATA
4. mysqlimport





# 存储过程

​		存储过程是一个预编译的SQL语句，优点是允许模块化的设计，就是说只需创建一次，以后在该程序中就可以调用多次。如果某次操作需要执行多次SQL，使用存储过程比单纯SQL语句执行要快。





# ？

时区问题

编码问题





































# B树

与平衡二叉树不同的是一个节点可以有多个元素

* 节点有序

* 一个节点可以存储多个元素，多个元素也排序了



B+树

* B树的所有优点

* 叶子节点之间有指针
* 非叶子节点的元素都在叶子节点上有冗余，并且排好了顺序，可以很好的支持全表扫描和范围查询



一个节点存储多个元素：降低树的高度，减少IO

非叶子节点的元素都在叶子节点上有冗余，并且排好了顺序，可以很好的支持全表扫描和范围查询



一个InnoDB页是一个节点，默认16k，一个节点可以存储多个元素就是一个InnoDB页可以存储多行记录。

一般情况下两层的B+树可以存储20000万行数据



**B+树根据什么排序**





