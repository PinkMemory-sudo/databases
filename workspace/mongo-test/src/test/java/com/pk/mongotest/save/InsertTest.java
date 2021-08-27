package com.pk.mongotest.save;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

@SpringBootTest
public class InsertTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    public void insertOne(){

    }

    /**
     * 批量添加
     */

    /**
     * 根据Id替换或添加
     */
}
