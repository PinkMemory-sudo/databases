**慢SQL查询优化**

**优化思路,步骤**





## 聚簇索引

B+树的节点中存的是索引值(一般是主键)，叶子节点存储着所有数据。

普通索引的叶子节点存的是PK值(一般是主键)



## 回表



## 索引覆盖

只需要在一棵索引树上就能获取SQL所需的所有列数据，无需回表



## 索引下推

MySQL 5.6引入了索引下推优化，可以在索引遍历过程中，**对索引中包含的字段先做判断，过滤掉不符合条件的记录，减少回表字数**

