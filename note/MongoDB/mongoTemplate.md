# 使用

1. 添加依赖

2. 配置数据源

3. 配置日志(可选)

   ```properties
   log4j.category.org.springframework.data.mongodb=DEBUG
   log4j.appender.stdout.layout.ConversionPattern=%d{ABSOLUTE} %5p %40.40c:%4L - %m%n
   ```

4. 自动注入mongoTemplate`MongoOperations mongo`,面向接口编程



***类型映射***

document和实体类之间的映射委托给了`MongoConverter`。mongoTemplate实现的转换器叫做`MappingMongoConverter`,还可以自定义映射(参考官网)

***_id的映射***

* 用@Id(org.springframework.data.annotation.Id)注解的属性会映射成_id
* 叫id的属性会映射成_id，String，BigInteger，ObjectId会转换成ObjectId，不能转换成ObjectId的会转换成String类型的\_Id。

* id为String/BigInteger时，会映射成ObjectId存储到mongo中.如果不能映射，会存储成String；如果类中没有指定id，mongo会生成的id不会进行映射。

选做

* 设置`WriteResultChecking`

进行操作返回的`com.mongodb.WriteResult`包含有错误时，我们可以做记录或抛异常，但在开发中很容易忘记，使得有错误但被当成成功来处理。我们可以设置mongoTemplate的`WriteResultChecking`为`EXCEPTION` or `NONE`,默认为NONE，不做任何处理，有我们自己处理。`EXCEPTION`表示出错时直接跑异常。

* `WriteConcern`

写入策略是指当客户端发起写入请求后，数据库什么时候给应答，mongodb有三种处理策略：客户端发出去的时候，服务器收到请求的时候，服务器写入磁盘的时候

* `WriteConcernResolver`

如果需要给增删改查设置不同的写入策略，

# 常用添加

| 方法      | 参数                                                         |
| --------- | ------------------------------------------------------------ |
| insert    | 可添加一个docoment或者用集合来批量插入，记得用Class指定映射类型 |
| insertAll |                                                              |
| save      |                                                              |

添加文档时，不完全限定类名就是集合名，可以在实体类上用@Document指定集合名。

# 常用查询

| 方法           | 描述               |
| -------------- | ------------------ |
| count          | 符合条件的文档个数 |
| estimatedCount | 不带条件，文档个数 |
| find           |                    |

查询长用到两个对象：Query和Criteria

用Query封装criteria, projection, sorting and query,criteria封装条件

Criteria作为条件，Criteria的链式调用的关系之间都是and的关系，它的orOperator方法中客串如多个Criteria表示或的关系。

***获得Query***

1. 直接new Query()
2. 通过Query的静态方法query(Criteria),返回带天健的query

***Query中添加条件***

1. 通过Query的静态方法query传入
2. 通过Query的addQuery添加

***添加分页或排序条件***

通过Query的with方法添加

1. 排序传入Sort对象
2. 分页传入Pageable对象

获得Sort对象：Sort.by(Order对象),可通过Order的静态方法，可传入多个Order对象

获得Pageable对象：PageRequest.of(int,int)



# 常用更新



| 方法                      | 描述                                                    |
| ------------------------- | ------------------------------------------------------- |
| findAndModify             | 找到更新返回更新前的document，找不到返回null            |
| fAM  FindAndModifyOptions | 传入findAndModify的额外条件，有返回新值，upsert，remove |
| indAndReplace             | 同样                                                    |
| findAndRemove             | 同样                                                    |
| upsert                    |                                                         |
| updateFirst               |                                                         |
| updateMulti               |                                                         |

更新主要包含两个类：Query和UpdateDefinition。

UpDate实现了UpdateDefinition

Update用来添加更新内容

| 方法                                         | 描述     |
| -------------------------------------------- | -------- |
| update(String key,  Object value)            | 静态方法 |
| set(String key, Object value)                |          |
| **addToSet** `(String key, Object value)`    |          |
| **currentDate** `(String key)`               |          |
| **currentTimestamp** `(String key)`          |          |
| **inc** `(String key, Number inc)`           |          |
| inc(String key)                              | 自增1    |
| **max** `(String key, Object max)`           |          |
| **min** `(String key, Object max)`           |          |
| unset(String key)                            |          |
| push(String key, Object value)               |          |
| **pullAll** `(String key, Object[] values)`  |          |
| **pushAll** `(String key, Object[] values)`  |          |
| **setOnInsert** `(String key, Object value)` |          |



Document，将JSON转为BSON

and与or

大括号里逗号隔开的几个条件都是and的关系

$or:[]数组中的关系都是or的关系；

and与or连用：

{ "class" : 1 }, "$or" : [{ "logic" : "simple" }, { "logic" : { "$exists" : false } }] }

表示class为1并且logic为simple或没有的情况



# 执行Mongo shell命令

`Document executeCommand(String jsonCommand);`

`Document executeCommand(Document command);`

`Document executeCommand(Document command, @Nullable ReadPreference readPreference);`

`void executeQuery(Query query, String collectionName, DocumentCallbackHandler dch);`

`<T> T execute(DbCallback<T> action);`

`<T> T execute(Class<?> entityClass, CollectionCallback<T> action);`

`<T> T execute(String collectionName, CollectionCallback<T> action);`

`SessionScoped withSession(ClientSessionOptions sessionOptions);`



***获得查询结果的stream***

`<T> CloseableIterator<T> stream(Query query, Class<T> entityType);`

`<T> CloseableIterator<T> stream(Query query, Class<T> entityType, String collectionName);`



# 集合操作



***创建集合***

可以自动创建集合，但有时候要创建固定大小的集合

`<T> MongoCollection<Document> createCollection(Class<T> entityClass);`

` createCollection(Class<T> entityClass, CollectionOptions collectionOptions);`

`MongoCollection<Document> createCollection(String collectionName);`

`createCollection(String collectionName, CollectionOptions collectionOptions);`

eg: 创建固定大小的集合

```.java
CollectionOptions collectionOptions = CollectionOptions.empty().capped().size(100L).maxDocuments(100L);
mongo.createCollection("test", collectionOptions);
```



***获得集合***

`MongoCollection<Document> getCollection(String collectionName);`



***获得所有集合名***

`Set<String> getCollectionNames();`



***集合是否存在***

`<T> boolean collectionExists(Class<T> entityClass);`

`boolean collectionExists(String collectionName);`



***删除集合***

`<T> void dropCollection(Class<T> entityClass);`

`void dropCollection(String collectionName);`



获得集合的所有文档

`<T> List<T> findAll(Class<T> entityClass);`

`<T> List<T> findAll(Class<T> entityClass, String collectionName);`



# index操作



***获得Index***

`IndexOperations indexOps(String collectionName);`

`IndexOperations indexOps(Class<?> entityClass);`



# 批量操作



获得批量操作,就是一次对同一个集合进行多个增删改操作，操作可以不同，条件可以不同。

`BulkOperations bulkOps(BulkMode mode, String collectionName);`没指定类型，不会进行字段映射，不推荐

`BulkOperations bulkOps(BulkMode mode, Class<?> entityType);`

`BulkOperations bulkOps(BulkMode mode, @Nullable Class<?> entityType, String collectionName);`

***使用***

1. 获得BulkOperations
2. 添加多个操作或者list of similar operations
3. 执行操作，返回结果



# 聚合操作

聚合操作返回结果可以看成是集合(实现了Iterable)，泛型是我们自己指定的。

`<O> AggregationResults<O> aggregate(TypedAggregation<?> aggregation, String collectionName, Class<O> outputType);`

`<O> AggregationResults<O> aggregate(TypedAggregation<?> aggregation, Class<O> outputType);`

`<O> AggregationResults<O> aggregate(Aggregation aggregation, Class<?> inputType, Class<O> outputType);`

`<O> AggregationResults<O> aggregate(Aggregation aggregation, String collectionName, Class<O> outputType);`

`<O> CloseableIterator<O> aggregateStream(TypedAggregation<?> aggregation, String collectionName, Class<O> outputType);`

`<O> CloseableIterator<O> aggregateStream(TypedAggregation<?> aggregation, Class<O> outputType);`

`<O> CloseableIterator<O> aggregateStream(Aggregation aggregation, Class<?> inputType, Class<O> outputType);`

`<O> CloseableIterator<O> aggregateStream(Aggregation aggregation, String collectionName, Class<O> outputType);`

**Aggregation**

来装AggregationOperation

**AggregationOperation**

通过Aggregation的构造方法获得GroupOperation、等AggregationOperation



**TypedAggregation**

特殊的TypedAggregation，提供了输入类型



**AggregationResults**









# map-reduce

`<T> MapReduceResults<T> mapReduce(String inputCollectionName, String mapFunction, String reduceFunction, Class<T> entityClass);`

` <T> MapReduceResults<T> mapReduce(String inputCollectionName, String mapFunction, String reduceFunction, @Nullable MapReduceOptions mapReduceOptions, Class<T> entityClass);`

`<T> MapReduceResults<T> mapReduce(Query query, String inputCollectionName, String mapFunction, String reduceFunction, Class<T> entityClass);`

`<T> MapReduceResults<T> mapReduce(Query query, String inputCollectionName, String mapFunction, String reduceFunction,@Nullable MapReduceOptions mapReduceOptions, Class<T> entityClass);`

需要用Js写map函数和reduce函数



# 会话

`SessionScoped withSession(ClientSessionOptions sessionOptions);`

`MongoOperations withSession(ClientSession session);`



# 分组

group command has been removed in MongoDB Server 4.2.0



# NearQuery

eval command has been removed in MongoDB Server 4.2.0.

