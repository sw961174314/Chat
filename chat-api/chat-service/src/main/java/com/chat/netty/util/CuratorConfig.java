package com.chat.netty.util;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * Curator配置类
 */
public class CuratorConfig {

    // 单机/集群ip:port
    private static String host = "127.0.0.1:3191";
    // 连接超时时间
    private static Integer connectionTimeoutMs = 30 * 1000;
    // 会话超时时间
    private static Integer sessionTimeoutMs = 3 * 1000;
    // 每次重试的间隔时间
    private static Integer sleepMsBetweenRetry = 2 * 1000;
    // 最大重试次数
    private static Integer maxRetries = 3;
    // 命名空间(root根节点名称)
    private static String namespace = "chat-im";

    // Curator客户端
    private static CuratorFramework client;

    static {
        // 定义重试策略
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(sleepMsBetweenRetry, maxRetries);
        // 声明初始化客户端
        client = CuratorFrameworkFactory.builder().connectString(host).connectionTimeoutMs(connectionTimeoutMs).sessionTimeoutMs(sessionTimeoutMs).retryPolicy(retryPolicy).namespace(namespace).build();
        // 启动客户端
        client.start();
    }

    public static CuratorFramework getClient() {
        return client;
    }
}