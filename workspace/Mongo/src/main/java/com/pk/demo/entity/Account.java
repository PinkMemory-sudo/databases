package com.pk.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 索引
 * 注解
 * 嵌套
 * 关联
 */
@Data
@Document("push_account")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Account {
    @Id
    @JsonIgnore
    private String id;

    // 要推送的账号
    private String accountId;

    // 推过的人多久不重复，单位为天.
    private int repeatDuration;

    // 该帐号对应的传输文件的管道，对于江干来说，应该配置为江干老项目的transId
    private int transId;
}
