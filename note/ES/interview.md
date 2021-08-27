**一条数据如何存储到对应的shard上**

肯定不能是随机的，不然查询的时候就不知道去哪些shards寻找了

分片时采用的算法;

```
shard_num = hash(_routing) % num_primary_shards
```

 `_routing` 是一个可变值，默认是文档的 `_id` 的值

