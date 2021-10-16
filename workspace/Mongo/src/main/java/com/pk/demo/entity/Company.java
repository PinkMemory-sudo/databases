package com.pk.demo.entity;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("company")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Company {
    @Id
    private String id;
    private String name;
}
