package com.pk.demo.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.List;

@Data
public class Intention {

    @Id
    private int id;
    private String name;
    private List<String> level2Intention;
}
