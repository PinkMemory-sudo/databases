package com.pk.demo.insert;

import com.pk.demo.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import javax.jws.soap.SOAPBinding;

/**
 * 不使用默认的Id，并且id的字段名也不叫id
 */
@SpringBootTest
public class IdTest {

    @Autowired
    private MongoOperations mongo;

    @Test
    public void test(){
        User user = new User();
        user.setName("Tom");
        user.setAge(18);
        mongo.insert(user);
    }

    @Test
    public void find(){
        // 那么根据_id查找韩式name
        Query query = Query.query(Criteria.where("_id").is("Tom"));
        System.out.println(mongo.find(query, User.class));

        // 都可以
    }

}
