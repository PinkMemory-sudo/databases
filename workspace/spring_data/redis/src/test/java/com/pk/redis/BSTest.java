package com.pk.redis;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootTest
public class BSTest {

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 十分钟内只能提交3次
     */
//    @Test
//    public void submit() {
//        String userId = "1";
//        String key = userId + "_submit";
//        if (redisTemplate.hasKey(key)){
//
//        }else {
//            // todo 提交操作
//            redisTemplate.opsForValue().
//        }
//
//    }
}
