**ES版本**



**安装启动ES**



**ES的不同安装包**



**ES支持哪些配置管理工具**



**节点，分片的关系**



**ES的type**



**mapping的作用**



**副本的好处**



**怎么创建索引**



**更新mapping**



**怎么删除索引**



**怎么查看所有索引**



**常用的cat命令**



**怎么根据Id进行检索**



**什么是相关性得分**



**精确匹配和全文检索的不同**



**聚合**



**什么是Elasticsearch Analyze**



**怎么使用Elasticsearch Tokenizer**



**token filter是怎么工作的**



**ingest节点是怎么工作的**



**ES的Rest API有哪些优势**



**什么是X-Pack**



**X-Pack常用API命令**



**ES字段的主要类型**



















**一条数据如何存储到对应的shard上**

肯定不能是随机的，不然查询的时候就不知道去哪些shards寻找了

分片时采用的算法;

```
shard_num = hash(_routing) % num_primary_shards
```

 `_routing` 是一个可变值，默认是文档的 `_id` 的值



