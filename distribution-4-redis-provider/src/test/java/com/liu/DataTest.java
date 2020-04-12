package com.liu;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DataTest {

    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void testSaveValueToRedisByRedisTemplate(){
        //获取redis操作器
        ValueOperations<Object,Object> operation = redisTemplate.opsForValue();

        //设置值
        //operation.set("keyone","valueone");

        //获取数据
        Object value = operation.get("keyone");
        System.out.println(value);
    }

    @Test
    public void testStringRedisTemplate(){

        //获取redis的操作器
        ValueOperations<String,String> operation = stringRedisTemplate.opsForValue();
        //设置值
        operation.set("keytwo","valuetwo");

    }
}
