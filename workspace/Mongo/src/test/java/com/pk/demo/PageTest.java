package com.pk.demo;

import com.mongodb.bulk.BulkWriteResult;
import com.pk.demo.entity.Account;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;


@SpringBootTest
public class PageTest {

    @Autowired
    private MongoTemplate mongo;

    @Test
    public void pageTest() {
        Pageable pageable = PageRequest.of(1, 2);
        Query query = new Query();
        query.with(pageable);
        List<Account> list = mongo.find(query, Account.class);
        System.out.println(list);
    }
}
