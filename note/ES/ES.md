* 增删改查，各种查询中分词关系的区别
* 批量
* 聚合
* 分词
* mapping
* 分片节点与副本
* 集群
* 安全
* 恢复与备份
* 分布式
* 可视化
* 监控
* 节点，节点上都有什么:(分片，副本)
* 改变两种端口





# Basic concepts



**用途(特性)**

* 全文检索
* 日志分析
* 近实时，从index到可search通常是一秒钟
* 扩展性强
* Java开发的



















## install



注意事项：

* 设置ES的初始内存和最大内存，否则导致过大启动不了ES
* -e "discovery.type=single-node" 设置为单节点



docker中安装

单体

```shell
docker run --name es-single -p 9200:9200 \
 -p 9300:9300 \
 -e "discovery.type=single-node" \
 -e ES_JAVA_OPTS="-Xms64m -Xmx128m" \
-d 6c0bdf761f3b
```





docker run -d --name kibana  --link bcd69f7cf081:elasticsearch -p 5601:5601 18efdc555b14



/usr/share/elasticsearch/config/elasticsearch.yml



docker run --name es1 -p 8201:9200 \
 -p 8101:9300 \
-v /Users/chenguanlin/Documents/workspace/es/cluster/es1/conf/elasticsearch.yml:/usr/share/elasticsearch/config/elasticsearch.yml \

--net=host \

-e ES_JAVA_OPTS="-Xms64m -Xmx128m" \
-d 6c0bdf761f3b



**配置**

[ES5.6.8配置](https://www.elastic.co/guide/en/elasticsearch/reference/5.6/settings.html)

ES有两个配置文件：

* elasticsearch.yml
* log4j2.properties



配置文件中使用环境变量的值:配置文件中用 ${ ... }符号引用的环境变量将替换为环境变量的值

Elasticsearch 具有良好的默认值，并且只需要很少的配置。可以使用群集更新设置 API 在正在运行的群集上更改大多数设置。

虽然 Elasticsearch 只需要很少的配置，在投入生产之前一定要进行一些配置。

- [`path.data` and 及`path.logs`](https://www.elastic.co/guide/en/elasticsearch/reference/5.6/important-settings.html#path-settings)
- [`cluster.name`](https://www.elastic.co/guide/en/elasticsearch/reference/5.6/important-settings.html#cluster.name)
- [`node.name`](https://www.elastic.co/guide/en/elasticsearch/reference/5.6/important-settings.html#node.name)
- [`bootstrap.memory_lock`](https://www.elastic.co/guide/en/elasticsearch/reference/5.6/important-settings.html#bootstrap.memory_lock)
- [`network.host`](https://www.elastic.co/guide/en/elasticsearch/reference/5.6/important-settings.html#network.host)
- [`discovery.zen.ping.unicast.hosts`](https://www.elastic.co/guide/en/elasticsearch/reference/5.6/important-settings.html#unicast.hosts)
- [`discovery.zen.minimum_master_nodes`](https://www.elastic.co/guide/en/elasticsearch/reference/5.6/important-settings.html#minimum_master_nodes)



配置文件

[参考](https://www.cnblogs.com/hanyouchun/p/5163183.html)

```yaml
cluster.name: elasticsearch
node.name: "TOM"


transport.tcp.port: 9300
http.port: 9200

# 是否使用http协议对外提供服务，默认为true
http.enabled: false

# 是否有资格成为node
node.master: true

# 是否存储数据，默认true
node.data: true

# 分片数，默认5
index.number_of_shards: 5

# 副本数，默认1
index.number_of_replicas: 1

# 数据存储路径，默认是es根目录下的data文件夹，可以设置多个存储路径，用逗号隔开
path.data: /path/to/data

# 临时文件的存储路径
path.work: /path/to/work

# 日志存储路径
path.logs: /path/to/logs

# 插件存储路径
path.plugins: /path/to/plugins

# 锁住内存
bootstrap.mlockall: true

# 设置内容的最大容量，默认100mb
http.max_content_length: 100mb

```



**安全配置**

[ Secure Settings](https://www.elastic.co/guide/en/elasticsearch/reference/5.6/secure-settings.html)

有些设置是敏感的，仅仅依靠访问许可权来保护他们的价值观是不够的。Elasticsearch 提供了一个 keystore 和 Elasticsearch-keystore 工具来管理 keystore 中的设置。

**查看集群信息和管理集群**

| API                 | 描述                      |
| ------------------- | ------------------------- |
| GET /_cat/          | 查看都能查看什么          |
| GET /_cat/health?v  | 查看健康状态              |
| GET /_cat/nodes?v   | 查看节点列表              |
| GET /_cat/indices?v | 查看所有index             |
| _cat/count/{index}  | 查看一个index中有多少文档 |



```shell
GET /_cat/health?v
```

返回集群名，集群状态等信息



集群状态：

* 绿色，表示一切健康
* 黄色，表示副本出现了问题，主分片不会影响使用
* 红色，表示主分片上的数据出现了问题，某些不可用









# CRUD



**读写的过程**



一个index分成多个分片，每个分片又有副本，在添加或删除document时必须保证主分片和副本的同步。

主分片作为操作的入口，会对请求进行校验然后转发给副本，



* 每个index操作都会被分到一个副本组中(默认有Id分)，这样使用多个副本组就能提高性能

* 由主分片(主副本)进行校验后转发到其他副本



**流程：**

1. 主分片收到请求后进行校验，然后再本地执行它
2. 主分片将请求转发到其他副本，其他副本并行执行操作
3. 所有副本执行成功后返回到主分片，主分片确认成功后返回给客户



**故障处理：**

在整个操作的过程中，可能由于网络，硬件设施的问题，导致在一个分片上的操作失败。



主分片失败的情况下，操作会等待一个副本成为新主分片(默认一分钟)，然会将操作发送到新主分片。

副本失败时，主节点会删除这个分片，然后再其他节点上新建一个分片，

如果副本处理完操作汇总到主节点时主节点出现了意外，旧主节点会拒绝请求，转发到新的主节点上



Basic write model

首先请求操作会被解析路由到对应的replication group，然后操作会被转发到给赋值组的primary，primary将负责操作的校验，执行操作，然后转发给其他shards。

因为replicas是可以离线的，primary不用讲操作转发给所有shards，ES中维护着一个列表：操作应该都发给谁，这个列表叫做*in-sync copies* ，由主节点维护。



**失败处理**

许多情况都会导致操作失败，尽管主分片操作成功， disks can get corrupted, nodes can be disconnected from each other, or some configuration mistake。这些很少见，但是必须对它做出响应。

* 主分片失效时，主分片的宿主节点会向主节点发送消息，操作会等一分钟让master来从配置分组中选一个新primary，然后讲操作转发到新的primary。master还会监视节点的运行状况，并可能会决定主动降级主节点。

* 副本上故障或由于网络问题导致操作无法到达副本，导致副本没有更新操作，primary将会请求master删除这些节点，确认删除后才返回。master还将指示另一个节点开始构建新的分片副本，以将系统恢复到正常状态。
* primary转发送replicas时，还会通过replica校验primary，操作会被replica拒绝



























































## index



一个index钟可以有多个type，(最新版已经移除type)

**创建index**

* 插入数据时会自动创建index和type
* 创建mapping

**删除index**

```shell
DELETE /customer
```



**插入或替换**

```shell
PUT /customer/external/1?pretty
```



### mapping



mapping定义的文档的字段怎么被存储和索引

index中的type对文档进行了逻辑上的划分，每个type可以定义自己的mapping



* 每个字段可以是简单的数据类型：text，keyword，date，long，double，ip等

* 也可以是复杂的JSON层次结构，如Object，nested
* 或者ES的特殊类型，geo_point, geo_shape,completion.



**动态映射**

在进行index时会自动创建mapping，但是这种mapping可能跟我们想要的不一样，会将字符串或时间创建为text，有可能我们想要的是keyword/dates



**更新Mapping**

Mapping是不能直接更新的，可以新建mapping，reIndex到新索引上



一个index中的字段声明作用于index中所有的type





## query

* 可以通过url传参和body传参，一般用body



**query的格式**

`***/_search`



###  Multiple Indices



一般查询都**支持多个index参数**，

包括传统的传入多个index

也包括：

* _all
* \*in\*dex*,用\*做wildcard
* 还可以进行+，-



都支持以下queryString参数

1. **ignore_unavailable**，忽略无效的inex(不存在或者closed)
2. **allow_no_indices**，通配符表达式表达的index不存在时，是否失败，同样适用_all等没有明确指定indx的
3. **expand_wildcards**，可设为open/closed



### Date math support in index names



Date math index name resolution enables you to search a range of time-series indices。

限制搜索索引的数量可以减少集群上的负载并提高执行性能。

index的名字按指定格式命名，查询的时候只查询匹配date的index

A date math index name takes the following form：

```txt
<static_name{date_math_expr{date_format|time_zone}}>
```

包括静态的名字，日期表达式，日期格式和时区.

所有特殊字符都应该是URI编码的,eg:

```console
# GET /<logstash-{now/d}>/_search 
GET /%3Clogstash-%7Bnow%2Fd%7D%3E/_search
```

[date-match-index](https://www.elastic.co/guide/en/elasticsearch/reference/5.6/date-math-index-names.html)



### Common options



**Enabling stack traces**

error_trace=true查看错误栈信息



### **DSL**



domain-specific language领域特定语言，ES提供了一个 json 风格的查询语言。

```json
GET /bank/_search
{
  "query": { "match_all": {} },
  "sort": { "balance": { "order": "desc" } },
  "from": 10,
  "size": 10
}
```

我们可以在JSON中指定查询条件等信息



**查询返回**

ES默认只返回查询到的数据的一部分文档(前10个)，可以通过size改变

**只返回指定字段**

文档存在_source里，所以可以通过\_source指定要返回的字段

```shell
GET /bank/_search
{
  "query": { "match_all": {} },
  "_source": ["account_number", "balance"]
}
```

**只返回需要的内容**

除了source外，返回中还包含了许多其他信息，可以通过



**match_all**

全文索引时boost用来提高权重



#### 全文索引



**match**

* 先分词再查询
* 接收文本，数字日期的查询
* 分词之间的关系默认是or，可以将operator设为and
* 可以通过minimum_should_match.设置should中最少满足的个数



**match_phrase**

分词，and关系且顺序一致



**match_phrase_prefix**

Match _ phrase _ prefix 与 match _ phrase 相同，只是它允许对文本中的最后一个词进行前缀匹配

此外，它可以控制后缀的长度max_expansions

```shell
GET /_search
{
    "query": {
        "match_phrase_prefix" : {
            "message" : {
                "query" : "quick brown f",
                "max_expansions" : 10
            }
        }
    }
}
```



**common terms**

"The  brown  fox"

进行全文查询会查询三个单词，但是常用单词The实际影响可能不大，但是有不能忽略



common terms会将分词归为两组：重要的(低频的)和不太重要的(高频的)

先查重要的再查不重要的

不重要的不会改变相关性得分



**query_string**

分词之间的关系是and，不要求顺序一致，与match相反，两者可以相互转换



**Fuzziness**

可以设置前缀长度和最大展开式来控制模糊过程

默认情况下允许使用模糊换位(ab → ba) ，但是可以通过将 Fuzzy _ 换位设置为 false 来禁用它



#### term查询



term



terms



range



exists



prefix



ids



#### 复合查询



**bool query**

The must and should clauses have their scores combined — the more matching clauses, the better — while the must_not and filter clauses are executed in filter context.









## delete



**delete_by_id**

```shell
DELETE /customer/external/2?pretty
```



**delete_by_query**





## update



更新会删除旧数据重新index



**update_by_id**

```shell
POST /customer/external/1/_update?pretty
{
  "doc": { "name": "Jane Doe" }
}
```

更新需要用到旧数据时，可以使用script

```shell
POST /customer/external/1/_update?pretty
{
  "script" : "ctx._source.age += 5"
}
```

ctx. _ source表示引用当前上下文的源文档



**update_by_query**



## bulks

```shell
POST /customer/external/_bulk?pretty
{"index":{"_id":"1"}}
{"name": "John Doe" }
{"index":{"_id":"2"}}
{"name": "Jane Doe" }
```

* 除了delete外都是两行组成一个操作

* 批量 API 不会因为某个操作失败而失败。如果单个操作因为某种原因失败，它将继续处理之后的其余操作。当大容量 API 返回时，它将为每个操作提供一个状态(与它发送的顺序相同)
* 

# Aggregations



# 集群



## Cluster

由多个节点共同提供功能，一个集群通过集群名唯一标识，节点通过集群名加入集群。ES默认的集群名是"elasticsearch"



## node

节点就是一个服务器，节点的标识符会在启动时分配一个UUID,也可以自定义节点名，相互可以发现的节点，如果集群名相同，就会自动形成一个集群，默认会加入一个名为"elasticsearch"的集群。











**写操作**

首先通过路由（通常基于文档ID）将Elasticsearch中的每个索引操作解析为一个复制组，确定复制组后，操作将在内部转发到该组的当前主分片，由于副本可以离线，**主副本因此不需要复制到所有副本**，Elasticsearch主节点维护应接收该操作的分片副本列表(*in-sync copies*)



The primary shard follows this basic flow

* 检查操作,如果结构无效(如have an object field where a number is expected)
* 本地执行操作，This will also validate the content of fields and reject if needed (Example: a keyword value is too long for indexing in Lucene).
* Forward the operation to each replica in the current in-sync copies set. If there are multiple replicas, this is done in parallel.
* 副本执行成功后响应主分片，主分片确认后响应给客户



**读操作**

通过ID来读是非常轻量级的，复杂查询和聚合依赖CPU的能力。

当节点接收到读取请求时，该节点负责将其转发到持有相关分片的节点，整理响应并响应客户端。 我们将该节点称为该请求的协调节点。





reindex

复制索引

refresh

控制改变什么时候可以访问



# Document API



All CRUD APIs are single-index APIs

**Single document APIs**

Index API
Get API
Delete API
Update API

**Multi-document APIs**

Multi Get API
Bulk API
Delete By Query API
Update By Query API
Reindex API



批处理

有两种批处理

***multiGet***

一次进行多个查询

***bulk***

一次进行多个index/delete

注意

* 最后一行要有一个换行符

* `Content-Type` header should be set to `application/x-ndjson`.



## Reindex

赋值索引中的文档到另一个

应用场景：

1、当你的数据量过大，而你的索引最初创建的分片数量不足，导致数据入库较慢的情况，此时需要扩大分片的数量，此时可以尝试使用Reindex。

2、当数据的mapping需要修改，但是大量的数据已经导入到索引中了，重新导入数据到新的索引太耗时；但是在ES中，一个字段的mapping在定义并且导入数据之后是不能再修改的，



注意：只是赋值文档，不复制index的设置

```console
POST _reindex
{
  "source": {
		"index": ["twitter", "blog"],
    "type": ["tweet", "post"],
    "size": 1,
    "_source": ["user", "tweet"],	
    "query": {
      "term": {
        "user": "kimchy"
      }
    }
  },
  "dest": {
    "index": "new_twitter"
  }
}
```

默认情况下，_reindex使用1000进行批量操作，您可以在source中调整batch_size



从远程ES赋值

```console
POST _reindex
{
  "source": {
    "remote": {
      "host": "http://otherhost:9200",
      "username": "user",
      "password": "pass"
    },
    "index": "source",
    "query": {
      "match": {
        "test": "data"
      }
    }
  },
  "dest": {
    "index": "dest"
  }
}
```

​	

## Term Vectors

查看document中的分词情况

位置信息，开始偏移量，结束偏移量，词条统计，字段统计

```
GET /twitter/tweet/1/_termvectors?fields=message
```





## Multi termvectors API



？Refresh

The Index, Update, Delete, and Bulk APIs support setting refresh to control when changes made by this request are made visible to search.



三种情况

***Empty string or true***
Refresh the relevant primary and replica shards (not the whole index) immediately after the operation occurs，so that the updated document appears in search results immediately.

只有在仔细考虑并验证它不会导致性能低下（无论是从索引还是从搜索的角度来看）之后，才应该这样做



**wait_for**

This doesn’t force an immediate refresh, rather, it waits for a refresh to happen.ES自动刷新分片的频率默认为1秒，调用refresh或将refresh置为true也会刷新



`false` **(the default)**

Take no refresh related actions.当更改请求返回后刷新。



# Search API



Most search APIs are multi-index, multi-type

When executing a search, it will be broadcast to all the index/indices shards



## 路由

_**routing** 是一个可变值，默认是文档的 **_id** 的值 ，也可以设置成一个自定义的值。 _routing 通过 hash 函数生成一个数字，然后这个数字再除以 num_of_primary_shards （主分片的数量）后得到余数 。这个分布在 0 到 number_of_primary_shards-1 之间的余数，就是我们所寻求的文档所在分片的位置。**这就解释了为什么我们要在创建索引的时候就确定好主分片的数量** **并且永远不会改变这个数量**

* 很多时候自定义路由是为了减少查询时扫描shard的个数，从而提高查询效率。

* 保证同一个商户的数据全部保存到同一个shard去







## Request Body Search

query放在body中

```console
GET /_search
{
    "query" : {
        "term" : { "user" : "kimchy" }
    }
}
```



还可以指定其他参数

**timeout**

A search timeout, bounding the search request to be executed within the specified time value and bail with the hits accumulated up to that point when expired. Defaults to no timeout. See Time units.

**from**

To retrieve hits from a certain offset. Defaults to 0.

**size**

The number of hits to return. Defaults to 10. If you do not care about getting some hits back but only about the number of matches and/or aggregations, setting the value to 0 will help performance.

**search_type**

The type of the search operation to perform. Can be dfs_query_then_fetch or query_then_fetch. Defaults to query_then_fetch. See Search Type for more.

**request_cache**

Set to true or false to enable or disable the caching of search results for requests where size is 0, ie aggregations and suggestions (no top hits returned). See Shard request cache.

**terminate_after**

The maximum number of documents to collect for each shard, upon reaching which the query execution will terminate early. If set, the response will have a boolean field terminated_early to indicate whether the query execution has actually terminated_early. Defaults to no terminate_after.

**batched_reduce_size**

The number of shard results that should be reduced at once on the coordinating node. This value should be used as a protection mechanism to reduce the memory overhead per search request if the potential number of shards in the request can be large.



## 排序



## SourceFilter

指定返回的_source中都要文档的什么字段

```console
GET /_search
{
    "_source": [ "obj1.*", "obj2.*" ],
    "query" : {
        "term" : { "user" : "kimchy" }
    }
}
```

默认指定的是需要包含的，还可以泛型过滤

```
GET /_search
{
    "_source": {
        "includes": [ "obj1.*", "obj2.*" ],
        "excludes": [ "*.description" ]
    },
    "query" : {
        "term" : { "user" : "kimchy" }
    }
}
```





## Fields

- 当store为false时(默认配置），这些field只存储在"_source" field中。
- 当store为true时，这些field的value会存储在一个跟 `_source` 平级的独立的field中。同时也会存储在_source中，所以有两份拷贝



## Doc value fields



## postFilter

作用于聚合完成时，但是只过滤搜索结果，不过滤聚合



## 高亮

高亮搜索结果中的一个或多个字段



## Rescoring



## SearchType



## Scroll

使用scroll，在首次查询时需要指明scroll参数，告诉ES保持SearchContext多久。时间不需要设置太长，只要能满足一次查询，每次查询都会新设过期时间。

首次scroll查询会返回一个_scroll_id，which should be passed to the `scroll` API in order to retrieve the next batch of results.不用再指定index和type，只需要传入scroll(保持多久)和srcoll_id



如果scrollSearch中包含了聚合，则只有首次查询的返回中包含聚合结果

优化：如果不需要考虑查询结果的顺序，可以将顺序指定为_doc来提高查询速度

查看

```console
GET /_nodes/stats/indices/search
```



清空scroll

```console
DELETE /_search/scroll
{
    "scroll_id" : [
      "DXF1ZXJ5QW5kRmV0Y2gBAAAAAAAAAD4WYm9laVYtZndUQlNsdDcwakFMNjU1QQ==",
      "DnF1ZXJ5VGhlbkZldGNoBQAAAAAAAAABFmtSWWRRWUJrU2o2ZExpSGJCVmQxYUEAAAAAAAAAAxZrUllkUVlCa1NqNmRMaUhiQlZkMWFBAAAAAAAAAAIWa1JZZFFZQmtTajZkTGlIYkJWZDFhQQAAAAAAAAAFFmtSWWRRWUJrU2o2ZExpSGJCVmQxYUEAAAAAAAAABBZrUllkUVlCa1NqNmRMaUhiQlZkMWFB"
    ]
}
```

清空所有

```console
DELETE /_search/scroll/_all
```



sliced scroll

当scroll饭hi大量文档时，可以将一个scroll分成多片，多片的并集就相当于不分片的结果，它根据算法将分片分给不同shard

```console
GET /twitter/tweet/_search?scroll=1m
{
    "slice": {
        "id": 0, 
        "max": 2 
    },
    "query": {
        "match" : {
            "title" : "elasticsearch"
        }
    }
}
GET /twitter/tweet/_search?scroll=1m
{
    "slice": {
        "id": 1,
        "max": 2
    },
    "query": {
        "match" : {
            "title" : "elasticsearch"
        }
    }
}
```



## preference



## version

Returns a version for each search hit.

```console
GET /_search
{
    "version": true,
    "query" : {
        "term" : { "user" : "kimchy" }
    }
}
```



## index boost

当使用多个index进行查询时，设置index的重要性

```console
GET /_search
{
    "indices_boost" : [
        { "alias1" : 1.4 },
        { "index*" : 1.3 }
    ]
}
```



## 最小得分

```console
GET /_search
{
    "min_score": 0.5,
    "query" : {
        "term" : { "user" : "kimchy" }
    }
}
```



## Inner hits



## Field Collapsing



按字段进行折叠













# Query DSL









































































# Mapping



**注意**

6.0之后的mapping不再包含type



**为什么要移除type？**

​		index -> databases, type -> table这种类比时错误的，因为在SQL中，同一个数据库中的不同表中，名字相同的字段之间是彼此独立的，而在ES中，同一个index中名字相同的字段在内部由同一个字段支持，这就使得不同type相同字段在内部时存储在统一个字段上的，它们的类型必须相同。

​		而一个index钟存储的实体几乎不含相同字段时，会导致数据稀疏，降低了压缩数据能力



解决方案：

1. **不同类型的type建立不同的index**

好处：

	* 数据更密集，有利于压缩
	* The term statistics used for scoring in full text search are more likely to be accurate，因为所有文档都是相同的index，相同的entity
	* 每个索引可以根据文档数量适当调整大小

2. **自定义type field**

一个集群中可以存在多少主碎片是有限制的，如果不希望浪费整个碎片来收集只有几千个document。可以实现自己的自定义类型字段，该字段的工作方式与旧的 _ type 类似。



将multi-type的index迁移到single-type的index中

```js
POST _reindex
{
  "source": {
    "index": "twitter",
    "type": "tweet"
  },
  "dest": {
    "index": "tweets"
  }
}
```



每个mapping中可以有多个type，每个type钟可以有多个fields

每个document中都有一个meta-field：_type，包含着type的名字，所以搜索document时可以指定一个/多个type

一个index不同type中可以有相同的字段，因为`_type` field was combined with the document’s `_id` to generate a `_uid` field

Mapping还可以建立一个父子关系，比如question类型的document可以是anser类型的document的父类

mapping定义了一个文档和它包含的字段怎么被存储和索引。

***一旦设置后不能修改和删除，只能增加***



use mappings to define:

* which string fields should be treated as full text fields.
* which fields contain numbers, dates, or geolocations.
* whether the values of all fields in the document should be indexed into the catch-all _all field.
  the format of date values.
* custom rules to control the mapping for dynamically added fields.

type只是逻辑分区



Meta-fields：包含\_index,\_type,\_id,\_source

type包含properties，properties中。

同一index中不同type中具有相同名称的字段必须具有相同的mapping

字段的类型

* 简单类型text, keyword, date, long, double, boolean or ip。

* 支持JSON分层特性的类型：Object和Nested
* 其他特殊的类型 geo_point, geo_shape, or completion



```
为不同的目的以不同的方式对同一字段建立索引通常很有用
例如一个字符串可以存储为text来进行全文索引，也可以存储伟keyword
另外，您可以使用标准分析器，如英语分析器和法语分析器为字符串字段建立索引
```



index中定义太多field可能会引起index爆炸





指定index的mapping(即指定index中的type和type中的properties(字段类型))



## 字段类型



**基本数据类型**

String：包括text和keyword

Numeric : long，integer，short，double，float，half_float,scaled_float

Date: Date

Boolean: boolean

Binary: binary

Range: integer_range`, `float_range`, `long_range`, `double_range`, `date_range



**复杂数据类型**



**数组**

​		在ES中没有数组类型，但是每个字段赋值时，都可以将多个值赋值给该字段，不需要任何配置。

​		要保证元素的数据类型与Mapping中的相同，没有设置Mapping时，数组中的第一个元素的类型会作为字段的类型，后面要保证一致或者能强制转换。

​		空数组会被当成没有值的字段



**Object**



**Nested**













---





ES
存储，搜索和分析

索引 类型  文档	分片		副本
Index
	Index中能定义一个或多个Type,索引是相关文档的集合,相当于目录
	index可以理解成插入
Type
	Index的逻辑分区，相当于章节
Document 
	章节中的具体内容,是能被索引的基本单位

X-Pack是Elastic Stack扩展

查看节点信息
查看节点健康信息：http://localhost:9200/_cat/health?v
查看节点列表		http://localhost:9200/_cat/nodes?v
查看所有index		GET /_cat/indices?v
等http://localhost:9200/_cat/中的内容

创建一个索引	localhost:9200/customer?pretty,默认由一个主分片和一个副本
pretty参数表示如果有，打印漂亮的JSON

	插入document
插入必须指定index/type,而index和type不需要先创建，没有时会自动创建
PUT /customer/external/1?pretty
{document}
插入document，id为1
返回的数据
{
    "_index": "customer",
    "_type": "external",
    "_id": "1",
    "_version": 1,//版本号
    "result": "created",//执行的操作
    "_shards": {
        "total": 2,
        "successful": 1,
        "failed": 0
    },
    "_seq_no": 0,// 用来实现锁
    "_primary_term": 1
}
可以不指定id，这样ES会自己生成，但是要有post

	查看document
GET /customer/external/1?pretty
响应：
{
    "_index": "customer",
    "_type": "external",
    "_id": "1",
    "_version": 1,
    "_seq_no": 0,
    "_primary_term": 1,
    "found": true,	// 查找成功
    "_source": {	// document中的数据
        "name": "John Doe"
    }
}

	删除index
DELETE /customer?pretty

可以看出通用模式，rest 、index/type/id

	替换index中的document
跟添加一样，存在了就是修改,可以看到version增加,就算数据都一样也会增加
	更新index中的document
_updateAPI
ES内部的更新是删除老doc，插入新doc
POST /customer/external/1/_update?pretty
{
  "doc": { "name": "Jane Doe" } //doc中指定新属性，旧文件有的修改，没有的添加
}
除了用doc外，还能用script
POST /customer/external/1/_update?pretty
{
  "script" : "ctx._source.age += 5"
}// ctx._source的作用是引用修改文档

	Update By Query
	
	批量操作
_bulkAPI
批量插入，url后加/_bulk,body中放多个doc(JSON对象)，不用是数组
每两个JSON对象表示一个doc，第一个指定id，第二个是数据本身，delete因为没有源文件，所以只有一个{}
{动作：{}
{文件}



curl -X POST "localhost:9200/customer/external/_bulk?pretty&pretty" -H 'Content-Type: application/json' -d'
{"index":{"_id":"1"}}
{"name": "John Doe" }
{"index":{"_id":"2"}}
{"name": "Jane Doe" }
	                       Java API
生成JSON文档
使用byte[]或String
使用Map会自动转换为其等效的JSON
使用内置帮助器XContentFactory.jsonBuilder（）
内部最终都会转成byte[]

XContentBuilder builder = jsonBuilder()
    .startObject()
        .field("user", "kimchy")
        .field("postDate", new Date())
        .field("message", "trying out Elasticsearch")
    .endObject()
 同样，还可以使用startArray(String)等
 Strings.toString(builder)可以转换成字符串

 两个端口

(index,type,id).setSource() // 插入的文档可以使用XContentBuilder或者是JSON字符串加类型
如(json, XContentType.JSON)


                        Query DSL
查询构建器的工厂是QueryBuilders

查询字句的行为决定了是用查询上下文还是过滤上下文

查询上下文：
    该文档与查询语句的匹配度如何,
过滤上下文：
    此文档是否与此查询子句匹配

                        scroll API 
可以从查询结果中重新提取，类似于MySQL的光标,不是为了实时请求，而是对结果进行重新检索
滚动查询需要指定活跃时间,
index,type,TimeValue,query,
                        
                        search API
执行搜索查询并获得命中率

            query:{}
match:{}会对field进行分词操作，然后再查询
基本封装条件，
matchall查询所有文档
must:[]封装必须满足的条件,还有mastnot,term,range,或者是简单的query
bool:{xxx条件，xxx条件}，多条件查询
filter:[]封装过滤条件

匹配查询
    matchQuery(
    "字段名",                  
    "文本"   
)

多重匹配查询
multiMatchQuery(
    "文本", 
    "字段名", "字段名"    // 有一个字段匹配到就行   
);

// 使用common term查询，会将分词分为两种
commonTermsQuery("name","kimchy");

// 使用一个查询字符串来插叙
QueryBuilder qb = queryStringQuery("+kimchy -elasticsearch");    
QueryBuilder qb = simpleQueryStringQuery("+kimchy -elasticsearch");   

prepareSearch(index)
                .setTypes(type)
                .setScroll(new TimeValue(60000))
                .setQuery(query)
                .setSize(1000).execute().actionGet(); 

SearchResponse查询结果：
_scroll_id
took
_shards
_hits命中对象，_hits 命中对象中记录的所有命中 _source 每个命中的文档


Query怎么写

Update怎么写







什么是ES

ES是如何工作的

全文搜索分析引擎

使用场景：

* 使用Elasticsearch存储整个产品目录和库存，并为它们提供搜索和自动完成建议
* 收集交易数量，分析挖掘趋势
* reverse-search，降价通知
* 可视化监控





基本概念：

NRT：近乎实时(NRT)地搜索性能：从插入到能被搜索的时间

cluster：一个或多个节点的集合，一起维护着全部数据，提供存储和搜索能力。集群的名字作为集群的唯一标识，默认集群的名字是elasticsearch，节点通过集群名字加入集群。

node：节点是集群中的单个服务器，用来存储数据，参与索引和搜索，节点启动时，节点名默认由UUID生成，可自定义。指定集群名来加入集群，默认加入集群名为elasticsearch的集群，同意网络中，集群名相同的节点会自动组成一个集群。



index

index是相关联的document集合，比如一个index存与customer相关的数据，另一个存与日志相关的数据。index的名字必须小写。对文档执行增删改查操作都需要用到index。



type

一个index中可以定义多个type，是index的逻辑分区。通常将含有相同字段的document放到一个type中。



doucument

数据存储的基本单元，文档用JSON表示



shards&replicas

索引中存储的数据可能会超过单个节点上硬件能存出的上限，或者单个节点做查找太慢了。

在ES中，可以将一个index分割到多个shards中。在创建index时，可以指定shards的个数，每个分片都拥有一个功能完成的index，可以把分片托管到集群中任意节点上。怎么分片以及结果如何聚合回来完全由ES管理，对使用者透明。

在网络/云中，分片/节点随时都有可能发生故障，可以使用ES的故障转移机制来避免分片掉线或消失：ES允许给一个分片制作一个或多个副本。分片节点发生故障时，分片节点提供高可用性，所以分片节点和赋值节点不要部署到一起。副本上可以执行搜索，所以副本提高了搜索性能。

所以index有两种分片：主分片和赋值分片，两者在创建index的时候指定，之后可以改变赋值分片。



Cluster Health

健康检查，`GET /_cat/health?v`

health有三种状态：绿，黄，红

- Green - everything is good (cluster is fully functional)
- Yellow - all data is available but some replicas are not yet allocated (cluster is fully functional)
- Red - some data is not available for whatever reason (cluster is partially functional)

当我们自己测试建了个单节点的，默认情况下，Elasticsearch为此索引创建了一个副本。由于目前我们只有一个节点在运行，因此直到另一个节点加入群集的稍后时间，才能分配该副本。

当群集为红色时，它将继续为可用分片提供搜索请求，但由于存在未分配的分片，需要尽快对其进行修复。

获取集群中节点的列表`GET /_cat/nodes?v`

列出所有index`GET /_cat/indices?v`

查看index信息 `GET /index`

创建index`PUT /customer?pretty`  pretty会打印漂亮的JSON

***ES不需要显示的添加索引***

根据Id替换document，Id不存在时创建

```.json
PUT /customer/external/1?pretty
{
  "name": "John Doe"
}
```

返回结果

```sh
{
  "_index" : "customer",
  "_type" : "external",
  "_id" : "1",
  "_version" : 1,
  "result" : "created",
  "_shards" : {
    "total" : 2,
    "successful" : 1,
    "failed" : 0
  },
  "created" : true
}
```



插入文档，不指定id自动生成id

```console
POST twitter/tweet/
{
    "user" : "kimchy",
    "post_date" : "2009-11-15T14:12:12",
    "message" : "trying out Elasticsearch"
}
```



修改文档

Elasticsearch实际上并未在后台进行就地更新，而是删除旧文档，插入新文档。

```
POST /customer/external/1/_update?pretty
{
  "doc": { "name": "Jane" }
}
```

将Id为1的document中name改为Jane

也可以通过script更新

```
POST /customer/external/1/_update?pretty
{
  "script" : "ctx._source.age += 5"
}
```



删除文档

```
DELETE /customer/external/2?pretty
```



查询文档

```console
GET /customer/external/1?pretty
```

结果

```js
{
  "_index" : "customer",
  "_type" : "external",
  "_id" : "1",
  "_version" : 1,
  "found" : true,
  "_source" : { "name": "John Doe" }
}
```

删除索引

```console
DELETE /customer?pretty
```

修改数据

默认情况下，从索引/更新/删除数据到显示在搜索结果中的时间可能会有一秒钟的延迟（刷新间隔）

每当我们进行更新时，Elasticsearch都会删除旧文档，然后以一张快照将应用了更新的新文档编入索引

```console
POST /customer/external/1/_update?pretty
{
  "doc": { "name": "Jane Doe" }
}
```

可以看出，修改是修改文档，文档中的字段可以进行增删改查。

通过script修改文档的字段

```console
POST /customer/external/1/_update?pretty
{
  "script" : "ctx._source.age += 5"
}
```

ctx._source就代表该文档

批量更新



删除文档

```console
DELETE /customer/external/2?pretty
```

批量删除



批处理

尽可能少的减少请求次数

```console
POST /customer/external/_bulk?pretty
{"index":{"_id":"1"}}
{"name": "John Doe" }
{"index":{"_id":"2"}}
{"name": "Jane Doe" }
{"delete":{"_id":"2"}}
```

可以看出，删除操作后不需要源文件

批量API不会因其中一项操作失败而失败。如果单个操作由于任何原因而失败，它将继续处理其后的其余操作。批量API返回时，它将为每个操作提供状态（以发送顺序相同），以便您可以检查特定操作是否失败。



SearchApi

可以通过url或者请求体封装条件，方法体的JSON字符串更可读

一旦返回搜索结果，Elasticsearch将完全完成请求，并且不会维护任何类型的服务器端资源或在结果中打开游标。这与许多其他平台（例如SQL）形成了鲜明的对比，在SQL中，您最初可能会首先获取部分查询结果，然后如果要获取（或分页）其余内容，则必须连续返回服务器。使用某种状态服务器端游标的结果。



QueryLanguage

使用JSON样式的查询语言，被称为DSL

```console
GET /bank/_search
{
  "query": { "match_all": {} }
}
```



除了query外，还可以传入其他参数影响结果，不指定size的话，默认是10

```console
GET /bank/_search
{
  "query": { "match_all": {} },
  "sort": { "balance": { "order": "desc" } }，
  "from": 10,
  "size": 10,
  “ _source” ：[ “ account_number” ，“ balance” ]//表示只要哪几个字段，类似SQL的SELECT ... FROM
}
```

query用来定义查询，match_all表示查询类型

**Query DSL**

match_all

match 	会对搜索内容分词

match_phrase	不会对搜索内容进行分词

range query 

bool 复合查询，可以用filter，should，must，must_not将其他query组合起来，可以把他们当成是query的修饰符



template

```JSONconsole
{
    "query": {
        "bool": {
            "must": {"match_all": {}},
            "filter": {}，
            "should": [],
            "mush_not": []
        }
    }
}
```

query时可以嵌套的



匹配的文档会放到_source中返回，如果不需要返回整个文档，可以在\_source中指定

```console
GET /bank/_search
{
  "query": { "match_all": {} },
  "_source": ["account_number", "balance"]
}
```



针对某一字段进行查询

```console
GET /bank/_search
{
  "query": { "match": { "account_number": 20 } }
}
```



返回含术语“ mill”或“ lane”的所有帐户：

```console
GET / bank / _search { “ query” ：{ “ match” ：{ “ address” ：“ mill lane” } } } 
```



它返回地址中包含短语“ mill lane”的所有帐户：

```console
GET / bank / _search { “ query” ：{ “ match_phrase” ：{ “ address” ：“ mill lane” } } } 
```



bool组合查询



地址中包含短语“ mill lane”的所有帐户

```console
GET / bank / _search { “ query” ：{ “ bool” ：{ “ must” ：[ { “ match” ：{ “ address” ：“ mill” } }，{ “ match” ：{ “ address” ：“ lane” } } ] } } } 
```



包含“ mill”和“ lane”的所有帐户：

```console
GET / bank / _search { “ query” ：{ “ bool” ：{ “ should” ：[ { “ match” ：{ “ address” ：“ mill” } }，{ “ match” ：{ “ address” ：“ lane” } } ] } } } 
```



如果一些条件只起到过滤的作用，并不需要影响到相关性得分，可以用filter



rangequery来按数组或日期的范围来过滤

```console
GET /bank/_search
{
  "query": {
    "bool": {
      "must": { "match_all": {} },
      "filter": {
        "range": {
          "balance": {
            "gte": 20000,
            "lte": 30000
          }
        }
      }
    }
  }
}
```



其他查询类型：



聚合

大致等同于SQL GROUP BY和SQL聚合函数



运行ES

```
docker run -d --name es5.6.8 -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" elasticsearch:5.6.8
```



配置ES

可以使用“[*群集更新设置”*](https://www.elastic.co/guide/en/elasticsearch/reference/5.6/cluster-update-settings.html) API在正在运行的群集上更改大多数设置 。



配置文件在config文件夹下

- `elasticsearch.yml` 用于配置Elasticsearch
- `log4j2.properties` 用于配置Elasticsearch日志记录

更改配置文件位置`./bin/elasticsearch -Epath.conf=/path/to/my/config/`

yml

```yaml
path:
    data: /var/lib/elasticsearch
    logs: /var/log/elasticsearch
```

配置集群名，默认是elasticSearch

`cluster.name: elastic-old-test`

配置节点名，默认是UUID的钱七位

`node.name: ${HOSTNAME}` 将节点名设置为hostname



安全配置

引导检查

JVM堆大小检查

文件描述符检查

内存锁检查

最大线程数检查

最大文件大小检查

虚拟内存大小检查

最大地图计数检查

客户端JVM检查

串行收集器检查



X-Pack监控组件

X-Pack是Elastic Stack扩展，它将安全性，警报，监视，报告，机器学习和图形功能捆绑到一个易于安装的软件包中



TransportCilent

作为一个外部访问者，请求ES的集群，对于集群而言，它是一个外部因素,该客户端执行HTTP请求



```
<!-- add the x-pack jar as a dependency -->
<dependency>
    <groupId>org.elasticsearch.client</groupId>
    <artifactId>x-pack-transport</artifactId>
    <version>5.6.8</version>
</dependency>
<dependency>
    <groupId>org.elasticsearch.client</groupId>
    <artifactId>transport</artifactId>
    <version>5.6.8</version>
</dependency>
```





问题

* es版本与transport不一样

SpringBoot的问题，pom中的parent回选一个它认为合适的版本

解决办法：显示执行版本`<elasticsearch.version>5.6.8</elasticsearch.version>`



* `NoNodeAvailableException[None of the configured nodes are available: [{#transport#-1}{X0iiydhrT6usopYGfOLvNA}{127.0.0.1}{127.0.0.1:9300}]`

解决办法：

添加配置`network.host: 0.0.0.0`,-e "transport.host=127.0.0.1" did not work.

Also settings should be as below. donot add the .put("client.transport.sniff", true).































































客户



3，4

2，待推送

5，6上个批次待推送















mapping

类似于类型声明，它的作用除了类型说明外，还告诉ES如何索引数据以及是否可以搜索到,**搜索数据的指令集合**。

当查询没有返回相应的数据， mapping很有可能有问题

一个mapping由一个或多个analyzer组成， 一个analyzer又由一个或多个filter(转换数据的方法)组成的

一个analyzer由一组顺序排列的filter组成，执行分析的过程就是按顺序一个filter一个filter依次调用， ES存储和索引最后得到的结果

mapping的作用就是执行一系列的指令将输入的数据转成可搜索的索引项



keyword



**默认analyzer**

 默认的analyzer是标准analyzer, 这个标准analyzer有三个filter：token filter, lowercase filter和stop token filter



都有哪些类型

* text还是keyword

这两个字段都可以存储字符串使用，但建立索引和搜索的时候是不太一样的

keyword：存储数据时候，不会分词建立索引

text：存储数据时候，会自动分词，并生成索引



用text存储就会先分词，存到分词库中，所以用term查询是查询不到的。

eg：存进去：上海市，分为上海、市，存入分词库，当用term查询"上海市"时，分词库中没有这个词，就查询不到。



* 数值，日期，还是地理位置



一个index

































'



---



是用来干什么：

​		分布式搜索引擎，建立在全文搜索引擎 Apache Lucene(TM) 基础上的搜索引擎.



与MySQL相比，ES重要的三大功能：存储，搜索(全文检索、处理同义词、通过相关性给文档评分)，分析。

我们生活中的数据总体是分为两种的：结构化数据和非结构化数据。MySQL中的数据都是有规律的(结构化数据)。

结构化数据：就是能二维表示的，比如MySQL，每一行每一列都有固定的意义。

全文检索：提取出全文中的一些信息，重新组织成一定结构来方便检索，而不是从头到尾去找。从非结构化数据中提取出的然后重新组织的信息，我们称之**索引**。



***面相文档***：

​		ES面相文档，不仅降文档作存储起来，还将文档的内容进行索引。将JSON作为文档的序列化格式。



docker下安装运行ES和kibana

1. 下载ES镜像后运行

```shell
docker run --name es7.9.3 -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" -d elasticsearch:7.9.3
```

2. 下载Kibana后运行

```shell
docker run --name kibana7.9.3 --link=elasticsearch:test  -p 5601:5601 -d kibana:7.9.3
```

然后需要重启kibana

```shell
 docker start kibana
```

访问kibana `http://localhost:5601/`

上层应用：集群，集群扩容

配置文件:
docker exec -it 469185b7abea /bin/bash cd /usr/share/kibana/config/ vi kibana.yml





***为什么移除type？***

​		es中同一个index中不同type是存储在同一个索引中的（lucene的索引文件），因此不同type中相同名字的字段的定义（mapping）必须一致。



关键词：倒排序，集群，分片，分词器，聚合操作



使用分片的持久化方案，比关系型数据库存的块，比非关系型数据库存的多



分布式存储的几个概念：

集群(Cluster)：多个节点(服务器)组成一个集群，存储所有数据。每个集群必须有有一个唯一标识名才能加入集群，默认是elasticsearch，节点中设置集群名来加入集群，***同一个网络中的节点，会加入到集群名相同的集群中***。

节点(Node)：节点也有唯一标识，这个名称默认使用UUID随机生成器，也可以自定义。设置它的集群名，假设节点之间是可以相互发现的，它们就会加入相同的集群

索引(Index)：索引就是拥有相似特性的集合。索引名小写。可以吧索引当成是一个数据库

类型(Type)：一个索引中可以有多个Type，Type是Index的逻辑分区，我们可以将拥有相同字段的Document放在相同Type中。

文档(Document)：存储的最小单位，文档是JSON格式。

***分片和副本***(shard&replacas )：一个索引可以存储超过耽搁节点存储空间大小的数据时，单个节点存储不了这么大的数据，单个索引处理数据的搜索，响应速度要慢。为了解决这个问题，ES提供了将索引划分成多片，

创建index时可以指定分片的数量，每个分片本质上是个完整独立的index，可以放置到集群中的任意节点上。

分片允许水平分隔/扩展容量，在分片（潜在地，位于多个节点上）之上进行分布式的、并行的操作，进而提高性能/吞吐量。

一个分片怎样分布，它的文档怎样聚合回搜索请求，是完全由Elasticsearch管理的，对我们来说时透明的

在网络里，某个分片/节点可能处于离线状态等故障，ES允许创建分片的一份或多分拷贝，这个拷贝叫赋值分片/直接复制。

通过复制，提高集群的高可用性，不把复制分片与原分片/主分片放在一起

默认情况下，ES每个Index有五个主分片和一个复制，

当索引中的数据很多时，多个节点一起处理要快点



Docker中实用ES+kibana

1. 创建一个自定义网络，用于将其他服务(kibana)连接到一个网络

   docker network create somenetwork 

2. 下载运行ES

   docker pull elasticsearch:版本号

   docker run -d --name elasticsearch --net somenetwork -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" 镜像名

3. 下载运行kibana，与ES在同一个网络

   docker run -d --name kibanna --net somenetwork  -p 5601:5601 a674d23325b0

# 倒排索引

搜索引擎的通常索引单位是单词

文档里出现的单词都会加到单词字典中，单词字典中每个单词记录这包含该单词的文档编号以及指向倒排列表的指针。

每个document有自己的ID，可以自己指定，也可以自动生成

**倒排列表** ：存储着term和Document的关系，记录着每个单词出现的次数和DocumentID



搜索结果和文档的相似度

***单词频率信息***

***文档频率信息***

***位置信息***  term查询







中文和英文不同，中文单词之间没有空格进行分隔，所以要使用分词器



节点与分片

节点分为master，data，client节点

ES提供分片机制，一个index可以存储在不同分片（数据容器）中，这些分片又可以存储在集群中不同节点上



















































index

可以插入：

* byte[]或String
* Map会自动转为JSON
* 使用第三方库序列化为JSON
* 使用XContentFactory.jsonBuilder()

内部最终都是转化成byte[]，所以，有byte[]形式的可以直接使用

如：

```java
import static org.elasticsearch.common.xcontent.XContentFactory.*;

XContentBuilder builder = jsonBuilder()
    .startObject()
        .field("user", "kimchy")
        .field("postDate", new Date())
        .field("message", "trying out Elasticsearch")
    .endObject()
```

也可以使用startArray(String)` and `endArray()来创建数组

```java
Strings.toString(builder) // 可以查看序列化后的String
```

```java
IndexResponse response = client.prepareIndex("twitter", "tweet", "1")
  .setSource(XContentFactory序列化的)// 或者是(JSON字符串, XContentType.JSON)
  .get();
```

***响应***

* 插入的index，type，id，状态









bool



transportClient

```java
client.prepareGet("twitter", "tweet", "1").get();
client.prepareDelete("twitter", "tweet", "1").get();
client.prepareMultiGet().add("twitter", "tweet", "1").get()// 一次add多个查询
  
```

updateByQuery

deleteByQuery



一次请求执行多个index和delete

```java
// 创建请求
BulkRequestBuilder bulkRequest = client.prepareBulk();

// either use client#prepare, or use Requests# to directly build index/delete requests
// 添加index/delete请求的bulk请求
bulkRequest.add(client.prepareIndex("twitter", "tweet", "1")
        .setSource(jsonBuilder()
                    .startObject()
                        .field("user", "kimchy")
                        .field("postDate", new Date())
                        .field("message", "trying out Elasticsearch")
                    .endObject()
                  )
        );

bulkRequest.add(client.prepareIndex("twitter", "tweet", "2")
        .setSource(jsonBuilder()
                    .startObject()
                        .field("user", "kimchy")
                        .field("postDate", new Date())
                        .field("message", "another post")
                    .endObject()
                  )
        );

BulkResponse bulkResponse = bulkRequest.get();
if (bulkResponse.hasFailures()) {
    // process failures by iterating through each bulk response item
}
```

或者使用BulkProcessor

1. 创建BulkProcessor

```java
BulkProcessor bulkProcessor = BulkProcessor.builder(
        client,  
        new BulkProcessor.Listener() {
            @Override
            public void beforeBulk(long executionId,
                                   BulkRequest request) { ... } 

            @Override
            public void afterBulk(long executionId,
                                  BulkRequest request,
                                  BulkResponse response) { ... } 

            @Override
            public void afterBulk(long executionId,
                                  BulkRequest request,
                                  Throwable failure) { ... } 
        })
        .setBulkActions(10000) 
        .setBulkSize(new ByteSizeValue(5, ByteSizeUnit.MB)) 
        .setFlushInterval(TimeValue.timeValueSeconds(5)) 
        .setConcurrentRequests(1) 
        .setBackoffPolicy(
            BackoffPolicy.exponentialBackoff(TimeValue.timeValueMillis(100), 3)) 
        .build();
```

2. 添加请求到BulkProcessor

```java
bulkProcessor.add(new IndexRequest("twitter", "tweet", "1").source(/* your doc here */));
bulkProcessor.add(new DeleteRequest("twitter", "tweet", "2"));
```

3. 使用玩后***千万要关闭BulkProcessor***

```java
// 等待所有请求完成后关闭
bulkProcessor.awaitClose(10, TimeUnit.MINUTES);
// 直接关闭
bulkProcessor.close();
```

两者都会刷新剩余



查询



滚动查询

就是在查询的时候setScroll和setSize

```java
SearchResponse scrollResp = client.prepareSearch(test)
        .addSort(FieldSortBuilder.DOC_FIELD_NAME, SortOrder.ASC)
  			// 每次查询的超时时间，单位ms
        .setScroll(new TimeValue(60000))
        .setQuery(qb)
  			// 每次滚动返回100
        .setSize(100).get(); 
do {
    for (SearchHit hit : scrollResp.getHits().getHits()) {
        //Handle the hit...
    }

    scrollResp = client.prepareSearchScroll(scrollResp.getScrollId()).setScroll(new TimeValue(60000)).execute().actionGet();
} while(scrollResp.getHits().getHits().length != 0); 
```



match，进行分词，然后查询

match_phrase，完全匹配，所有分词都要包括？

slop，match_phrase中设置slop，表示对多可以少匹配多少个分词



term查询，完全匹配，即不进行分词器分析

term属于精确匹配，只能查单个词，查询多个单词用terms，单词间是或的关系，可以使用bool的must来变and

term级别的查询

term，terms，rangeQuery，existsQuery，prefixQuery，wildcardQuery，regexpQuery，fuzzyQuery，typeQuery，idsQuery

查询多个id

```java
QueryBuilder qb = idsQuery("my_type", "type2")
    .addIds("1", "4", "100");
```





bool，联合查询

must,should,must_not，filter

must：必须完全匹配

shoud：带一个以上的条件，至少满足一个条件

must_not: 文档必须不匹配条件





QueryBuilders，用来创建查询条件

| 方法            | 描述     |
| --------------- | -------- |
| matchAllQuery() | 查询所有 |
|                 |          |
|                 |          |





搜索：

​	全文搜索：只要包含了搜索中的一个单词就命中

​	短语搜索：match_phrase，对整个短语进行匹配

*查询字符串* （*query-string*） 搜索

高亮搜索：高亮文档中复合的条件，更方便的直到这个文档为什么会命中



index的含义：

		* 存储一个文档
		* 数据库
		* 倒排索引




BoolQueryBuilder

boll查询有四个：

must，must_not，should，filter

对应BoolQueryBuilder有四个方法



QueryBuilders



QueryBuilder































数据类型

| 类型    | 说明                                           |
| ------- | ---------------------------------------------- |
| text    | 文本类型，会进行分词。不具备唯一性的字符串使用 |
| keyword | 文本类型，不会进行分词                         |
| numeric | 数字类型，可以用term匹配                       |

double、float、half_float这3种浮点型数据类型，认为-0.0和0.0是不同的值。





























bool

组合查询，将must，mustnot，should，filter组合起来









---

# Search API



查询条件可以放在queryString中或body中

search可以查询一个或多了index/type



通过body查询可选参数：

| 参数                  | 描述                                                         |
| --------------------- | ------------------------------------------------------------ |
| `timeout`             | 搜索超时，将搜索请求限制为在指定的时间值内执行，并保全过期时累积到该点的匹配。默认为无超时。 |
| `from`                | 从某个偏移量获取匹配。默认为`0`。                            |
| `size`                | 返回的点击数。默认为`10`。如果您不希望获得任何匹配，而仅关注匹配和/或聚合的数量，则将值设置为`0`有助于提高性能。 |
| `search_type`         | 要执行的搜索操作的类型。可以是 `dfs_query_then_fetch`或`query_then_fetch`。默认为`query_then_fetch`。 |
| `request_cache`       | 设置为`true`或`false`启用或禁用对`size`值为0的请求的搜索结果进行缓存，即汇总和建议（不返回最高匹配）。 |
| `terminate_after`     | 为每个分片收集的最大文档数，达到该数量时查询执行将提前终止。如果设置，响应将具有一个布尔值字段，`terminated_early`以指示查询执行是否实际上已终止。默认为no terminate_after。 |
| `batched_reduce_size` | 分片结果的数量应在协调节点上立即减少。如果请求中的分片数量可能很大，则此值应用作保护机制以减少每个搜索请求的内存开销。 |
| sort                  |                                                              |

**`search_type`和`request_cache`必须作为查询字符串参数传递。搜索请求的其余部分应在正文本身内传递**



查询的请求体通过`SearchSourceBuilder`创建



## 排序



```json
{
    "sort" : [
        { "habby" : {"order" : "asc","mode":"max"}},
        "user",
        { "name" : "desc" },
        { "age" : "desc" },
        "_score"
    ],
    "query" : {
        "term" : { "user" : "kimchy" }
    }
}
```

字段:排序规则，没有mode可以只写desc/asc

支持按数组或多字段排序mode

| 数组值   | 描述                                                       |
| -------- | ---------------------------------------------------------- |
| `min`    | 选择最低值。                                               |
| `max`    | 选择最高的价值。                                           |
| `sum`    | 使用所有值的总和作为排序值。仅适用于基于数字的数组字段。   |
| `avg`    | 使用所有值的平均值作为排序值。仅适用于基于数字的数组字段。 |
| `median` | 使用所有值的中位数作为排序值。仅适用于基于数字的数组字段。 |



***支持按内嵌对象的字段排序***

```json
{
   "query" : {
      "term" : { "product" : "chocolate" }
   },
   "sort" : [
       {
          "offer.price" : {
             "mode" :  "avg",
             "order" : "asc",
             "nested_path" : "offer",
             "nested_filter" : {
                "term" : { "offer.color" : "blue" }
             }
          }
       }
    ]
}
```

​	

***missing value***

```json
{"missing" : "_last"} 指定缺少字段的排序怎么处理，可以是first/last默认last	
```



***unmapped_type***

elasticsearch里面一个index下面的field是共用的,unmapped_type表示每一这个字段的映射时怎么排序

```json
{
    "sort" : [
        { "price" : {"unmapped_type" : "long"} }
    ],
    "query" : {
        "term" : { "product" : "chocolate" }
    }
}
```



sourceFIltering

过滤返回的source

```json
{
    "_source": {
        "includes": [ "obj1.*", "obj2.*" ],
        "excludes": [ "*.description" ]
    },
    "query" : {
        "term" : { "user" : "kimchy" }
    }
}
```



***post_filter?***

后置过滤器



***highlight***

高亮查询结果中的一个或多个字段

SearchType，ES的搜索类型

***query and fetch***

向所有分片发送请求，各分片返回文档后进行计算和排序

优点：查的快

缺点：查询的量可能是要求的n倍



***query then fetch***

ES默认的搜索模式向所有分片发送请求，各分片只返回文档Id和排名，然后再重新排名，取前个，然后根据id取document

优点：数据量准确

缺点：性能一般，排名不太准确



***DFS query then fetch***



***DFS query and fetch***



DFS就是在请求前先将所有



滚动查询

一次查询可能会返回大量的结果，就想传统数据库的游标。

滚动不是用于实时的查询，而是用于处理大量的数据







HTTP客户端

分为高版本和低版本



一个RestClient，通过RestClient的静态方法builder(HttpHost...)创建对应的RestClientBuilder。参数只需要HttpHost。

```java
RestClient restClient = RestClient.builder(
        new HttpHost("localhost", 9200, "http"),
        new HttpHost("localhost", 9201, "http")).build();
```

RestClient是线程安全的。

如果可以，可释放它的资源`restClient.close()`



可以通过RestClientBuilder添加设置

```java
RestClientBuilder builder = RestClient.builder(new HttpHost("localhost", 9200, "http"));
Header[] defaultHeaders = new Header[]{new BasicHeader("header", "value")};
builder.setDefaultHeaders(defaultHeaders);
builder.setMaxRetryTimeoutMillis(10000);

```



QueryBuilders

封装查询条件





























想要精确查询，使用term并且字段类型时keyword

trem可能会匹配到分词后的





match与term



match_phrase与match

两者都会分词，但是match_phrase要求分词有顺序

例如，对于text类型的字段`hello word`

通过match_phrase/match查hello/word时，都会查到

通过match_phrase/match查hello word时，都会查到

通过match_phrase/match查word hello时，match可以，但是match_phrase不行，应为查询的顺序和存的信息不一样

即match_phrase用来精确查询字段为text类型的值



query_string与match_phrase





全文索引

match 

match queries accept text/numerics/dates

查询的text会进行分词来构建查询语句，默认分词器根据字段类型来选择，默认关系是or，should匹配的最小数量可以通过minimum_should_match参数来指定。

The `lenient` parameter can be set to `true` to ignore exceptions caused by data-type mismatches, such as trying to query a numeric field with a text query string. Defaults to `false`



 The node setting named `indices.query.bool.max_clause_count` (defaults to 1024) is applied to boolean queries in order to limit the number of terms per query.It is also possible to limit the expansion of phrase queries with the JVM option `-Des.query.apply_graph_phrase_limit=true`, when activated the `indices.query.bool.max_clause_count` is applied to phrase queries expansions. Elasticsearch will stop observing this system property in 6x



match是基本的全文查询，包含了模糊查询，分词查询和接近性查询。







* Search
* Query
* mapping
* aggregations





# aggregations

基于查询结果进行数据计算。用来分析查询到的document的信息



通常将聚合分成四个系列

* bucketing

  一个水桶对应一个key/标准，执行聚合时，所有水桶都去查询结果中找document，匹配上的document放入水桶，最后我们得到了多个水桶。Bucketing aggregations can have sub-aggregations (bucketing or metric)

* metric 指标聚合

  进行数学统计，对文档中的字段进行分析，如求平均值，最值等

* matrix，在多个字段上进行操作，如同时求平均值和最值，会根据获得的结果产生一个矩阵，此聚合系列尚不支持脚本

* pipeline，聚合其他聚合的聚合结果



**聚合的基本结构：**

```js
"aggregations" : {
    "<aggregation_name>" : {  // 逻辑命会作为identify
        "<aggregation_type>" : {
            <aggregation_body>
        }
        [,"meta" : {  [<meta_data_body>] } ]?
        [,"aggregations" : { [<sub_aggregation>]+ } ]?
    }
    [,"<aggregation_name_2>" : { ... } ]*
}
```

aggregations可以简写成aggs

aggregation_name用来识别响应中的聚合

aggregation_type就是



所以，聚合用aggs指定，与query同级



## metric



指标聚合，用来计算document中的一些指标(平均值，最值等)



###  Avg Aggregation

* single-value
* These values can be extracted either from specific numeric fields in the documents, or be generated by a provided script.

```json
"aggs" : {
        "grade_avg" : {
            "avg" : {
                "field" : "grade",
                "missing": 10 
            }
        }
    }
```

missing表示缺失时的默认值



###  Cardinality Aggregation

* single-value
* 类似SQL中的distinct，计算一个字段有几个不同值(近似的)
* 精确度由precision_threshold控制，以内存为代价，低于该值时是近似准确的，超过该值时将是模糊的。最大值是40000，默认值是3000

```json
{
    "aggs" : {
        "tag_cardinality" : {
            "cardinality" : {
                "field" : "tag",
                "missing": "N/A" 
            }
        }
    }
}
```

document中没有tag字段对应的值时，进入N/A的bucket中

**count是模糊的**

Computing exact counts requires loading values into a hash set and returning its size.

This cardinality aggregation is based on the HyperLogLog++ algorithm.

精确度设置为c就需要c*8byte



###  Stats Aggregation

* multi-value
* 统计总数，平均值，最值，总和
* 聚合的名字会作为桶的名字



```json
{
    "aggs" : {
        "grades_stats" : {
            "stats" : {
                "field" : "grade",
                "missing": 0 
            }
        }
    }
}
```



###  Extended Stats Aggregation

* multi-value

* 与stat类似，添加了sum_of_squares，variance，std_deviation，std_deviation_bounds，upper，lower



| stats返回的字段      | 描述                  |
| -------------------- | --------------------- |
| sum_of_squares       | 平方和                |
| variance             | 方差                  |
| std_deviation        | 标准偏差              |
| std_deviation_bounds | 平均值加/减两个标准差 |



```json
{
    "aggs" : {
        "grades_stats" : {
            "extended_stats" : {
                "field" : "grade",
                "missing": 0 
            }
        }
    }
}
```



### MAX Aggregations

```json
{
    "aggs" : {
        "grade_max" : {
            "max" : {
                "field" : "grade",
                "missing": 10 
            }
        }
    }
}
```



###  Min Aggregation



```json
{
    "aggs" : {
        "min_price" : { "min" : { "field" : "price" } }
    }
}
```



###  Sum Aggregation

* single-value
* 计算字段值的和

```json
{
"aggs" : {
        "hat_prices" : {
            "sum" : {
                "field" : "price",
                "missing": 100 
            }
        }
    }
}
```



###  Top hits Aggregation

取最相关的前几个，常用来做子聚合





###  Value Count Aggregation

* single-value
* 计算字段值的个数

```json
{
    "aggs" : {
        "types_count" : { "value_count" : { "field" : "type" } }
    }
}
```



###  Percentiles Aggregation

* multi-value
* 计算百分位数



###  Percentile Ranks Aggregation



### Geo Bounds Aggregation

用于geo-point类型的字段

计算出该字段所有地理坐标点的边界



###  Geo Centroid Aggregation

* 用于geo-point类型字段）
* 计算所有坐标的加权重心。



## Bucket Aggregations



桶聚合不计算指标，只是不同的文档放入不同的桶中，返回每个桶的名字和匹配到的document的数量.

**不进行字段计算，只按照字段进行分组**

有的创建一个桶，有的创建固定数量的桶，有的根据执行情况动态的创建桶



### Filter Aggregation

Silgle-bucket

```
POST /sales/_search?size=0
{
    "aggs" : {
        "t_shirts" : {
            "filter" : { "term": { "type": "t-shirt" } },
            "aggs" : {
                "avg_price" : { "avg" : { "field" : "price" } }
            }
        }
    }
}
```

匹配Filter的进入桶中，可以在桶中进行子聚合



###  Filters Aggregation

mutil-bucket

```
GET logs/_search
{
  "size": 0,
  "aggs" : {
    "messages" : {
      "filters" : {
        "filters" : {
          "errors" :   { "match" : { "body" : "error"   }},
          "warnings" : { "match" : { "body" : "warning" }}
        }
      }
    }
  }
}
```

一个filter创建一个容器，上面会创建两个bucket：errors,warnings



为什么有两个Filters可以看看下面的矩阵bucket



匿名filter

```
GET logs/_search
{
  "size": 0,
  "aggs" : {
    "messages" : {
      "filters" : {
        "filters" : [
          { "match" : { "body" : "error"   }},
          { "match" : { "body" : "warning" }}
        ]
      }
    }
  }
}
```

匿名filter中，fliters是包含条件的数组，返回的bucket与条件的顺序一样



Other_bucket

```
GET logs/_search
{
  "size": 0,
  "aggs" : {
    "messages" : {
      "filters" : {
        "other_bucket_key": "other_messages",
        "filters" : {
          "errors" :   { "match" : { "body" : "error"   }},
          "warnings" : { "match" : { "body" : "warning" }}
        }
      }
    }
  }
}
```

filters中的filters是条件，other_bucket_key来指定other_bucket桶的名字(也就开启了other_bucket)

```js
{
    "aggregations" : {
        "genres" : {
            "doc_count_error_upper_bound": 0, // 文档计数的最大偏差值
            "sum_other_doc_count": 0,         // 未返回的其他项的文档数
            "buckets" : [ 
                {
                    "key" : "jazz",
                    "doc_count" : 10
                },
                {
                    "key" : "rock",
                    "doc_count" : 10
                },
                {
                    "key" : "electronic",
                    "doc_count" : 10
                },
            ]
        }
    }
}
```



### **adjacency_matrix**

multi-bucket

返回一个矩阵，提供一系列filter expressions，每个桶代表矩阵中的一个单元格



提供三个filterA,B,C，

返回的桶(想象一下矩阵)：

A,B,C, AB,AC,BC，即我们提供了三个Filter，最后会返回六个桶

这几个桶分别表示，满足A,B,C的三个桶和同时满足交叉两个桶的桶

默认情况下为100个filter，可以使用`index.max_adjacency_matrix_filters`索引级设置更改此设置。



###  Terms Aggregation

* Multi-bucket

* 动态的创建桶(值不一样时创建)，即指定的字段，**每个值都创建一个桶**
* 返回的顺序默认按照问的文档个数

```
{
    "aggs" : {
        "genres" : {
            "terms" : { "field" : "genre" }，
             "size" : 10,          //size用来定义需要返回多个 buckets（防止太多），默认会全部返回。
              "order" : { "_count" : "asc" },
               "min_doc_count": 10,            //只返回文档个数不小于该值的 buckets
        }
    }
}
```





###  Missing Aggregation

Single-bucket

指定字段，缺失字段值得会进入桶中(没有字段或者字段值为NULL)

```console
POST /sales/_search?size=0
{
    "aggs" : {
        "products_without_a_price" : {
            "missing" : { "field" : "price" }
        }
    }
}
```



###  Nested Aggregation

* single-bucket
* aggregating nested documents

​	

###  Histogram Aggregation

* multi-bucket
* can be applied on numeric values
* 根据值动态构建固定大小（也称为间隔）的存储桶

比如一个字段：商品的价格，我们把建个设置为5，那么价格为32的document将会装进桶30中，

0，5，10······所以文档的bucket_key= 值/间隔的桶中

```js
POST /sales/_search?size=0
{
    "aggs" : {
        "prices" : {
            "histogram" : {
                "field" : "price",
                "interval" : 50
            }
        }
    }
}
```

得到的响应

```json
{
    ...
    "aggregations": {
        "prices" : {
            "buckets": [
                {
                    "key": 0.0,
                    "doc_count": 1
                },
                {
                    "key": 50.0,
                    "doc_count": 1
                },
                {
                    "key": 100.0,
                    "doc_count": 0
                },
                {
                    "key": 150.0,
                    "doc_count": 2
                },
                {
                    "key": 200.0,
                    "doc_count": 3
                }
            ]
        }
    }
}
```

可以看到，100里没有document，聚合中可以设置min_doc_count，如果`min_doc_count` 大于0，则将不返回空bucket

```console
"histogram" : {
                "field" : "price",
                "interval" : 50,
                "min_doc_count" : 1
            }
```

###  Date Histogram Aggregation

mutil-bucket

只能用于Date类型**

Histogram：柱状图

dates are represented in elasticsearch internally as long values



###  Range Aggregation

multi-bucket

[from, to)

```js
{
    "aggs" : {
        "price_ranges" : {
            "range" : {
                "field" : "price",
                  "keyed" : true,  # 返回桶的名字会是一个区间
                "ranges" : [
                    { "to" : 50 },
                    { "from" : 50, "to" : 100 },
                    { "from" : 100 }
                ]
            }
        }
    }
}
```

[0,50)，[50, 100), 大于等于100，一共三个桶



###  Date Range Aggregation

* dedicated for date values

* The main difference between this aggregation and the normal range aggregation is that the `from` and `to` values can be expressed in Date Math expressions, and it is also possible to specify a date format by which the `from` and `to` response fields will be returned. Note that this aggregation includes the `from` value and excludes the `to` value for each range.

```console
POST /sales/_search?size=0
{
    "aggs": {
        "range": {
            "date_range": {
                "field": "date",
                "format": "MM-yyy",
                "ranges": [
                    { "to": "now-10M/M" }, 
                    { "from": "now-10M/M" } 
                ]
            }
        }
    }
}
```

两个桶:10月前的，十月前以后的

**Date Format/Pattern**

https://www.elastic.co/guide/en/elasticsearch/reference/5.6/search-aggregations-bucket-daterange-aggregation.html



###  IP Range Aggregation

dedicated for IP

```js
{
    "aggs" : {
        "ip_ranges" : {
            "ip_range" : {
                "field" : "ip",
                "ranges" : [
                    { "to" : "10.0.0.5" },
                    { "from" : "10.0.0.5" }
                ]
            }
        }
    }
}
```

与range相同，keyed字段可以给bucket的名字编程区间

```js
{
    "aggs": {
        "ip_ranges": {
            "ip_range": {
                "field": "remote_ip",
                "ranges": [
                    { "to" : "10.0.0.5" },
                    { "from" : "10.0.0.5" }
                ],
                "keyed": true
            }
        }
    }
}
```

自定义bucket的名字

```js
{
    "aggs": {
        "ip_ranges": {
            "ip_range": {
                "field": "remote_ip",
                "ranges": [
                    { "key": "infinity", "to" : "10.0.0.5" },
                    { "key": "and-beyond", "from" : "10.0.0.5" }
                ],
                "keyed": true
            }
        }
    }
}
```



### Children Aggregation

* Single-bucket

* This aggregation has a single option:type



##  Pipeline Aggregations



* 聚合其他聚合的输出而不是document
* 管道聚合不能包含子聚合
* **实验性的，可能高版本会移除**

























eg:

***求平均值***

```console
POST /exams/_search?size=0
{
    "aggs" : {
        "grade_avg" : {
            "avg" : {
                "field" : "grade",
                "missing": 10 
            }
        }
    }
}
```

missing表示缺失字段的放到哪个桶里

***计数***

统计distinct数量

```console
POST /sales/_search?size=0
{
    "aggs" : {
        "tag_cardinality" : {
            "cardinality" : {
                "field" : "tag",
                "missing": "N/A" 
            }
        }
    }
}
```

***精华统计***

```js
{
    "aggs" : {
        "grades_stats" : {
            "extended_stats" : {
                "field" : "grade",
                "missing": 0 
            }
        }
    }
}
```

是个`multi-value`metrics，返回平均值，最值，总和，总数等信息

***求最值***

```console
POST /sales/_search
{
    "aggs" : {
        "grade_max" : {
            "max" : {
                "field" : "grade",
                "missing": 10 
            }
        }
    }
}
```

最值是`single-value` metrics

***百分位数统计***

`multi-value` metrics

```js
{
    "aggs" : {
        "grade_percentiles" : {
            "percentiles" : {
                "field" : "grade",
                  "missing": 10 
            }
        }
    }
}
```



**百分数排名**

`multi-value` metrics

```js
{
    "aggs" : {
        "load_time_outlier" : {
            "percentile_ranks" : {
                "field" : "load_time", 
                "values" : [15, 30]
            }
        }
    }
}
```

表示值达到15和30的各占百分之几



***terms***

```js
{
    "aggs" : {
        "genres" : {
            "terms" : { "field" : "genre" }
        }
    }
}
```

ganre字段一样的一个桶，相当于按字段分组



***range***

```js
{
    "aggs" : {
        "price_ranges" : {
            "range" : {
                "field" : "price",
                "ranges" : [
                    { "to" : 50 },
                    { "from" : 50, "to" : 100 },
                    { "from" : 100 }
                ]
            }
        }
    }
}
```

一个range一个桶

还可以跟每个range起个名字

```js
{
    "aggs" : {
        "price_ranges" : {
            "range" : {
                "field" : "price",
                "keyed" : true,
                "ranges" : [
                    { "key" : "cheap", "to" : 50 },
                    { "key" : "average", "from" : 50, "to" : 100 },
                    { "key" : "expensive", "from" : 100 }
                ]
            }
        }
    }
}
```

子聚合

```js
{
    "aggs" : {
        "price_ranges" : {
            "range" : {
                "field" : "price",
                "ranges" : [
                    { "to" : 50 },
                    { "from" : 50, "to" : 100 },
                    { "from" : 100 }
                ]
            },
            "aggs" : {
                "price_stats" : {
                    "stats" : { "field" : "price" }
                }
            }
        }
    }
}
```



使用：

1. 获得bucket

   有几种：range，terms，Date Range，











































db.user.find({"intentionList":{$elemMatch:{"name":"上海"}}}).pretty()



# 查询

两类：查询



## **全文查询**

全文检索在查找前会进行分词



**match**

```
GET /_search
{
    "query": {
        "match" : {
            "message" : "this is a test"
        }
    }
}
```

会进行分词，**分词间的关系默认是or**



**match_phrase**

用于匹配精确短语，**分词间的关系是and，并且出现的顺序要一致**



**match_phrase_prefix**



**multi_search**



**common_terms**

为了解决的问题：

会分成两组，更重要一些的和不重要的





**query_string**

**分词间的关系是and，不要求出现的顺序要一致**

**simple_query_string**



**匹配所有doc**

match_all



## 精确



term

字段=值



terms

字段=提供的一组值中的一个就行(每个值不会进行分词)

类似in



Range

用于数字或日期



exists

判断某个字段是否有值

* 

* 空字符串也相当于没有值
* 只含有-,_等用来分词的符号的，也属于没有值





























```
{
                    "range": {
                        "role1Es": {
                            "gte": 90
                        }
                    }
                },
                {
                    "bool": {
                        "should": [
                            {
                                "exists": {
                                    "field": "imeiEs"
                                }
                            },
                            {
                                "exists": {
                                    "field": "macEs"
                                }
                            }
                        ]
                    }
                }
```











































单条件查询：query中直接放查询条件

```
{
    "query":{
        "term":{
            "userid":"4707034794"
        }
    }
}
```

多条件查询，query里通过bool的四个查询组合起来，

```json
"size": 0,
    "query": {
        "bool": {
            "filter": [
                {
                    "term": {
                        "instanceId": "736"
                    }
                },
                {
                    "term": {
                        "eventDay": "20210117"
                    }
                }
            ]
        }
    }
```





# ES集群



* 节点
* 分片
* 集群名
* Http端口和TCP端口

































节点的角色

master：

data：

ingest：

一个节点可以有多个角色



**Java数据类型与ES的转换**



**集合**





**TransportCilent**

用来远程连接ES集群，它



**依赖**

```xml
<dependency>
    <groupId>org.elasticsearch.client</groupId>
    <artifactId>transport</artifactId>
    <version>5.6.16</version>
</dependency>
```



springboot会选择一个合适的elasticesearch的版本，所以springboot版本太高时，elasticsearch的版本会比transport的高



解决办法：

1. 降低springboot的版本
2. 显式指明elasticesarch的版本

```xml
<properties>
    <java.version>1.8</java.version>
    <elasticsearch.version>5.6.8</elasticsearch.version>
</properties>
```



创建TransportCilent

```java
TransportClient client = new PreBuiltTransportClient(Settings.EMPTY)
        .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("host1"), 9300))
        .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("host2"), 9300));
```

* TransportCilent并不加入集群，只获取一个或多个传输地址。
* 如果ES集群的名字不是"elasticsearch"，需要设置集群名称

```java
Settings settings = Settings.builder()
        .put("cluster.name", "myClusterName").build();
```



java配置

```java
@Configuration
public class EsCilentConfig {

    @Bean
    public TransportClient client() throws UnknownHostException {
        // Note that you have to set the cluster name if you use one different than "elasticsearch"
        Settings settings = Settings.builder()
                .put("cluster.name", "my-application")
                .build();
        return new PreBuiltTransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("10.124.119.21"), 8513));
    }
}
```





**TransportClient常用方法**



以prepare开头的比没有prepare开头的封装程度高



**index**

index的两种方式

1. index一个JSON字符串

```java
client.prepareIndex("daily_query","query_info").setSource(json, XContentType.JSON).get();
```

2. 通过ES的jsonBuilder

```java
 client.prepareIndex("daily_query", "query_info").setSource(
                jsonBuilder().startObject()
                        .field("instanceId", 903)
                        .field("role", 1)
                        .field("userid", "2183427222")
                        .field("eventDay", "20210120")
                        .endObject()
        ).get();
```



**update**

```java
client.prepareUpdate("daily_query", "query_info", "AXhtPyNWxqKbJFC1zMYN")
                .setDoc(json, XContentType.JSON).get();
```



**查询**

通过TermsQueryBuilders来创建TermsQueryBuilder

```java
TermsQueryBuilder query = QueryBuilders.termsQuery("instanceId", "903");
        SearchResponse response = client.prepareSearch("daily_query")
                .setTypes("query_info")
                .setQuery(query)
                .setSize(100)
                .get();
        System.out.println(response.getHits().getHits().length);
```



**滚动查询**

```java
    @Test
    public void scrollTest() {
        TermsQueryBuilder query = QueryBuilders.termsQuery("instanceId", "903");
        SearchResponse response = client.prepareSearch("daily_query")
                .setTypes("query_info")
                .setQuery(query)
                .setScroll(TimeValue.MINUS_ONE)
                .setSize(100)
                .get();
        do {
            for (SearchHit hit : response.getHits().getHits()) {
                System.out.println(hit.getSource());
            }
            response = client.prepareSearchScroll(response.getScrollId()).setScroll(TimeValue.MINUS_ONE).get();
        } while (response.getHits().getHits().length > 0);
    }
```

滚动查询与普通查询相比，多设置了一个Scroll，告诉ES这次查询的上下文要保存多长时间，获得第一页，然后再用滚动查询去获得剩下的。





**批量操作**



批量增删改

通过TransportClient的PrepareBulk获得批量操作对象，然后将增删改的请求添加到对象，然后提交

1. 获得批量操作对象

`client.prepareBulk()`

2. 添加请求

`client.prepareBulk().add(增删改请求)`

3.  提交批量处理操作

`client.prepareBulk().add(request1).add(request2).get();`



批量查

通过TransportClient的MultiSearch

```java
MultiSearchRequest multiSearchRequest = new MultiSearchRequest();
multiSearchRequest.add(searchRequest);
client.multiSearch(multiSearchRequest).get();
```



**聚合**







批量查询





commonTerms与terms

commonTerms是全文查询

terms是精确查询



fuzzy query

当用户输入有错误时，使用这个功能能在一定程度上召回一些和输入相近的文档。



**mapping创建错怎么办？**

reIndex

reIndex只是将文本进行赋值，不会复制mapping，counts, replicas, etc.



```shell
POST _reindex
{
  "source": {
    "index": "twitter"
  },
  "dest": {
    "index": "new_twitter"
  }
}


```



