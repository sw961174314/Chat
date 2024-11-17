package com.chat.zk;

import com.chat.pojo.netty.NettyServerNode;
import com.chat.utils.JsonUtils;
import com.chat.utils.RedisOperator;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * Curator配置类
 */
@Data
@Slf4j
@Component
@ConfigurationProperties(prefix = "zookeeper.curator")
public class CuratorConfig {

    // 单机/集群ip:port
    private String host;
    // 连接超时时间
    private Integer connectionTimeoutMs;
    // 会话超时时间
    private Integer sessionTimeoutMs;
    // 每次重试的间隔时间
    private Integer sleepMsBetweenRetry;
    // 最大重试次数
    private Integer maxRetries;
    // 命名空间(root根节点名称)
    private String namespace;

    public static final String PATH = "/server-list";

    @Resource
    private RedisOperator redis;

    @Resource
    private RabbitAdmin rabbitAdmin;

    @Bean("curatorClient")
    public CuratorFramework curatorClient() {
        // 定义重试策略
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(sleepMsBetweenRetry, maxRetries);
        // 声明初始化客户端
        CuratorFramework client = CuratorFrameworkFactory.builder().connectString(host).connectionTimeoutMs(connectionTimeoutMs).sessionTimeoutMs(sessionTimeoutMs).retryPolicy(retryPolicy).namespace(namespace).build();
        // 启动客户端
        client.start();
        // 第一次创建完毕之后需要注释，否则会报错（节点已存在）
        /** try {
            client.create().creatingParentsIfNeeded().forPath("/springboot/test", "springdloud".getBytes());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }*/
        // 注册监听watcher的事件
        addWatch(PATH, client);
        return client;
    }

    /**
     * 注册节点的事件监听
     * @param path
     * @param client
     */
    public void addWatch(String path, CuratorFramework client) {
        CuratorCache curatorCache = CuratorCache.build(client, path);
        curatorCache.listenable().addListener((type, oldData, data) -> {
            // type：当前监听到的事件类型
            // oldData：节点更新前的数据、状态
            // data：节点更新后的数据、状态
            switch (type.name()) {
                case "NODE_CREATED":
                    log.info("(子)节点创建");
                    break;
                case "NODE_CHANGED":
                    log.info("(子)节点变更");
                    break;
                case "NODE_DELETED":
                    log.info("(子)节点删除");
                    NettyServerNode oldNode = JsonUtils.jsonToPojo(new String(oldData.getData()), NettyServerNode.class);
                    // 移除残留端口
                    String oldPort = oldNode.getPort() + "";
                    String portKey = "netty_port";
                    redis.hdel(portKey, oldPort);
                    // 移除残留消息队列
                    String queueName = "netty_queue_" + oldPort;
                    rabbitAdmin.deleteQueue(queueName);
                    break;
            }
        });
        // 开启监听
        curatorCache.start();
    }
}