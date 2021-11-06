# 备份与恢复



**备份user_ex_info表**

```
mongodump -uroot -pMingjing_110 --authenticationDatabase admin \
-d mingjing_private_auth_server \
-c user_ex_info \
-o /data/bak
```



**恢复user_ex_info表**

1. 清空user_ex_info

```
db.user_ex_info.remove({})
```

2. 导入user_ex_info表

```
mongorestore -uroot -pMingjing_110 --authenticationDatabase admin \
-d mingjing_private_auth_server \
-c user_ex_info \
/data/bak/mingjing_private_auth_server/user_ex_info.bson
```



# 修改智能预警数量



进入Mongo，执行以下代码

```
// 统计开通过智能预警的客户
use mingjing_private_auth_server
// 保存开通过智能预警权限的客户名
var warnAccountList=[]
db.user_ex_info.find({"authorizedServices":"WARN"},{"_id":1}).forEach(
    function(doc){
        printjson(doc)
        warnAccountList.push(doc._id)
    }
)

// 统计账户添加过多少重点人
use intention_target_db
// 账户名与添加数量的映射
var cmdLine={}
db.target_person.aggregate([
    {
        "$group": {
            "_id":
                "$clientId"
            ,
            "unique": {
                "$addToSet": "$outUid"
            }
        }
    },
    {
        "$project": {
            "total": {
                "$size": "$unique"
            }
        }
    }
]).forEach(
    function(doc){
        printjson(doc)
        cmdLine[doc._id]=doc.total
    }
)

use mingjing_private_auth_server
for (var i = warnAccountList.length - 1; i >= 0; i--) {
    accountName=warnAccountList[i]
    if (cmdLine[accountName]) {
        db.user_ex_info.update({'_id':accountName},{$set:{'accountWarnInfo':{'warnNumMax':50000,'warnNumUsed':cmdLine[accountName]}}})
    }else{
        // 没有添加过重点人的客户，已使用人数设为0
        db.user_ex_info.update({'_id':accountName},{$set:{'accountWarnInfo':{'warnNumMax':50000,'warnNumUsed':0}}})
    }
}
```



 





```
auth_server_online
intention_target_db
```

