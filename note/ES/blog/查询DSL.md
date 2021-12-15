**ES查询文档的过程**



什么是相关性得分**



**精确匹配和全文检索的不同**



```
GET /bank/_search
{
  "query": { "match_all": {} },
  "from": 10,
  "size": 10,
  "sort": { "balance": { "order": "desc" } }
}
```







查询结果默认返回10条