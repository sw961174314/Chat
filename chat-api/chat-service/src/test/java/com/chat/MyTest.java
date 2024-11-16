package com.chat;

import com.chat.netty.util.CuratorConfig;
import com.chat.netty.util.JedisPoolUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.data.Stat;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class MyTest {

    @Test
    public void testJedisPool() {
        String key = "testJedis";
        Jedis jedis = JedisPoolUtils.getJedis();
        jedis.set(key, "helloJedis");
        String value = jedis.get(key);
        System.out.println(value);
    }

    @Test
    public void testGetJedisNettyPort() {
        Integer nettyPort = selectPort(nettyDefaultPort);
        System.out.println(nettyPort);
    }

    public static final Integer nettyDefaultPort = 875;
    public static final String initOnlineCounts = "0";

    public static Integer selectPort(Integer port) {
        String portKey = "netty_port";
        Jedis jedis = JedisPoolUtils.getJedis();
        Map<String, String> portMap = jedis.hgetAll(portKey);
        // 由于map中的key都应该是整数类型的port，所以先转换成整数后，再进行对比，否则String类型的端口比对会有问题
        List<Integer> portList = portMap.entrySet().stream().map(entry -> Integer.valueOf(entry.getKey())).collect(Collectors.toList());
        Integer nettyPort = null;
        if (portList == null || portList.isEmpty()) {
            jedis.hset(portKey, port + "", initOnlineCounts);
            nettyPort = port;
        } else {
            // 使用stream循环获得最大值，并且累加n，获得新的端口
            Optional<Integer> maxInteger = portList.stream().max(Integer::compareTo);
            Integer maxPort = maxInteger.get().intValue();
            Integer currentPort = maxPort + 10;
            jedis.hset(portKey, currentPort + "", initOnlineCounts);
            nettyPort = currentPort;
        }
        return nettyPort;
    }

    @Test
    public void testGetCurator() throws Exception {
        CuratorFramework zkClient = CuratorConfig.getClient();
        Stat stat = zkClient.checkExists().forPath("/hello");
        String data = new String(zkClient.getData().forPath("/hello"));
        System.out.println(data);
    }
}