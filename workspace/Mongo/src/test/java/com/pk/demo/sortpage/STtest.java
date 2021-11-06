package com.pk.demo.sortpage;

import com.pk.demo.entity.Intention;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

@SpringBootTest
public class STtest {

    @Autowired
    private MongoOperations mongo;

    @Test
    public void test() {
        Query query = new Query();
        query.with(Sort.by(new Sort.Order(Sort.Direction.fromString("ASC"),"id")));
        query.with(PageRequest.of(1,2));
        List<Intention> intentions = mongo.find(query, Intention.class);
        System.out.println(intentions);
    }
}
