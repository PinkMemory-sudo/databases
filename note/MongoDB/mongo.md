





## 集合操作



对于修改系统集合中的对象有如下限制。

在{{system.indexes}}插入数据，可以创建索引。但除此之外该表信息是不可变的(特殊的drop index命令将自动更新相关信息)。

{{system.users}}是可修改的。 {{system.profile}}是可删除的。

集合，是一组文档的集合，一般把又关联性的放在一个集合中，当第一个文档插入时，集合就会被创建，所以，没有数据的集合不会显示。

| 操作                       | 命令                    |
| -------------------------- | ----------------------- |
| 查看当前数据库中的所有集合 | show tables/collections |
| 创建集合                   | 插入是自动创建          |
| 删除集合                   | db.collection.drop()    |

​	

## 文档操作

文档就是最小的存储单位，所以文档中的字段可以是不相同的。

文档用BSON存储(二进制的JSON，遍历更高效，Binary JSON 的简称)

文档中的键值对是有序的

文档中的键可以使用任意UTF-8字符

文档中的值可以是常用数据类型和文档



## MongoDB 数据类型

| 编号 | 数据类型           | 描述                                                         |
| ---- | :----------------- | :----------------------------------------------------------- |
| 2    | String             | 字符串。存储数据常用的数据类型。在 MongoDB 中，UTF-8 编码的字符串才是合法的。 |
|      | Integer            | 整型数值。用于存储数值。根据你所采用的服务器，可分为 32 位或 64 位。 |
| 8    | Boolean            | 布尔值。用于存储布尔值（真/假）。                            |
| 1    | Double             | 双精度浮点值。用于存储浮点值。                               |
|      | Min/Max keys       | 将一个值与 BSON（二进制的 JSON）元素的最低值和最高值相对比。 |
| 4    | Array              | 用于将数组或列表或多个值存储为一个键。                       |
| 17   | Timestamp          | 时间戳。记录文档修改或添加的具体时间。                       |
| 3    | Object             | 用于内嵌文档。                                               |
| 10   | Null               | 用于创建空值。                                               |
| 14   | Symbol             | 符号。该数据类型基本上等同于字符串类型，但不同的是，它一般用于采用特殊符号类型的语言。 |
| 9    | Date               | 日期时间。用 UNIX 时间格式来存储当前日期或时间。你可以指定自己的日期时间：创建 Date 对象，传入年月日信息。 |
| 7    | Object ID          | 对象 ID。用于创建文档的 ID。                                 |
| 5    | Binary Data        | 二进制数据。用于存储二进制数据。                             |
| 13   | Code               | 代码类型。用于在文档中存储 JavaScript 代码。                 |
| 11   | Regular expression | 正则表达式类型。用于存储正则表达式。                         |
| 6    | Undefined          |                                                              |



### Object ID

Object ID，**必须包括的，可以是任何类型，默认是ObjectId对象** 类似唯一主键，可以很快的去生成和排序，包含 12 bytes，含义是：

​		前四个字节是时间戳，接着3个是机器识别码，下面的两个字节由进程 id 组成 PID，最后三个字节是随机数。

所以不用在文档里存储时间戳，可以通过 getTimestamp 函数来获取文档的创建时间。newObject.getTimestamp()

​		*BSON 时间戳类型主要用于 MongoDB 内部使用。在大多数情况下的应用开发中，你可以使用 BSON 日期类型。*



***根据数据类型查找***

MongoDB的数据类型有编号，使用$type来构造查询

```
{"title" : {$type : 2}}
```

表示查找title的类型是String，也可以使用：

```
{"title" : {$type : 'string'}}
```



## 索引





**为什么用索引？**

索引用来提高查询的效率，没有索引，mongo查找数据时就必须进行全表扫描来匹配查询。而存在合适的索引时，就会减少扫描的数量。



**什么是索引**

所以是一个特殊的数据结构，实际就是document中的一个或多个字段组合成的值与document的映射，然后将这些值排序，从而提高查询效率。



***创建索引***



`db.collection.createIndex({字段:1/-1}, options)`

MongoDB indexes use a B-tree data structure

keys指定索引的字段，options为1或-1(升序/降序)，可设置多个索引和**有聚合索引**

createIndex() 接收可选参数，可选参数列表如下：

| Parameter          | Type          | Description                                                  |
| :----------------- | :------------ | :----------------------------------------------------------- |
| background         | Boolean       | 建索引过程会阻塞其它数据库操作，background可指定以后台方式创建索引，即增加 "background" 可选参数。 "background" 默认值为**false**。 |
| unique             | Boolean       | 建立的索引是否唯一。指定为true创建唯一索引。默认值为**false**. |
| name               | string        | 索引的名称。如果未指定，MongoDB的通过连接索引的字段名和排序顺序生成一个索引名称。 |
| dropDups           | Boolean       | **3.0+版本已废弃。**在建立唯一索引时是否删除重复记录,指定 true 创建唯一索引。默认值为 **false**. |
| sparse             | Boolean       | 对文档中不存在的字段数据不启用索引；这个参数需要特别注意，如果设置为true的话，在索引字段中不会查询出不包含对应字段的文档.。默认值为 **false**. |
| expireAfterSeconds | integer       | 指定一个以秒为单位的数值，完成 TTL设定，设定集合的生存时间。 |
| v                  | index version | 索引的版本号。默认的索引版本取决于mongod创建索引时运行的版本。 |
| weights            | document      | 索引权重值，数值在 1 到 99,999 之间，表示该索引相对于其他索引字段的得分权重。 |
| default_language   | string        | 对于文本索引，该参数决定了停用词及词干和词器的规则的列表。 默认为英语 |
| language_override  | string        | 对于文本索引，该参数指定了包含在文档中的字段名，语言覆盖默认的language，默认值为 language. |



## 聚合

集合就是

先进行分组/过滤/截断

再进行统计，如最值，平均值，分组求和等聚合操作

可重复操作

更通俗说，就是先获得管道，再对管道中的内容进行聚合操作

```
db.COLLECTION_NAME.aggregate(AGGREGATE_OPERATION)
```



***管道常用方法***

- $project：修改输入文档的结构。可以用来重命名、增加或删除域，也可以用于创建计算结果以及嵌套文档。
- $match：用于过滤数据，只输出符合条件的文档。$match使用MongoDB的标准查询操作。
- $limit：用来限制MongoDB聚合管道返回的文档数。
- $skip：在聚合管道中跳过指定数量的文档，并返回余下的文档。
- $unwind：将文档中的某一个数组类型字段拆分成多条，每条包含数组中的一个值。
- $group：将集合中的文档分组，可用于统计结果。
- $sort：将输入文档排序后输出。
- $geoNear：输出接近某一地理位置的有序文档。



***常见聚合表达式***

可以看出格式为[{$group : {**_id** : "$分组依据", 别名 : {$聚合操作 : "$likes"}}}]，求平均，没有的不会算进去

| 表达式    | 描述                                           | 实例                                                         |
| :-------- | :--------------------------------------------- | :----------------------------------------------------------- |
| $sum      | 计算总和。                                     | db.mycol.aggregate([{$group : {_id : "$by_user", num_tutorial : {$sum : "$likes"}}}]) |
| $avg      | 计算平均值                                     | db.mycol.aggregate([{$group : {_id : "$by_user", num_tutorial : {$avg : "$likes"}}}]) |
| $min      | 获取集合中所有文档对应值得最小值。             | db.mycol.aggregate([{$group : {_id : "$by_user", num_tutorial : {$min : "$likes"}}}]) |
| $max      | 获取集合中所有文档对应值得最大值。             | db.mycol.aggregate([{$group : {_id : "$by_user", num_tutorial : {$max : "$likes"}}}]) |
| $push     | 在结果文档中插入值到一个数组中。               | db.mycol.aggregate([{$group : {_id : "$by_user", url : {$push: "$url"}}}]) |
| $addToSet | 在结果文档中插入值到一个数组中，但不创建副本。 | db.mycol.aggregate([{$group : {_id : "$by_user", url : {$addToSet : "$url"}}}]) |
| $first    | 根据资源文档的排序获取第一个文档数据。         | db.mycol.aggregate([{$group : {_id : "$by_user", first_url : {$first : "$url"}}}]) |
| $last     | 根据资源文档的排序获取最后一个文档数据         | db.mycol.aggregate([{$group : {_id : "$by_user", last_url : {$last : "$url"}}}]) |





## 复制

将数据同步到多个服务器

主节点记录在其上的所有操作oplog，从节点定期轮询主节点获取这些操作，然后对自己的数据副本执行这些操作，从而保证从节点的数据与主节点一致。所有写入操作都在主节点上。



## 分片 Shard

将集合分隔到多个服务器上(一个集群中)

满足MongoDB数据量大量增长的需求，当MongoDB存储海量的数据时，一台机器可能不足以存储数据，也可能不足以提供可接受的读写吞吐量。这时，我们就可以通过在多台机器上分割数据，使得数据库系统能存储和处理更多的数据。

一个分片可以用多个副本，单点故障。



## 备份与恢复

mongodump命令导出所有数据到指定目录中。

恢复`mongorestore -h <hostname><:port> -d dbname <path>`



