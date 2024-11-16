package com.chat.netty.util;

import com.chat.pojo.netty.NettyServerNode;
import com.chat.utils.JsonUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Zookeeper 服务节点配置类
 */
public class ZookeeperRegister {

    /**
     *
     * @param nodeName
     * @param ip
     * @param port
     * @throws Exception
     */
    public static void registerNettyServer(String nodeName, String ip, Integer port) throws Exception {
        CuratorFramework zkClient = CuratorConfig.getClient();
        String path = "/" + nodeName;
        Stat stat = zkClient.checkExists().forPath(path);
        if (stat == null) {
            zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path);
        } else {
            System.out.println(stat);
        }
        // 创建对应的临时节点，值可以放在线人数，默认为初始化为0
        NettyServerNode serverNode = new NettyServerNode();
        serverNode.setIp(ip);
        serverNode.setPort(port);
        String nodeJson = JsonUtils.objectToJson(serverNode);
        zkClient.create().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(path + "/im-", nodeJson.getBytes());
    }

    /**
     * 获取本地IP
     * @return
     * @throws UnknownHostException
     */
    public static String getLocalIP() throws UnknownHostException {
        InetAddress address = InetAddress.getLocalHost();
        String ip = address.getHostAddress();
        return ip;
    }
}