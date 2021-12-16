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



# API Conventions



## **Multiple Indices**

* 大多数API支持多个index查询(index逗号隔开，_all表示ALL)
* index支持通配符(*)
* 可以通过+-添加/排除index



## **Date math support in index names**



##  **Common options**



**human=false**



**filter _ path**

* 该参数可用于减少 elasticsearch 返回的响应,指定都返回什么

* 支持通配符
* 可以通过-排除不要返回的数据。包含和排除同时存在时，先进行排除，在用结果进行包含过滤



**flat_settings**

默认返回的是JSON，如果想打平看的话需要此操作















































