package com.pk.redis;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootTest
public class StringTest {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    public void getString() {
        System.out.println(redisTemplate.hasKey("tom"));
        System.out.println(redisTemplate.opsForValue().get("tom"));
        System.out.println(redisTemplate.keys("*"));
    }

}
