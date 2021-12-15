**查看集群状态**

| api                    | 描述                |
| ---------------------- | ------------------- |
| /cat                   | 查看都有哪些cat接口 |
| /_cat/health?v         | 查看集群状态        |
| /_cat/shards/{index}?v | 查看分片状态        |
| /_cat/nodes?v          | 查看节点状态        |
| /_cat/indices?v        | 查看index状态       |



**集群的状态**

* Green：everything is ok
* Yellow：有些副本出现问题了(es整体功能没有收到影响，还能继续使用)
* Red：有些分片和副本都没了，不能再用了(可用的分片还可以提供查询服务)