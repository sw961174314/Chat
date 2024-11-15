package com.chat.rabbitmq;

import com.chat.pojo.netty.ChatMsg;
import com.chat.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * RabbitMQ消费者
 */
@Slf4j
@Component
public class RabbitMQConsumer {

    @RabbitListener(queues = {RabbitMQTestConfig.TEST_QUEUE})
    public void watchQueue(String payload, Message message) {
        String routingKey = message.getMessageProperties().getReceivedRoutingKey();
        log.info("routingKey = " + routingKey);

        if (routingKey.equals(RabbitMQTestConfig.ROUTING_KEY_TEST)) {
            String msg = payload;
            ChatMsg chatMsg = JsonUtils.jsonToPojo(msg, ChatMsg.class);
            log.info("chatMsg = " + chatMsg.toString());
        }
    }
}