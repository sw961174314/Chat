package com.chat.rabbitmq;

import com.chat.pojo.netty.ChatMsg;
import com.chat.service.ChatMessageService;
import com.chat.utils.JsonUtils;
import jakarta.annotation.Resource;
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

    @Resource
    private ChatMessageService chatMessageService;

    @RabbitListener(queues = {RabbitMQTestConfig.TEST_QUEUE})
    public void watchQueue(String payload, Message message) {
        String routingKey = message.getMessageProperties().getReceivedRoutingKey();
        log.info("routingKey = " + routingKey);

        if (routingKey.equals(RabbitMQTestConfig.ROUTING_KEY_CHAT_MSG_SEND)) {
            String msg = payload;
            ChatMsg chatMsg = JsonUtils.jsonToPojo(msg, ChatMsg.class);
            chatMessageService.saveMsg(chatMsg);
        }
    }
}