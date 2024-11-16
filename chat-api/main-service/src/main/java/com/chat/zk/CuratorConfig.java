package com.chat.zk;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Value;
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
        return client;
    }
}