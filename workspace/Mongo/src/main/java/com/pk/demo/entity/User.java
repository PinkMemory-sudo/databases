package com.pk.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 索引
 * 注解
 * 嵌套
 * 关联
 */
@Data
@Document("user")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {
    @Id
    @JsonIgnore
    private String id;
    private String name;
    @DBRef
    private Company company;
    @DBRef
    private Home home;
}
