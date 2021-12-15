package com.pk.esold.search;

import org.elasticsearch.client.transport.TransportClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ScrollTest {

    @Autowired
    private TransportClient client;

    @Test
    public void test(){

    }
}
