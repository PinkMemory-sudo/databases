离线安装





集群搭建(离线)

下载jar包

https://www.elastic.co/cn/downloads/past-releases/elasticsearch-5-6-8

2. 解压

3. 修改配置文件
4. 启动



**配置文件**

与springboot的自动配置一样，可以使用默认的配置，需要修改哪些配置再在elasticsearch.yml中添加

常用elasticsearch配置

```yaml

```









全文检索和分析引擎？

使用场景

* 用户搜索补全

* 日志的采集，存储和分析
* reverse-search，价格更新时通知用户



**Near Realtime (NRT)**

接近实时，从插入一条数据到可以查询需要一段时间







# Index



**创建index**

```console
PUT /index_name?pretty
```

ES并不要求先创建index，在第一次index的时候会自动创建



**插入一个document**



**指定id**(存在时替换)

```console
PUT /index/type/id?pretty
{
	document
}
```

id是可选的，自动生成Id时使用POST方法

```console
POST /index/type?pretty
```



**删除index**

```console
DELETE /customer?pretty
```



**更新document**

```console
POST /index/type/1/_update?pretty
{
  "doc": { "字段名": "字段值",... }
}
```



也可以通过脚本更新，ctx._source来获得当前的document

```console
POST /customer/external/1/_update?pretty
{
  "script" : "ctx._source.age += 5"
}
```



UpdateByQuery



**删除文档**

```console
DELETE /customer/external/2?pretty
```





**批量增删改**

通过_bulk接口和buil文件

接口：`POST /customer/external/_bulk?pretty`

bulk文件的格式,第一行为操作类型，第二行是数据(删除的话只需要一行,不需要数据）

```
{"index":{"_id":"1"}}
{"name": "John Doe" }
{"index":{"_id":"2"}}
{"name": "Jane Doe" }
{"delete":{"_id":"2"}}
```

批量 API 不会因为其中一项操作失败而失败，bulk的返回将提供每个操作的状态

# 配置































# Mapping