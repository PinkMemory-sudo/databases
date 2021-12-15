**ES查询文档的过程**



什么是相关性得分**



**精确匹配和全文检索的不同**



# 基本查询

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





match，match_phrase



# **组合查询**



must

must_not

should

filter

```json
{
  "query": {
    "bool": {
      "must_not": [
        { "match": { "address": "mill" } },
        { "match": { "address": "lane" } }
      ]
    }
  }
}
```

