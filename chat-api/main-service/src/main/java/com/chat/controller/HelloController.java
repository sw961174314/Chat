package com.chat.controller;

import com.chat.pojo.netty.ChatMsg;
import com.chat.rabbitmq.RabbitMQTestConfig;
import com.chat.utils.JsonUtils;
import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("m")
public class HelloController {

    @Resource
    private RabbitTemplate rabbitTemplate;

    @GetMapping("hello")
    public Object Helllo() {
        return "Hello world~";
    }

    @GetMapping("mq")
    public Object mq() {
        ChatMsg chatMsg = new ChatMsg();
        chatMsg.setMsg("hello");
        String msg = JsonUtils.objectToJson(chatMsg);
        rabbitTemplate.convertAndSend(RabbitMQTestConfig.TEST_EXCHANGE,RabbitMQTestConfig.ROUTING_KEY_TEST,msg);
        return "ok";
    }
}