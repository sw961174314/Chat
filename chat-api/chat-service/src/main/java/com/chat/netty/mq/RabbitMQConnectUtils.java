package com.chat.netty.mq;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * RabbitMQ连接工具类
 */
public class RabbitMQConnectUtils {

    private final List<Connection> connections = new ArrayList<>();
    private final int maxConnection = 20;

    // 开发环境 dev
    private final String host = "127.0.0.1";
    private final int port = 5682;
    private final String username = "root";
    private final String password = "123456";
    private final String virtualHost = "chat";

    public ConnectionFactory factory;

    public ConnectionFactory getRabbitMqConnection() {
        return getFactory();
    }

    public ConnectionFactory getFactory() {
        initFactory();
        return factory;
    }

    private void initFactory() {
        try {
            if (factory == null) {
                factory = new ConnectionFactory();
                factory.setHost(host);
                factory.setPort(port);
                factory.setUsername(username);
                factory.setPassword(password);
                factory.setVirtualHost(virtualHost);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMsg(String message, String queue) throws Exception {
        Connection connection = getConnection();
        Channel channel = connection.createChannel();
        channel.basicPublish("",
                            queue,
                            MessageProperties.PERSISTENT_TEXT_PLAIN,
                            message.getBytes("utf-8"));
        channel.close();
        setConnection(connection);
    }

    public void sendMsg(String message, String exchange, String routingKey) throws Exception {
        Connection connection = getConnection();
        Channel channel = connection.createChannel();
        channel.basicPublish(exchange,
                            routingKey,
                            MessageProperties.PERSISTENT_TEXT_PLAIN,
                            message.getBytes("utf-8"));
        channel.close();
        setConnection(connection);
    }

    public GetResponse basicGet(String queue, boolean autoAck) throws Exception {
        GetResponse getResponse = null;
        Connection connection = getConnection();
        Channel channel = connection.createChannel();
        getResponse = channel.basicGet(queue, autoAck);
        channel.close();
        setConnection(connection);
        return getResponse;
    }

    public Connection getConnection() throws Exception {
        return getAndSetConnection(true, null);
    }

    public void setConnection(Connection connection) throws Exception {
        getAndSetConnection(false, connection);
    }

    /**
     * 开启消息队列监听
     * @param exchangeName
     * @param queueName
     * @throws Exception
     */
    public void listen(String exchangeName, String queueName) throws Exception {
        Connection connection = getConnection();
        Channel channel = connection.createChannel();

        // 定义交换机 发布订阅模式(广播模式)
        channel.exchangeDeclare(exchangeName,
                BuiltinExchangeType.FANOUT,
                true, false, false, null);
        // 定义队列
        channel.queueDeclare(queueName, true, false, false, null);
        // 绑定交换机与队列
        channel.queueBind(queueName, exchangeName, "");
        // 消费者
        Consumer consumer = new DefaultConsumer(channel){
            /**
             * 重写消息配送方法
             * @param consumerTag 消息的标签（标识）
             * @param envelope  信封（一些信息，比如交换机路由等等信息）
             * @param properties 配置信息
             * @param body 收到的消息数据
             * @throws IOException
             */
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                // 交换机
                String exchange = envelope.getExchange();
                if (exchange.equalsIgnoreCase(exchangeName)) {
                    String msg = new String(body);
                    System.out.println(msg);
                }
            }
        };
        /**
         * 开启监听
         * queue: 监听的队列名
         * autoAck: 是否自动确认，true：告知mq消费者已经消费的确认通知
         * callback: 回调函数，处理监听到的消息
         */
        channel.basicConsume(queueName, true, consumer);
    }

    private synchronized Connection getAndSetConnection(boolean isGet, Connection connection) throws Exception {
        getRabbitMqConnection();
        if (isGet) {
            if (connections.isEmpty()) {
                return factory.newConnection();
            }
            Connection newConnection = connections.get(0);
            connections.remove(0);
            if (newConnection.isOpen()) {
                return newConnection;
            } else {
                return factory.newConnection();
            }
        } else {
            if (connections.size() < maxConnection) {
                connections.add(connection);
            }
            return null;
        }
    }
}