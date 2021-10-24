Redis support

Redis repositories

* 哨兵模式和集群的使用

工厂充当`PersistenceExceptionTranslator`对象



两种连接工厂





RedisConnection



Redis序列化





Java使用的两个Redis客户端：Lettuce，Jedis

## Lettuce



Lettuce is a Netty-based open-source connector

添加maven依赖

```xml
<dependencies>

  <!-- other dependency elements omitted -->

  <dependency>
    <groupId>io.lettuce</groupId>
    <artifactId>lettuce-core</artifactId>
    <version>6.1.5.RELEASE</version>
  </dependency>

</dependencies>
```

**配置luttuce**

```java
@Configuration
class WriteToMasterReadFromReplicaConfiguration {

  @Bean
  public LettuceConnectionFactory redisConnectionFactory() {

    LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
      // 写master读replica
      .readFrom(REPLICA_PREFERRED)
      .build();

    RedisStandaloneConfiguration serverConfig = new RedisStandaloneConfiguration("server", 6379);

    return new LettuceConnectionFactory(serverConfig, clientConfig);
  }
}
```



```java
@Test
void contextLoads() {
  RedisClient client = RedisClient.create("redis://localhost/0");
  StatefulRedisConnection<String, String> connect = client.connect();
  String name = connect.sync().get("name");
  System.out.println(name);
}
```





## Jedis



**添加jedis依赖**

```xml
<dependencies>

  <!-- other dependency elements omitted -->

  <dependency>
    <groupId>redis.clients</groupId>
    <artifactId>jedis</artifactId>
    <version>3.6.3</version>
  </dependency>

</dependencies>
```

**添加配置**

```java
@Configuration
class RedisConfiguration {

  @Bean
  public JedisConnectionFactory redisConnectionFactory() {

    RedisStandaloneConfiguration config = new RedisStandaloneConfiguration("server", 6379);
    return new JedisConnectionFactory(config);
  }
}
```





### Sentinel

```java
/**
 * Jedis
 */
@Bean
public RedisConnectionFactory jedisConnectionFactory() {
  RedisSentinelConfiguration sentinelConfig = new RedisSentinelConfiguration()
  .master("mymaster")
  .sentinel("127.0.0.1", 26379)
  .sentinel("127.0.0.1", 26380);
  return new JedisConnectionFactory(sentinelConfig);
}

/**
 * Lettuce
 */
@Bean
public RedisConnectionFactory lettuceConnectionFactory() {
  RedisSentinelConfiguration sentinelConfig = new RedisSentinelConfiguration()
  .master("mymaster")
  .sentinel("127.0.0.1", 26379)
  .sentinel("127.0.0.1", 26380);
  return new LettuceConnectionFactory(sentinelConfig);
}
```





无论选择哪个客户端，只需要使用一组Spring Data Redis API（在所有连接器上都保持一致）：org.springframework.data.redis.connection包及其RedisConnection和RedisConnectionFactory接口即可使用和检索 与Redis的活动连接。



RedisConnection

getNativeConnection获得连接



## RedisTemplate



RestTemplate是线程安全的，可以被多个实例使用。



**序列化**

RedisTemplate 的大多数操作都要 java 的序列化，所以redis的多有读写操作需要经过Java的序列化和反序列化。

可以更改RedisTemplate的序列化配置

* `org.springframework.data.redis.serializer`中提供了许多其他的序列化器，JdkSerializationRedisSerializer，默认用于 RedisCache 和 RedisTemplate；StringRedisSerializer

* 还可以将任何序列化器设置为 null，并通过将 enableDefaultSerializer 属性设置为 false 来使用带有原始字节数组的 RedisTemplate



因为Redis中key和value的常见类型都是String。SpringDataRedis提供了两个扩展RedisConnection和RedisTemplate用来进行String的操作。



RedisCallback





作为框架的角度，Redis存储的都是二进制，尽管Redis本身支持各种类型，但在大多数情况下，它们是指数据的存储方式，而不是其表示的内容。序列化决定是否转换成String/Object。



### 序列化

SpringData的序列化包`org.springframework.data.redis.serializer`包含两种类型的序列化器

- Two-way serializers based on `RedisSerializer`.
- Element readers and writers that use `RedisElementReader` and `RedisElementWriter`.

主要区别是RedisSerializer主要序列化为byte []，而读写器使用ByteBuffer

也可以使用其他序列化器

- `JdkSerializationRedisSerializer`, which is used by default for `RedisCache` and `RedisTemplate`.
- the `StringRedisSerializer`.
- OxmSerializer for Object/XML mapping through Spring OXM support
- Jackson2JsonRedisSerializer or GenericJackson2JsonRedisSerializer for storing data in JSON format.





SpringData已经注入的StringRedisTemplate





stringRedisTemplate默认使用StringRedisSerializer序列化器；

redisTemplate默认使用JdkSerializationRedisSerializer

配置

```java
序列化与连接管理器
```



StringRedisTemplate

| 操作                          | 描述                            |
| ----------------------------- | ------------------------------- |
| hasKey                        | 是否存在                        |
| keys                          | 获得符合样式的key集合           |
| type                          | key对应的value的类型            |
| rename                        | 重命名key                       |
| delete                        | 删除，可以批量                  |
| expire                        | 设置多久过期                    |
| expireAt                      | 设置过期时间                    |
| getExpire                     | 获得过期时间                    |
| randomKey                     | 随机返回一个key                 |
| dump(K key)                   | 序列化key，返回序列化后的byte[] |
| countExistingKeys(Collection) |                                 |
| move(key,dbIndex)             | 将当前数据库中的key移动到另一个 |

opsForValue

| 操作             | 描述                                  |
| ---------------- | ------------------------------------- |
| set              | 新加/覆盖，可以指定开始位置和超时时间 |
| setIfAbsent      | 不存在时插入，存在时不替换            |
| setIfPresent     | 存在时替换                            |
| multiSet         | 批量新增                              |
| multiSetIfAbsent | 批量                                  |
| append           | String后追加                          |
| decrement        | 自减，默认为1                         |
| increment        | 自增，默认为1，还可以设置double       |
| get              | 获得value或者value的一部分            |
| getAndSet        | 设置新值，返回旧值                    |
| multiGet         | 获得传入的Key集合对应的value          |
| size             | 获得字符串长度                        |





哨兵和集群的支持





原子计数器



排序和管道支持



支持Spring 3.1 cache



@EnableRedisRepositories自定义查询方法





# 使用：



SpringDataRedis需要redis2.6及以上，集成了lettuce和jedis两个redis的Java开源库



**添加依赖**

```xml

```



**配置**



连接：和MySQL一样，通过配置文件配置，如果是本地的，可以不写配置文件使用默认配置

```yml

spring:
  redis:
    port: 6379
    password: xxx
    host: xxx
    lettuce:
      pool:
        max-active: 100 # 连接池最大连接数（使用负值表示没有限制） 太小可能出现connection.PoolExcelption
        max-idle: 8 # 连接池中的最大空闲连接
        min-idle: 0 # 连接池中的最小空闲连接
        max-wait: 1000 # 连接池最大阻塞等待时间（使用负值表示没有限制）
```



**配置RedisTemplate**



RedisTemplate

RedisTemplate提供了redis各种操作、异常处理及序列化，支持发布订阅，spring 3.1 cache。



StringRedisTemplate与RedisTemplate

* StringRedisTemplate继承RedisTemplate
* ReidsTemplate会将数据序列化成字节数组存到redis中，StringRedisTemplate存的是字符串



advance



使用

默认实现了StringRedisTemplate，Value的值是String，

使用RedisTemplate进行操作，value时Object，会进行序列化和反序列化。

两者的序列化策略不同，所以数据是不通用的。



配置RedisTemplate

```java
// 需要配置连接工厂，序列化可以使用默认JDK提供的，或自定义键和值得序列化器

```



**opsForXXX**

选择操作的Redis的类型

除了五种数据类型外



Redis Stream 主要用于消息队列（MQ，Message Queue），Redis 本身是有一个 Redis 发布订阅 (pub/sub) 来实现消息队列的功能，但它有个缺点就是消息无法持久化，如果出现网络断开、Redis 宕机等，消息就会被丢弃。

Redis GEO 主要用于存储地理位置信息





**execute**





集群



读写分离

Write to Master, Read from Replica



Pub/SUb



# Lettuce与Jedis的区别



# Redis Cluster



连接集群

RedisClusterConnection, an extension to RedisConnection, handles the communication with the Redis Cluster and translates errors into the Spring DAO exception hierarchy。

RedisClusterConnection instances are created with the RedisConnectionFactory, which has to be set up with the associated RedisClusterConfiguration









































