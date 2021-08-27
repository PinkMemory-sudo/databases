package com.pk.demo;

import com.pk.demo.entity.Account;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class AddTest {

    @Autowired
    private MongoTemplate mongo;

    @Test
    public void addOne() {
        Account account = new Account();
        account.setAccountId("WX");
        account.setRepeatDuration(60);
        account.setTransId(2);
        mongo.insert(account);
    }

    @Test
    public void bulkAdd() {
        List<Account> accountList = new ArrayList<>();
        Account account = new Account();
        account.setAccountId("TS");
        account.setRepeatDuration(60);
        account.setTransId(2);
        accountList.add(account);

        Account a1 = new Account();
        a1.setAccountId("LW");
        a1.setRepeatDuration(60);
        a1.setTransId(2);
        accountList.add(a1);
        mongo.insert(accountList, Account.class);
    }
}
