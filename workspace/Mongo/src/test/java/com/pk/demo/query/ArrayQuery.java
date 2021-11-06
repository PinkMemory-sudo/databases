package com.pk.demo.query;

import com.pk.demo.entity.Intention;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class ArrayQuery {

    @Autowired
    private MongoOperations mongo;


    @Test
    public void add() {
        Intention intention = new Intention();
        intention.setId(3);
        intention.setName("涉稳版");
        List<String> level2List = new ArrayList<>();
        level2List.add("上访");
        intention.setLevel2Intention(level2List);
        mongo.insert(intention);
    }

    @Test
    public void arrayFind() {
        List<String> intentionList = new ArrayList<>();
        intentionList.add("制毒");
        intentionList.add("上访");
        Query query = Query.query(Criteria.where("level2Intention").in(intentionList));
        List<Intention> intentions = mongo.find(query, Intention.class);
        System.out.println(intentions);
    }

    @Test
    public void orTest(){
        Criteria criteria = new Criteria();
        criteria = criteria.orOperator(Criteria.where("level2Intention").is("上访"), Criteria.where("level2Intention").is("制毒"));
//        Query query = Query.query(criteria);
        Query query = new Query();
        query.addCriteria(Criteria.where("level2Intention").is("吸毒"));
        query.addCriteria(Criteria.where("name").is("涉毒"));

        System.out.println(mongo.find(query, Intention.class));
    }

    @Test
    public void regexTest(){
        Criteria criteria = new Criteria();

        Query query = Query.query(criteria.not().and("name").regex("^.*毒.*$"));
        System.out.println(mongo.find(query, Intention.class));
    }

}
