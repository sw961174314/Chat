package com.chat;

import com.chat.netty.util.JedisPoolUtils;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;

public class MyTest {

    @Test
    public void testJedisPool() {
        String key = "testJedis";
        Jedis jedis = JedisPoolUtils.getJedis();
        jedis.set(key, "helloJedis");
        String value = jedis.get(key);
        System.out.println(value);
    }
}