package com.pk.demo;

import com.pk.demo.entity.Company;
import com.pk.demo.entity.Home;
import com.pk.demo.entity.User;
import org.junit.jupiter.api.Test;
import org.omg.PortableInterceptor.HOLDING;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.UUID;

@SpringBootTest
public class DBRefTest {

    @Autowired
    private MongoOperations mongo;

    /**
     * 添加
     */
    @Test
    public void add() {
        Company company = new Company();
        company.setId(UUID.randomUUID().toString());
        company.setName("g00gle");

        Home home = new Home();
        home.setId(UUID.randomUUID().toString());
        home.setAddress("NewYork");

        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setName("Jack");
        user.setCompany(company);
        user.setHome(home);
        mongo.save(user);
        mongo.save(company);
        mongo.save(home);
        System.out.println(mongo.findAll(User.class));
        System.out.println(mongo.findAll(Company.class));
        System.out.println(mongo.findAll(Home.class));
    }


    /**
     * 修改
     */

    /**
     * 删除
     */
    @Test
    public void delete() {
        Query query = Query.query(Criteria.where("name").is("Jack"));
        mongo.remove(query, User.class);
        System.out.println(mongo.findAll(User.class));
        System.out.println(mongo.findAll(Company.class));
        System.out.println(mongo.findAll(Home.class));
    }

    /**
     * 查询
     */

    @Test
    public void find() {
        Query query = Query.query(Criteria.where("home.address").is("NewYork"));
        System.out.println(mongo.find(query, User.class));
    }

    @Test
    public void test() {
        mongo.dropCollection(User.class);
        mongo.dropCollection(Company.class);
        mongo.dropCollection(Home.class);
    }

}
