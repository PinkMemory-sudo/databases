



MongoTemplate



使用com.mongodb.client.MongoClient和数据库名来实例化SpingData Mongo的核心帮助类MOngoTemplate。



连接Mongo

***需要两个东西***：

​		client和database



创建client

根据com.mongodb.client.MongoClient和数据库名来创建MongoTemplate来操作Mongo，client指出host，再加上数据库进行连接

* 直接创建client

```java
MongoClients.create("mongodb://localhost:27017");
```

* 通过factory来创建

```java
MongoClientFactoryBean mongo = new MongoClientFactoryBean();
mongo.setHost("localhost");
```

使用factory创建client的好处就是他为容器提供了一个`ExceptionTranslator`实现(将MongoDB异常转换为Spring的)









MongoClients.create("mongodb://localhost:27017");，

也是SpringData下的项目，支持持久层接口的自动实现

low-level API 

id会存储为ObjectId

使用MongoDB的第一步是创建com.mongodb.client.MongoClient，

```java
@Configuration
public class AppConfig {

  /*
   * Use the standard Mongo driver API to create a com.mongodb.client.MongoClient instance.
   */
   public @Bean MongoClient mongoClient() {
       return MongoClients.create("mongodb://localhost:27017");
   }
}
```



```java
@Configuration
public class AppConfig {

    /*
     * Factory bean that creates the com.mongodb.client.MongoClient instance
     */
     public @Bean MongoClientFactoryBean mongo() {
          MongoClientFactoryBean mongo = new MongoClientFactoryBean();
          mongo.setHost("localhost");
          return mongo;
     }
}
```



使用MongoClientFactoryBean的好处是它添加了ExceptionTranslator的一个实现(将MongoDB异常转换为Spring的可移植DataAccessException层次结构中的异常)



com.mongodb.client.MongoClient是MongoDB driver API的入口，连接到MongoDB还需要数据库名，用户名和密码，之后可以获得com.mongodb.client.MongoDatabase，可以使用这个数据库的所有功能。



org.springframework.data.mongodb.core.MongoDatabaseFactory

```
public class MongoApp {

  private static final Log log = LogFactory.getLog(MongoApp.class);

  public static void main(String[] args) throws Exception {

    MongoOperations mongoOps = new MongoTemplate(new SimpleMongoClientDatabaseFactory(MongoClients.create(), "database"));

    mongoOps.insert(new Person("Joe", 34));

    log.info(mongoOps.findOne(new Query(where("name").is("Joe")), Person.class));

    mongoOps.dropCollection("person");
  }
}
```



```
@Configuration
public class MongoConfiguration {

  public @Bean MongoDatabaseFactory mongoDatabaseFactory() {
    return new SimpleMongoClientDatabaseFactory(MongoClients.create(), "database");
  }
}
```

MongoTemplate是线程安全的

MongoOperations接口

MongoTemplate实现了MongoOperations接口。



documents和entity之间的映射由MongoConverter接口的实现来完成，Spring提供了MappingMongoConverter



mongo会话

mongo事务





Type Mapping

集合的名字取决于类名(小写)，MongoDB collections可以存储各种类型的实例,

可以使用@Document自定义集合名，或者insert/save时最后一个参数指定集合名





***update***

|                                         |                      |
| --------------------------------------- | -------------------- |
| set(String key, Object value)           |                      |
| setOnInsert(String key, Object value)   | 文档不存在则插入     |
| unset(String key)                       | 删除文档中指定字段   |
| inc(String key, Number inc)             | 自增inc              |
| inc(String key)                         | 自增1                |
| push(String key,  Object value)         | 添加指定的值到数组中 |
| pushAll(String key, Object[] values)    | 不再支持             |
| addToSet(String key, Object value)      | 重复时不添加         |
| pop(String key, Position pos)           |                      |
| pull(String key,  Object value)         |                      |
| pullAll(String key, Object[] values)    |                      |
| rename(String oldName, String newName)  | 重命名               |
| currentDate(String key)                 | 更新到嘴型Date       |
| currentTimestamp(String key)            | 更新至最新时间戳     |
| multiply(String key, Number multiplier) | 当前值相乘           |
| min(String key, Object value)           | 跟新成小的那个       |
| max(String key, Object value)           |                      |
|                                         |                      |



***query***



***upsert***

​		Related to performing an updateFirst operation, you can also perform an “upsert” operation, which will perform an insert if no document is found that matches the query. The document that is inserted is a combination of the query document and the update document.

`upsert does not support ordering. Please use findAndModify to apply Sort`





***Criteria***

|                                   |                         |
| --------------------------------- | ----------------------- |
| static Criteria where(String key) | 用给定的key创建Criteria |
|                                   |                         |
|                                   |                         |







批量插入

封装成一个集合作为第一个参数



更新

更新第一个updateFirst

更新所有updateMulti



update不支持排序

 findAndModify支持



查询

使用Query和Criteria来描述查询，

* 有着和MongoDB一样的操作符名，例如：lt, lte, is。

* 可以链式调用

BasicQuery

使用JSON的方式来查询



* MongoTemplate,封装了对mongo的常见操作，包括对象与文档之间的映射
* 注解
* 生命周期事件
* 基于Java构建查询，更新等
* 自动实现接口
* 回调方法返回MongoDatabase，直接与Mongo交互
* 额外的元数据

































## MongoClient



创建客户端，即指明Mongo服务在哪。

1. 配置文件中指明mongo在哪，然后可以直接自动注入MongoClient

```properties
spring.data.mongodb.host=localhost
spring.data.mongodb.port=27017
spring.data.mongodb.database=mydb
```

2. 将MongoClient，注入到Sping容器中

```java
@Configuration
public class AppConfig {

  /*
   * Use the standard Mongo driver API to create a com.mongodb.client.MongoClient instance.
   */
   public @Bean MongoClient mongoClient() {
       return MongoClients.create("mongodb://localhost:27017");
   }
}
```

3. 通过MongoClientFactoryBean创建MongoClient，好处是将mongo的异常转移到持久层(@Repository)

```java
@Configuration
public class AppConfig {

    /*
     * Factory bean that creates the com.mongodb.client.MongoClient instance
     */
     public @Bean MongoClientFactoryBean mongo() {
          MongoClientFactoryBean mongo = new MongoClientFactoryBean();
          mongo.setHost("localhost");
          return mongo;
     }
}
```





```properties

```



#### WriteResultChecking Policy

从MongoDB操作返回的WriteResult包含一个错误，记录日志获/抛出错误时很方便的。WriteResultChecking有两个取值：EXCEPTION/NONE，MongoTemplate默认是NONE即什么也不错，但这样当数据库没有按照期望进行修改时，应用程序看起来也已经成功运行了。



#### WriteConcern

如果未设置WriteConcern属性，则默认为MongoDB驱动程序的DB或集合设置中的设置。



### 查询

Query对象，用来表示查询条件(criteria),(projection),(sorting),(query hints)



添加查询条件：

1. 通过构造方法Query(CriteriaDefinition criteriaDefinition)
2. 通过静态方法query(CriteriaDefinition criteriaDefinition)
3. 通过addCriteria(CriteriaDefinition criteriaDefinition)



其他常用方法

| 方法                          | 描述               |
| ----------------------------- | ------------------ |
| Query skip(long skip)         | 跳过，即从哪开始   |
| Query limit(int limit)        | 到哪结束           |
| Query with(Pageable pageable) | 会转换成skip+limit |
| Query with(Sort sort)         |                    |

#### 分页查询

### 批量操作



### Type Mapping

Mongo中的集合可以包含任何文档，并不是只能包含一个类。

Mongo的集合中会用_class,来存储全类名来判断类型。

异常转换







# 动态查询

















































