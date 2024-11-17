package com.chat.netty;

import com.chat.netty.handler.HttpServerInitializer;
import com.chat.netty.mq.RabbitMQConnectUtils;
import com.chat.netty.util.JedisPoolUtils;
import com.chat.netty.util.ZookeeperRegister;
import com.chat.netty.websocket.WSServerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Netty服务的启动类
 */
public class ChatApplication {

    // netty默认端口
    public static final Integer nettyDefaultPort = 875;
    // 初始化连接数
    public static final String initOnlineCounts = "0";

    /**
     * 动态分配Netty集群接口
     * @param port
     * @return
     */
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

    public static void main(String[] args) throws Exception {
        // 定义主线程组，用于接收客户端的连接，但是不做任何处理
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        // 定义从线程池，处理主线程池交过来的任务
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        // Netty服务启动的时候，从Redis中查找有没有端口，如果没有则使用875，如果有则端口累加10再启动
        Integer nettyPort = selectPort(nettyDefaultPort);
        // 向Zookeeper注册Netty服务节点
        ZookeeperRegister.registerNettyServer("server-list", ZookeeperRegister.getLocalIP(), nettyPort);
        // 启动消费者进行监听，队列可以根据动态生成的端口进行动态拼接
        String queueName = "netty_queue_" + nettyPort;
        RabbitMQConnectUtils mqConnectUtils = new RabbitMQConnectUtils();
        mqConnectUtils.listen("fanout_exchange", queueName);
        try {
            // 构建Netty服务器
            ServerBootstrap server = new ServerBootstrap();
            // 把主从线程池组放入到启动类中
            // channel()设置Nio双向通道
            // childHandler()设置处理器，主要用于处理workerGroup
            server.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).childHandler(new WSServerInitializer());
            // 启动server，并且绑定端口号为875，同时启动方式为同步
            ChannelFuture channelFuture = server.bind(nettyPort).sync();
            // 监听关闭的channel
            channelFuture.channel().closeFuture().sync();
        }finally {
            // 关闭线程池组
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}