 

# 安装



```
docker pull redis:5.0.7
```





[Redis配置文件](http://download.redis.io/redis-stable/redis.conf)



```bash
docker run -p 6379:6379 --name redis \
-v /Users/chenguanlin/Documents/workspace/redis/conf/redis.conf:/etc/redis/redis.conf \
-v /Users/chenguanlin/Documents/workspace/redis/data:/data -d 7eed8df88d3b \
redis-server /etc/redis/redis.conf --appendonly yes
```

* 注意，提前在本地创建好配置文件





























# Basic Conscept



Redis： Remote dictionary server

键值对数据库，整个数据库通通加载内存中进行操作(减少IO操作)，定期通过异步的方式flush到磁盘

Redis支持多个数据库，并且每个数据库的数据是隔离的不能共享，并且基于单机才有，如果是集群就没有数据库的概念



优点：

* 支持持久化，可以将内存中的数据保存在磁盘中，重启的时候可以再次加载进行使用；
* 数据结构种类丰富
* Redis支持数据的备份，即master-slave模式的数据备份
* 性能极高 – Redis能读的速度是110000次/s,写的速度是81000次/s 
* 原子 – Redis的所有操作都是原子性的，意思就是要么成功执行要么失败完全不执行。单个操作是原子性的。多个操作也支持事务，即原子性，通过MULTI和EXEC指令包起来。
* key 过期等等特性。



**redis与其他缓存产品相比的优势**

* 支持数据的持久化
* 数据类型丰富
* 速度快，每秒能进行10万读写操作



## Redis客户端

**获得所有配置项**

`CONFIG GET ` xxx

通过redis.conf或者config set修改配置



## 配置

配置文件：redis.conf





Redis 命令用于在 redis 服务上执行操作。要在 redis 服务上执行命令需要一个 redis 客户端

| 命令                             | 描述                                                         |
| -------------------------------- | ------------------------------------------------------------ |
| redis-cli -h host -p port        | 使用本机+默认端口时可以只写redis-cli                         |
| auth password                    | 有密码时要认证                                               |
| config  set requirepass 密码     | 设置密码，立即生效，修改配置文件中的requirepass需要重启redis |
| config get requirepass           | 查看密码                                                     |
| reids-server -port 6379 配置文件 | 启动redis服务                                                |
| ping                             | 用于检查redis服务是否启动                                    |



## Key

| 命令                   | 描述                          |
| ---------------------- | ----------------------------- |
| keys  pattern          | 查找符合条件的key             |
| exists key             | 检查key是否存在，不会模糊查询 |
| type key               | 查看key对那个的value的类型    |
| del  key               | 删除key                       |
| flushdb                | 删除所有key                   |
| dump key               | 序列化key，返回序列化后的值   |
| expire key seconds     | 给指定key设置过期时间         |
| EXPIREAT key timestamp | 设置key什么时候过期           |
| MOVE key db            | 讲当前key移动到另一个数据库   |
| PERSIST key            | 移除过期时间                  |
| PTTL key               | 返回剩余的过期时间， 毫秒     |
| TTL key                | 返回剩余时间， 秒             |
| randomkey              | 随机返回一个key               |
| rename key newName     | 重命名                        |

* 如何给一个可以设置过期时间

**keys**

Supported glob-style patterns:

- `h?llo` matches `hello`, `hallo` and `hxllo`
- `h*llo` matches `hllo` and `heeeello`
- `h[ae]llo` matches `hello` and `hallo,` but not `hillo`
- `h[^e]llo` matches `hallo`, `hbllo`, ... but not `hello`
- `h[a-b]llo` matches `hallo` and `hbllo`





## 数据类型

Redis支持五种数据类型：string（字符串），hash（哈希），list（列表），set（集合）及zset(sorted set：有序集合)。



五种类型的操作非常相似，只是存储方式上有所不同



## String

string 类型是二进制安全的。意思是 redis 的 string 可以包含任何数据。比如jpg图片或者序列化的对象。string 类型的值最大能存储 512MB。



**常用的操作**

1. 赋值

赋值，赋值并返回旧值，赋值并设置过期时间，同时添加多个键值对，key不存在时才赋值，存在时追加赋值

| 操作                 | 命令                                   |
| -------------------- | -------------------------------------- |
| 赋一个值             | set key val                            |
| key不存在时才设值    | setnx key val nx表示not exist          |
| 获取原值同时设置新值 | getset key value                       |
| 多重赋值             | mset key val key val                   |
| 赋值并指定过期时间   | setex key seconds value 也可以是msetex |
| 追加赋值             | append key value                       |

取值

按照key取值，只取value的一部分，同时取多个值

| 操作     | 命令                           |
| -------- | ------------------------------ |
| 取值     | get key                        |
| 多重取值 | mget key key                   |
| 取一部分 | getrange key start end(-1开始) |

**当String中存的是数字时**

| 操作      | 描述                          |
| --------- | ----------------------------- |
| 自增1     | incr key                      |
| 自减1     | decr key                      |
| 自增n     | **incrby** key  n n可为负     |
| 自减n     | decrby key n n可为负          |
| 自增float | incrbyfloat key 值 值可以为负 |

字符串的操作

| 操作 | 描述                                      |
| ---- | ----------------------------------------- |
| 长度 | strlen key                                |
| 追加 | append key 添加内容                       |
| 截取 | getrange key 下标1 下标2 闭区间           |
| 覆盖 | setrange key 下标1 值  没覆盖完保持原来的 |



## Hash

键值对集合，适合存对象

常见操作

与String命令相比，**前面多了个h**

| 操作                       | 描述                                     |
| -------------------------- | ---------------------------------------- |
| 赋值一个字段的             | HSET key field value                     |
| 一次赋值多个字段的         | HMSET key field1 value1 [field2 value2 ] |
| 获得所有hash中的key        | hkeys pattern                            |
| 获得一个hash中的某个字段值 | HGET key field                           |
| 获得一个hash中的所有字段值 | hgetall key                              |
| 字段不存在时设置           | HSETNX key field value                   |
| 一个hash中某个字段是否存在 | HEXISTS key field                        |
| 一个hash中的一个字段自增   | HINCRBY key field increment              |
| hash中浮点数自增           | HINCRBYFLOAT key field increment         |
| 获得字段个数               | hlen key                                 |

​	

## List



字符串列表，列表最多可存储 232 - 1 元素，一个双向链表，所以在头部或尾部插入

命令以l开头

赋值

| 操作                                                        | 命令                                                         |
| ----------------------------------------------------------- | ------------------------------------------------------------ |
| 在key对应的右侧插入数据，如果没有key则会先创建再插入        | rpush key value1 value2...                                   |
| 在key对应的右侧插入数据，如果没有key则什么也不操作          | rpushx key value1, value2...                                 |
| `弹出 key 代表的链表的右侧数据；如果不存在 key，则返回 nil` | `rpop key`                                                   |
| 讲弹出的值插入到另一个                                      | `rpoplpush a b`  弹出a的右侧，添加到b的左侧  **只能这样**    |
| 在一个元素之前或之后插入                                    | `linsert key before|after a b``在元素a之后/之前插入b。注意两端不能插` |
| 修改指定index下标的值                                       | `lset key index value`                                       |



删除，删除指定的值或者不是该值的值

| 操作                                          | 命令                                                         |
| --------------------------------------------- | ------------------------------------------------------------ |
| `删除链表 key 中值等于 value 的 count 个元素` | `lrem key count value`  值表示删几个(0表示所有) 负号表示方向 |
| `ltrim key start end`                         | 删除未选中的                                                 |



查看

| 操作                                | 描述                   |
| ----------------------------------- | ---------------------- |
| `查看 index 位置的元素`             | `lindex key index`     |
| `查看从 start 到 end (包括) 的元素` | `lrange key start end` |
| `查看 key 中元素个数`               | `llen key`             |



## Set

集合是通过哈希表实现的，所以唯有重复的元素，添加，删除，查找的复杂度都是 O(1)

常用操作

复制

| 操作                    | 描述                     |
| ----------------------- | ------------------------ |
| `向集合 key 中添加元素` | sadd key member[…member] |



删除

| 操作                        | 描述                     |
| --------------------------- | ------------------------ |
| `删除集合 key 中的多个成员` | srem key member[…member] |
| `随机删除一个并返回`        | spop key                 |
| `删除指定元素`              | srem key value1 value2   |



查看

| 操作                                | 描述                                                         |
| ----------------------------------- | ------------------------------------------------------------ |
| `显示集合 key 的元素个数`           | scard key                                                    |
| `查询 member 是不是集合 key 的元素` | sismember key member                                         |
| `显示所用元素`                      | smembers key                                                 |
| `随机显示集合中某几个元素`          | srandmember key count count大于集合长度则返回所有，小于0时时返回绝对值个单数可能会重复 |
| `查看是否包含某元素`                | sismember key value                                          |



集合操作

| 操作                                                   | 描述                            |
| ------------------------------------------------------ | ------------------------------- |
| `计算集合 key1 和 key2 的差集`                         | sdiff   key1 key2               |
| `计算集合 key1, key2... 的并集`                        | sunion key1 key2 …              |
| `计算集合 key1, key2... 的交集`                        | sinter key1 key2                |
| `计算集合 key1 和 key2 的差集，并将结果存入 目标集合`  | sdiffstore 目标集合  key1 key2  |
| `计算集合 key1, key2... 的并集，并将结果存入 目标集合` | sunionstore 目标集合  key1 key2 |
| `计算集合 key1, key2... 的交集，并将结果存入 目标集合` | sinterstore 目标几个  key1 key2 |



## ZSet

sorted set，有序的set，**每个元素会关联一个分数，根据分数进行排序**，可以做带权重的消息队列。

由hash表或跳跃表实现

赋值

| 操作                                          | 描述                      |
| --------------------------------------------- | ------------------------- |
| `向集合 key 中添加元素，附加元素的分数 score` | zadd key score1 member1…… |

即Zset中的元素都会用一个得分，zset会根据得分进行排序



读取

读取操作可以按排序的范围/得分的范围/正序/倒序

| 操作                                                         | 描述                         |
| ------------------------------------------------------------ | ---------------------------- |
| 查询指定空间得元素(默认升序)可选性withscores表示是否包含分数 | zrange key 下标1 下标2       |
| 查询指定空间元素，降序                                       | zrevrange key 下标1  下标2   |
| 查询分数在指定空间的元素，inf表示无穷，默认是闭区间，使用（表示开区间  。默认也是降序 | zrangebyscore key min max    |
| 查询分数在指定空间的元素，降序                               | zrevrangebyscore key max min |
| 查看成员分数                                                 | zscore key member            |
| 查看成员个数                                                 | zcard key                    |
| 查看分数在某一区间上的成员个数                               | zcount key min max           |
| 查看成员的名次                                               | zrank key member             |
| 查看成员的倒序名次                                           | zrevrank key member          |



删除

| 操作         | 描述                         |
| ------------ | ---------------------------- |
| 按成员名删除 | zrem key member1 member2     |
| 按分数删除   | zremrangebyscore key min max |
| 按排名删     | zremrangebyrank key min max  |



分数的自增

`zincrby key 自增的分数 member名`



**集合运算**



## Bitmap



**不是一种数据类型**

本身是字符串，只不过能进行位操作。可以当成是由01组成的数据，数组的下标叫做偏移量。

使用偏移量来作为key，偏移量对应的值作为value，用来节省存储时间。

key为数字，value只有01两种状态



**使用场景**

* 适合key连续的，活跃的

**示例**

某用户id,是否开通了该功能。

用户的id作为偏移量，开通为1，未开通为0



| 命令                      | 描述                       |
| ------------------------- | -------------------------- |
| `setbit key offset value` | 将offset对应的bit设置为key |
| `getbit key offset`       | 获得offset对应bit值        |
| `bitcount key`            | 统计bit为1的数量           |
|                           |                            |



**bitop**

支持四种操作：AND, OR, XOR,NOT。按位操作后保存到另一个key中

***按位与：***`BITOP AND destkey srckey1 srckey2 srckey3 ... srckeyN`

***按位或：***`BITOP OR destkey srckey1 srckey2 srckey3 ... srckeyN`

***按位异或：***`BITOP XOR destkey srckey1 srckey2 srckey3 ... srckeyN`

***按位取反：***`BITOP NOT destkey srckey`

not命令只有一个输入的key，因为它的作用是将bit反转而不是和其他key参与运算

The result of the operation is always stored at `destkey`

进行复合操作时，长度不一样会使用0填充，返回的key的值的长度=最长的长度。



## HyperLogLog



**基数问题**

求集合中不重复的元素的个数的问题



只花费12k的内存，就能计算出2^64个不同元素的基数，只根据输入的元素来计算基数，而不存储元素本身



| 命令·                           | 描述                                          |
| ------------------------------- | --------------------------------------------- |
| pfadd key  values               | 不重复返回1，重复返回0                        |
| pfcount key[key1,key2...]       | 查看key对应的基数,还可以查看多个key组成的基数 |
| pfmerge destkey srckey1 srckey2 | 合并                                          |



## GEO

3.2中新增堆GEO数据的支持。geographic，就是该元素的二维坐标



| 命令                              | 描述                                           |
| --------------------------------- | ---------------------------------------------- |
| geoadd key x y name[key x y name] | 将name的经纬度添加到key中                      |
| geopos key name                   | 将key中name的经纬度取出来                      |
| geodist key name1 name2 [unit]    | 获得两点的直线距离，单位默认m,此外还有km,mi,ft |
| georadius key x y radius unit     | 获得指定坐标指定范围内的name                   |



## 配置文件

* 大小写不敏感
* 1k与1kb的区别
* include 路径，可以包含其他配置文件



**通用配置**

`daemonize yes`  后台运行

`pidFile filePath`   pidfile保存进程号

`loglevel notice` 设置日志级别

`logfile `默认为空，可以设置日志输出目录

`database 0` redis有16个库，默认使用0

`requirepass `默认没有密码

`maxmemory ` **必须设置**，否则占满内存，导致服务器宕机

`maxmemory-policy` redis淘汰策略

`maxmemory-samples` 设置样本数量，LRU算法和TTL算法都是估算值，可以设置样本数据



**连接**

`bind ip`  只能通过指定的ip连接

`protected-mode yes` 默认yes，表示远程不能访问

`port 6379` 端口号默认6379

`tcp-backlog 511`  backlog是一个连接队列=已连接和正在连接的总和

`timeout 0` 多久没有操作会断开连接，0表示无限制，单位s

`tcp-keepalive 300`  超时释放tcp连接

`limits 10000` 客户端最大连接数，默认10000





## 发布订阅



消息通知模式

Redis 客户端可以订阅任意数量的频道



**频道**



***订阅频道***

```bash
SUBSCRIBE 频道名
```

订阅后可以获得频道中的所有消息(包括之前发布的)

**向频道中发送消息***

```bash
publish 频道名 消息
```

返回订阅者数量



**先发布，再订阅，也受到了之前发布的消息**





数据的回复与备份



**save**

save命令用来创建当前数据库的备份，会在redis的安装目录下生成dump.rdb文件

**CONFIG GET dir**  用来获得redis的安装目录

只需要讲dump.rdb文件放入redis的安装目录，然后重启redis服务



**bgsave** 也可以创建备份文件



## 管道

**Pipeline**



Redis是一种基于客户端-服务端模型以及请求/响应协议的TCP服务。n个命令需要连接n次Redis

Redis 管道技术可以在服务端未响应时，客户端可以继续向服务端发送请求，并最终一次性读取所有服务端的响应。



**优势**





用来批量操作？





# Jedis

Java客户端，就如MySQL与JDBC的关系。

















* String：二进制安全的，redis 的 string 可以包含任何数据。比如jpg图片或者序列化的对象。
* hash：键值对集合，适合用于存储对象
* List：字符串列表
* Set：字符串的无需列表，通过哈希表实现的，所以添加，删除，查找的复杂度都是 O(1)。
* sorted set，字符串的有序集合

[^优点]: 可持久化，数据类型丰富，支持数据备份，性能高(读写速度100 000左右),Redis的所有操作都是原子性的,支持发布订阅，通知，过期等操作。



命令

连接Redis服务器

要在 redis 服务上执行命令需要一个 redis 客户端

`redis-cli` 连接到本地的redis服务

`redis-cli -h host -p port -a password` 连接远程服务



与键相关的命令

用于管理 redis 的键

| 命令 | 描述 |
| ---- | ---- |
|      |      |
|      |      |
|      |      |



值为字符串的

| 命令                          | 描述                                                  |
| ----------------------------- | ----------------------------------------------------- |
| set key value                 |                                                       |
| setex key second value        |                                                       |
| Psetex key milliseconds value |                                                       |
| setnx key value               |                                                       |
| setrange key offset value     |                                                       |
| mset key value key value      |                                                       |
| Msetnx key value key value    |                                                       |
| incr key                      |                                                       |
| incrby key increment          |                                                       |
| Decr key                      |                                                       |
| Decrby key decrement          |                                                       |
| Append key value              |                                                       |
| get key                       |                                                       |
| getrange key start end        | 获得键对应的值得指定的部分                            |
| mget key...                   | 获得多个key的值                                       |
| getset key value              | 设置新值并返回旧值                                    |
| getbit key offset             | 获得值中指定偏移量的bit，偏移量太大或key不存在时返回0 |
| strlen key                    | 获得字符串的长度                                      |



值为hash

| 命令                               | 描述                          |
| ---------------------------------- | ----------------------------- |
| hset(key filed value )             | key对应一个键值对             |
| hmset(key filed value filed value) | key对应多个键值对             |
| hget(key field)                    | 获得可以中的一个field对应的值 |
| hmget(key field field)             | 获得多个                      |
| hvals key                          | 获得所有值                    |
| Hgetall key                        | 获得所有的键和值              |
| HEXISTS key field                  |                               |
| HINCRBY key field increment        |                               |
| HINCRBYFLOAT key field increment   |                               |
| HKEYS key                          |                               |
| HLEN key                           |                               |
| HSETNX key field value             |                               |
| HDEL key field1 [field2]           |                               |



值为list

| 命令                                  | 描述                                                         |
| ------------------------------------- | ------------------------------------------------------------ |
| LPUSH key value1 [value2]             |                                                              |
| LSET key index value                  |                                                              |
| LPOP key                              | 移出并获取列表的第一个元素                                   |
| BLPOP key1 [key2 ] timeout            | 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止。 |
| RPOP key                              |                                                              |
| BRPOP key1 [key2 ] timeout            |                                                              |
| LPUSHX key value                      | 将一个值插入到已存在的列表头部                               |
| RPUSHX key value                      |                                                              |
| RPOPLPUSH source destination          |                                                              |
| BRPOPLPUSH source destination timeout |                                                              |
| LINDEX key index                      |                                                              |
| LINSERT key BEFOR\|EAFTER pivot value |                                                              |
| LRANGE key start stop                 |                                                              |
| LTRIM key start stop                  | 让列表只保留指定区间内的元素，不在指定区间之内的元素都将被删除 |
| LLEN key                              |                                                              |















监听

与memcache相比的好处

多数据库的设置

持久化

主从复制



连接命令



# 事务



# 持久化

* 流程
* 触发时机



Redis是基于内存的数据库，同时会定期的将数据持久化，保证重启时数据不丢失。

Redis提供类两种持久化方案：RDB和AOF



## RDB

Redis Database

根据指定的时间间隔，将内存中的数据集快照写入磁盘，恢复时是将快照文件直接读取到内存。默认就有rdb的持久化



**备份流程**

Redis会单独fork一个子进程来进行持久化，先将数据写入到一个临时文件中，当持久化过程结束后，用该临时文件替换成上次持久化的文件(dump.rdb)。整个过程中，主进程不进行任何IO操作。



**使用**

配置文件

```
dbfilename dump.rdb
dir ./
# 无法写入磁盘是，关闭redis的写操作
stop-writes-on-bgsave-error yes
rdbcompression yes
# 校验快照，会影响性能
rdbchecksum yes
# 指定时间间隔，至少达到指定次数才会进行持久化
save seconds changes
```



**恢复**

将rdb文件放到dir目录中，文件名为dbfilename



## AOF

Append Only FIle

将每个操作追加到日志中(不记录读操作)，重启后将日志中的命令重新执行一次

**AOF默认不开启**

配置

```
appendonly no
appendfilename "appendlonly.aof"
dir ./
```

同步频率配置

```
# 每次
appendfync alwys
# 每秒
appendfsync everysec
# 不主动同步，把同步时机交给操作系统
appendfsync no
```





**流程**

* 客户端的请求命令被append到AOF缓冲区中
* AOF缓冲区根据持久化策略，将操作sync到磁盘
* AOF文件大小超过重写策略或手动重写时，对AOF文件rewrite重写，压缩AOF文件
* 重启时，重新执行AOF记录的写操作



**rewite压缩**

AOF文件时追加的，文件会越来越大。rewite压缩会对文件中的命令进行筛选，比如进行ABA操作时只会保留一个A，只关注最后的结果。重写也是fork一个子进程进行操作，与rdb类似。

**触发条件：**





**异常恢复**

如果遇到AOF文件损坏，可以通过`redis-check-aof --fix appendonly.aof`进行恢复



## RDB与AOF的对比

* RDB最后一次持久化的数据可能会丢失，适合对一致性不高的情况下
* 两者都开启时，aof文件的优先级高



## **最佳实践**

官方推荐两者一起使用，对数据一致性不敏感可以使用rdb，不建议单独使用aof，而如果只是错纯缓存，两者可以都不用。













































# 主从复制

* 什么时主从复制

* 为什么使用主从复制，优势是什么

* 有什么缺点，要注意什么问题
* 主机挂掉或从机挂掉会有什么后果



**什么是主从复制**

一个master用来进行写操作，master将数据同步到多个slave，slave  用来进行读操作



**为什么要使用主从模式**

* 使用主从复制实现读写分离，提高性能

* 容灾快速恢复



**主服务器和从服务器都可以干什么**





**怎么使用**





缺点：

复制延时



## 搭建



修改配置文件

可以将基础的部分和主从复制的部分分开，然后用include合并

```
port 
pidfile 
dbfilename
```



```

```

`info replication` 查看redis主从复制信息



配置一个一主两从

* 创建3个redis实例
* 登录一个redis，执行`slaveof host port`, 称为一个redis的从服务器
* 相同做法再设置一个redis
* 只能在主机中进行写操作，从机不能进行写操作





**原理，流程**

1. 从服务器连接到住服务器后，发送同步数据的命令，主服务器将当前数据持久化到rdb中发送给从服务器，从服务器读取到数据库中 
2. 主服务器进行写操作后，将数据同步到从服务器中
3. 薪火相传，一个从服务器下还可以再挂从服务器`slaveof host port`,将一个从服务器绑定到另一个从服务器。主服务器只需要同步到一个从服务器，从服务器传递同步数据。
4. 反客为主，主服务器挂掉后，从服务器可以变成主服务器。从服务器执行`slave no one`
5. 



**从服务器出现故障**

从服务器失败重启后，并不会再次加入主服务器，但是数据还在。它现在是一个独立的redis服务，还是需要手动执行`slaveof host prot`来加入主服务器。



**主服务器出现故障**

从服务器挂掉后，重启还是住服务器，从服务器不会进行任何改变，继续等住服务器回来





## 哨兵模式



**什么是哨兵模式**

在主从复制下，主服务器挂掉后，从服务器不会做什么改变，从服务器挂掉后，重启时还需要手动再绑定到主服务器。**为了保证可用性**，在主服务器和从服务器上都加上哨兵来监控，达到自动化控制。

哨兵模式是反客为主的自动版，能够后台监控主机是否故障，故障后根据投票数自动将从服务器变为主服务器，主服务器被设置为从服务器。

哨兵模式已经集成在redis的2.4版本中了，是redis集群的解决方案，sentinel来监视master即它的slave



**原理**

1.  每个Sentinel频率向它所知的Master，Slave以及其他 Sentinel 实例发送一个PING命令。 
2.  如果一个实例（instance）距离最后一次有效回复PING命令的时间超过 own-after-milliseconds 选项所指定的值，则这个实例会被Sentinel标记为主观下线 。
3.  如果一个Master被标记为主观下线，则正在监视这个Master的所有 Sentinel 要确认Master的确进入了主观下线状态。 
4.  当有足够数量的Sentinel（大于等于配置文件指定的值）在指定的时间范围内确认Master的确进入了主观下线状态，则Master会被标记为客观下线。 
5. 



**怎么使用哨兵模式**

1. 新建配置文件：sentinel.conf

```
sentinel monitor mastername host port number
```

* mastername,给master起个名字
* number，至少有number个从服务器的哨兵同意

2. `redis-sentinel 配置文件` 来启动哨兵

 Master-Slave切换后，master_redis.conf、slave_redis.conf和sentinel.conf的内容都会发生改变，即master_redis.conf中会多一行slaveof的配置，sentinel.conf的监控目标会随之调换。 



**sentinel的持久化**





**sentinel集群**





**选举规则**

1. 优先级高的(sentinel配置文件中，slave-priority 100,值越小，优先级越高)
2. 偏移量小的(跟主服务器最相近的)
3. runid(启动后自动生成)小的



**设置优先级**





 



# 集群



* 与主从复制的区别
* 为什么不适用主从复制



**为什么要使用集群**

* 一个服务器存不下所有数据，解决容量不够的问题
* 并发的写操作只能在主服务器上执行



**代理主机？**

客户端不请求redis服务，而是通过代理服务器来请求。



**去中心化配置**

任何一个redis服务器都可以作为集群的入口



**什么是集群**

主从模式是将集群的一份数据往从服务器中复制了多分来实现读写分离。而集群是将一份数据分成多分，存储到多个redis节点，而每一份又可以又多个副本。 一个集群又相当于多个主从复制。

* 将一份数据分成多分

* 将一份数据复制多分



**搭建**

修改配置文件

```
cluster-enabled yes
cluster-config-file xxx
cluster-node-timeout 15000
```

创建六个redis实例

加入集群`redis-cli `





# 主从复制和集群的对比



























