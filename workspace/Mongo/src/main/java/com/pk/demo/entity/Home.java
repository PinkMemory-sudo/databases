package com.pk.demo.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("home")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Home {
    @Id
    private String id;
    private String address;
}
