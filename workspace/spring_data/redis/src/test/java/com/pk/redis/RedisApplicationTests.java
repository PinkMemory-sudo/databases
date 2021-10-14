package com.pk.redis;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@SpringBootTest
class RedisApplicationTests {

    @Test
    void contextLoads() {
//        System.out.println(getFbnc(6));
        String pushDateEnd = LocalDate.parse("2021-01-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                .plusDays(1L).toString();
        System.out.println(pushDateEnd);
    }


    /**
     * 获得倍数
     */
    private int getFbnc(int times) {
        int one = 0;
        int two = 1;
        int sum = 1;
        if (times < 1) {
            sum = 0;
        } else if (times > 1) {
            for (int i = 1; i < times; i++) {
                sum = one + two;
                one = two;
                two = sum;
            }
        }
        return sum;
    }

    /**
     *
     */

}
