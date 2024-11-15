package com.chat.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ配置类
 */
@Configuration
public class RabbitMQTestConfig {

    // 交换机
    public static final String TEST_EXCHANGE = "test_exchange";

    // 队列
    public static final String TEST_QUEUE = "test_queue";

    // 具体的路由地址
    public static final String ROUTING_KEY_TEST = "chat.test";

    // 创建交换机
    @Bean(TEST_EXCHANGE)
    public Exchange exchange() {
        return ExchangeBuilder.topicExchange(TEST_EXCHANGE).durable(true).build();
    }

    // 创建队列
    @Bean(TEST_QUEUE)
    public Queue queue() {
        return QueueBuilder.durable(TEST_QUEUE).build();
    }

    // 交换机和队列进行绑定
    @Bean
    public Binding binding(@Qualifier(TEST_EXCHANGE) Exchange exchange, @Qualifier(TEST_QUEUE) Queue queue) {
        // 执行绑定
        return BindingBuilder.bind(queue).to(exchange).with("chat.#").noargs();
    }
}