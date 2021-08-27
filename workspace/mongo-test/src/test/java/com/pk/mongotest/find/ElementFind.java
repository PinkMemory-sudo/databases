package com.pk.mongotest.find;

import com.pk.mongotest.entity.AccountStatus;
import com.pk.mongotest.entity.UserExInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

@SpringBootTest
public class ElementFind {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Test
    public void test(){
        Criteria criteria = Criteria.where("status").is(AccountStatus.ENABLED)
                .and("intelEditions.endTime").gt(16200L)
                .and("intelEditions.pushDate").is("asd");
        List<UserExInfo> userExInfos = mongoTemplate.find(Query.query(criteria), UserExInfo.class);
        System.out.println(userExInfos);
    }
}
