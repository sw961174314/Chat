package com.chat.controller;

import com.chat.base.BaseInfoProperties;
import com.chat.grace.result.GraceJSONResult;
import com.chat.pojo.netty.ChatMsg;
import com.chat.rabbitmq.RabbitMQTestConfig;
import com.chat.utils.JsonUtils;
import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("chat")
public class ChatController extends BaseInfoProperties {

    /**
     * 查询未读消息数量
     * @param myId
     * @return
     */
    @PostMapping("getMyUnReadCounts")
    public GraceJSONResult getMyUnReadCounts(String myId) {
        Map map = redis.hgetall(CHAT_MSG_LIST + ":" + myId);
        return GraceJSONResult.ok(map);
    }

    /**
     * 清空未读消息数量
     * @param myId
     * @return
     */
    @PostMapping("clearMyUnReadCounts")
    public GraceJSONResult clearMyUnReadCounts(String myId,String oppositeId) {
        redis.setHashValue(CHAT_MSG_LIST + ":" + myId, oppositeId, "0");
        return GraceJSONResult.ok();
    }
}