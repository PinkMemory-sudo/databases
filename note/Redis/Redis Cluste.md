* 为什么至少三个节点
* 哈希槽，新增删除节点





集群的搭建测试和操作



redis集群需要两个端口：6370，16379

6370用于与客户机的通信，16379是集群的总线(使用二进制协议节点到节点的通信)用于故障检测，配置更新，故障转移授权等。

命令端口和集群总线端口偏移量是固定的，始终为10000







## 创建redis集群多分片多副本

1. In order to make Docker compatible with Redis Cluster you need to use the **host networking mode** of Docker.(--net=host)

2. --cluster-enabled yes支持集群
3. 启动redis
4. 执行创建集群的命令









**分片**

使用哈希槽而不是一致性hash进行分片，每个节点分一部分哈希槽。

There are 16384 hash slots in Redis Cluster, and to compute what is the hash slot of a given key, we simply take the CRC16 of the key modulo 16384.

新增节点时将其他节点的哈希槽移动到新节点，删除节点时将节点的哈希槽移动到其他节点，删除空节点。

移动哈希槽不需要停工

Redis Cluster supports multiple key operations as long as all the keys involved into a single command execution (or whole transaction, or Lua script execution) all belong to the same hash slot. The user can force multiple keys to be part of the same hash slot by using a concept called *hash tags*.





## 主从模式



**读写分离**

写操作通过主节点，然后主节点将操作复制给从节点

从操作准们用来进行读操作



**容错**

筒仓采用一主多从的配置，一个从节点错误后可以从其他从节点读。



**配置**



/etc/redis.conf

appendonly

redis-server 配置文件

启动多个redis 

通过redis客户端命令 

info replication查看节点信息

slaveof  主节点host 主节点port将当前节点设置为指定节点的从节点，主节点有密码时，要在配置文件中设置masterauth



从服务器挂掉后

重启变成了主服务器，需要重新指定主服务器



主服务器挂掉后

主服务器重启后还是主服务器



**主从复制原理**

从服务器连接到主服务器后，主服务器会对数据进行持久化生成rdb文件交给从服务器(全量复制)

从服务器在进行添加操作后会将数据同步到从服务器(增量复制)



一个从服务器下可以挂它自己的从服务器，主服务器挂掉后可以通过命令(slaveof no one)让该服务器做住服务器

主服务器只会同步到一个从服务器中，然后由该从服务器同步到其他从服务器



## 哨兵模式



不用再通过手动命令将从节点变成主节点



主从模式来保证在主节点失败时仍然可用，一个主节点发生问题时会提升它的从节点(副本)作为新的主节点

旧主节点重启后悔变成新主节点的从节点



设置：

哨兵配置文件：sentinel.conf

启动哨兵命令：redis-sentinel 配置文件

```
port：26379 #哨兵端口号 
daemonize yes  #后台启动
sentinel monitor mymaster 主节点ip 主节点端口 2
```





选举规则：

* 优先级(redis.conf的 xxx-priority)
* 偏移量大的优先
* runid最小的











# 主从复制



## 哨兵模式





# Redia-cluster



**参考**：

从用户角度描述redis-cluster的行为：[Redis cluster tutorial](https://redis.io/topics/cluster-tutorial)

Redis-cluster算法和设计的细节：[Redis cluster specification](https://redis.io/topics/cluster-spec)



## BasicConcepts



**为什么使用cluster，主从复制不行吗**

* 一个节点存不下整个数据，redis自动拆分，存储到多个节点上去
* 提高并发写的性能，主从复制只能在主节点(只有一个)上写
* cluster相当于多个主从复制组合起来，一个主从复制负责一部分hash槽



**redis需要两个tcp端口**

* 一个端口用来接收客户端请求，如6379
* 一个客户端作为cluster的bus port进行节点间的通信、故障检测、配置更新等。一般再加上10000，如16379
* 确保防火墙开放这两个端口



**Data sharding**

Redis集群中有16384个hash槽，集群中每个节点负责一部分hash槽。根据key计算出它所属的hash槽，找到对应的节点。可以通过重新分配hash槽来添加或删除节点，并且不需要停机。



**hash tags**

`if there is a substring between {} brackets in a key, only what is inside the string is hashed`

hashtags允许用key的子串来精算hash槽，当一个key包含{}时，就只是有{}中的字符串进行hash计算{可以修改hash_tag配置，使用其他标记}。hash tags用来保证多个key被分配到同一个hash槽中，以此来保证与mset，keys类似的多key操作。



**Redis Cluster master-replica model**

一个主节点失败后，从节点可以晋升成主节点，主节点和它的从节点都失败时，集群无法再继续进行。



**Redis Cluster consistency guarantees**

集群无法保证强一致性，集群是异步写的，客户端发送写操作到主节点，主节点回复OK，并且同步到它的副本，而不是同步到副本后再回复客户端。这时候主节点失败，没有同步到副本，副本晋升成主节点，写的这部分数据就丢失了。

通常需要在一致性和性能之间做出一个权衡。

Redis 集群支持在需要时进行同步写操作，通过 WAIT 命令实现。这使得写失败的可能性大大降低。但是，即使使用同步复制，Redis Cluster 也不能实现强一致性。

`network partition 由于网络设备的failure，造成网络分裂为多个独立的组`,出现network partition时，集群分成两半，一般可达，客户在一办进行写操作，当网络恢复时，另一半由于主节点超时，又选出一个主节点，此时主节点会变成从节点，客户写的数据就丢失了。



**集群参数配置**

```
# no表示作为独立的实例启动
cluster-enabled yes
# 这不是一个用户可编辑的配置文件，持久化集群配置有变化会自动刷新
cluster-config-file <filename>
cluster-node-timeout <milliseconds>
# 默认yes，no时，如果hash槽不完整集群仍然可进行读操作
cluster-require-full-coverage <yes/no>
```



## 手动搭建Redis-cluster

* 需要redis3.0及以上的版本



1. **创建6个redis实例**

*创建6个文件夹*

```bash
mkdir cluster-test/{7000,7001,7002,7003,7004,7005}
```

每个目录下创建一个redis.conf文件

首先需要运行一些空的 Redis 实例(集群模式，而不是普通模式，来开启redis的集群特性和命令)

```
port 7000
cluster-enabled yes
cluster-config-file nodes.conf
cluster-node-timeout 5000
appendonly yes
```

**cluster-enable**只是启用了redis的集群命令

**cluster-config-file**该节点的配置文件路径，启动时由redis集群生成，并由集群更新等

Note that the **minimal cluster** that works as expected requires to contain at least three master nodes.

再加上三个从节点，组成一个六节点的集群



*启动六个实例*

```
cd 7000
../redis-server ./redis.conf
```

第一次启动没有nodes.conf 文件，每个节点都为自己分配一个新的 ID。

```
[82462] 26 Nov 11:56:55.329 * No cluster configuration found, I'm 97a3a64667477371c4479320d683e4c8db5858b1
```

集群根据ID来标识，节点的ip和端口会变，但是ID不会，这个 ID 将被这个特定实例永远使用，以便该实例在集群的上下文中具有唯一的名称。



2. **用启动的redis的host和port创建集群**

现在只是有一些运行的redis实例了，而我们要通过向节点中写入一些配置来创建我们的集群。

redis5以上可以通过redis-cli来写入配置、检查或重新分割现有集群等等

```bash
redis-cli --cluster create 127.0.0.1:7000 127.0.0.1:7001 \
127.0.0.1:7002 127.0.0.1:7003 127.0.0.1:7004 127.0.0.1:7005 \
--cluster-replicas 1
```

* create 命令来创建集群，
* --cluster-replicas 1设置从节点的数量
* redis自动分配主服务器和从服务器，主服务器和从服务器会尽量保证不再一台机器上

6. 运行成功的结果

```
[OK] All 16384 slots covered
```



3. **连接集群**

集群是去中心化配置，可以根据任何节点连接到集群(加上-c参数)

`redis-cli -c -h host -p port`





## create-Cluster脚本创建Redis集群



reids的utils/create-cluster 目录下的create-cluster脚本

```bash
create-cluster start
create-cluster create
```

第一个节点默认从端口30001开始



## 用docker搭建redis集群



`in order to make Docker compatible with Redis Cluster you need to use the **host networking mode** of Docker.`

 redis 5.0 版本以后，就只能通过 redis-cli 来实现。



 因为redis集群的节点选举方式是需要半数以上的master通过，所以建议创建奇数个节点 ，集群中至少有奇数个节点，所以至少是三个节点，每个节点再配置一个副本，一个集群就至少需要6个redis实例。



通过一个节点，将其他节点加入集群(PING-PONG)

通过一个节点新建一个节点，两个节点握手成功后，通过Gossip协议添加到其他节点



1. 开启节点
2. 添加到集群
3. 分配槽(槽都被分配后集群才是上线状态，有槽下线则集群就会出于下线状态)，每个节点负责一段哈希槽
4. 添加从节点



## 新增或删除节点



就是修改一下节点负责的哈希槽，每个节点负责的哈希槽不必须连续，并且在改变哈希槽的时候不影响使用

哈希槽可以是不连续的，



使用哈希槽的局限性：

* 同一哈希槽的key才能进行批量操作
* 不支持多数据库，单机下的Redis可以支持16个数据库，但集群之只能使用一个数据库空间，即db 0



**常用命令**

| 命令                          | 描述                  |
| ----------------------------- | --------------------- |
| cluster keyslot key           | 获得key的slot值       |
| cluster countkeysinslot  hash | 查看hash槽中key的个数 |
|                               |                       |



**一次添加多个key**

一次添加多个key

*分组：*将多个key分成一组，根据组名去计算属于哪个hash槽



**不同节点之间的操作**

每个节点只能看当前节点插槽范围内的数据，那它跟用三个主从模式又什么区别



**故障恢复**

* 主机挂掉又恢复，会发生什么变化

* 一个主机和他的从机都挂掉时，集群还能不能工作？





# 常用命令



查看主节点

redis-cli -p 任意节点端口号 cluster nodes | grep master

redis-cli里面的话cluster nodes | grep master



将此节点的哈希槽移动到另一个节点

```bash
redis-cli --cluster reshard host:port
```

随后输入移动的哈希槽的数量

```
How many slots do you want to move (from 1 to 16384)?
```



获得某个节点的ID

```bash
redis-cli -p 7001 cluster nodes | grep myself
```



查看节点状态

```bash
redis-cli --cluster check 127.0.0.1:7001
```



添加节点(主)

```bash
redis-cli --cluster add-node host:port列表
```

然后给他分哈希槽



添加从节点

```bash
redis-cli --cluster add-node  host:port列表 --cluster-slave
```

redis-cli会将从节点分配给一个从节点，也可以指定添加给哪个从节点：

```bash
redis-cli --cluster add-node  host:port列表 --cluster-slave --cluster-master-id 主节点id
```

还有一种方法是添加一个空的主节点，进入空节点的redis-cli,然后该节点变成另一个主节点的从节点

```bash
redis 127.0.0.1:7006 cluster replicate 主节点id
```

该方法也适用于将一个从节点变成另一个主节点的从节点



删除节点

进入任意一个节点中，通过del-node命令删除

```bash
redis-cli --cluster del-node 127.0.0.1:7000 `<node-id>`
```

如果删除主节点时注意要清空哈希槽



设置集群密码



```bash
docker run --net=host -d --name rc6 -v /Users/chenguanlin/Documents/workspace/redis/redis-cluster-test/7006:/conf 7eed8df88d3b redis-server /conf/redis.conf

redis-cli --cluster create 127.0.0.1:7006 127.0.0.1:7001 \
127.0.0.1:7002 127.0.0.1:7003 127.0.0.1:7004 127.0.0.1:7005 \
--cluster-replicas 1
```











