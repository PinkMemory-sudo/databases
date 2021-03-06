package com.pk.redis;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SpringBootTest
public class SerializationTest {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void test() {
        Instant begin = Instant.now();
        List<Integer> list = IntStream.range(1, 10000).boxed().collect(Collectors.toList());
        RedisSerializer<String> keySerializer = new StringRedisSerializer();
        RedisSerializer valueSerializer = redisTemplate.getValueSerializer();
        redisTemplate.execute((RedisConnection redisConnection) -> {
            for (Integer integer : list) {
                byte[] key = keySerializer.serialize(integer.toString());
                byte[] value = valueSerializer.serialize(integer);
                redisConnection.set(key, value);
            }
            return null;
        });
        Instant end = Instant.now();
        System.out.println(Duration.between(begin, end).getSeconds());
    }

    @Test
    public void test1() {
        Instant begin = Instant.now();
        List<Integer> list = IntStream.range(1, 10000).boxed().collect(Collectors.toList());
//        RedisSerializer<String> keySerializer = new StringRedisSerializer();
//        RedisSerializer valueSerializer = redisTemplate.getValueSerializer();
        for (Integer integer : list) {
            byte[] bytes = new byte[1];
            bytes[0] = integer.byteValue();
            redisTemplate.opsForValue().set(integer, integer);
        }
        Instant end = Instant.now();
        System.out.println(Duration.between(begin, end).getSeconds());
    }

    @Test
    public void test2() {

    }

    public static void writeCodeInfoToRedisPipe(RedisTemplate redisTemplate,
                                                Map<String, ?> keyValueMap) {
        // redis k????????????v????????????
        RedisSerializer<String> keySerializer = new StringRedisSerializer();
        RedisSerializer valueSerializer = redisTemplate.getValueSerializer();

        // ??????????????????????????????redis??????????????????ttl.
        redisTemplate.execute((RedisConnection redisConnection) -> {
            keyValueMap.forEach((key, value) -> {
                // ???key?????????
                byte[] rawKey = keySerializer.serialize(key);
                // ???value??????????????????redis, ????????????ttl.
                redisConnection.set(rawKey, valueSerializer.serialize(value));
            });
            return null;
        });
    }
}
