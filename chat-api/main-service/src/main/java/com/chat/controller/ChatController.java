package com.chat.controller;

import com.chat.base.BaseInfoProperties;
import com.chat.grace.result.GraceJSONResult;
import com.chat.pojo.netty.ChatMsg;
import com.chat.rabbitmq.RabbitMQTestConfig;
import com.chat.service.ChatMessageService;
import com.chat.utils.JsonUtils;
import com.chat.utils.PagedGridResult;
import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("chat")
public class ChatController extends BaseInfoProperties {

    @Resource
    private ChatMessageService chatMessageService;

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

    /**
     * 分页查询聊天记录
     * @param senderId
     * @param receiverId
     * @return
     */
    @PostMapping("list/{senderId}/{receiverId}")
    public GraceJSONResult list(@PathVariable("senderId") String senderId, @PathVariable("receiverId") String receiverId, Integer page, Integer pageSize) {
        if (page == null) {
            page = 1;
        }
        if (pageSize == null) {
            pageSize = 20;
        }
        PagedGridResult result = chatMessageService.queryChatMsgList(senderId, receiverId, page, pageSize);
        return GraceJSONResult.ok(result);
    }

    /**
     * 标记语音聊天信息的签收已读
     * @param msgId
     * @return
     */
    @PostMapping("signRead/{msgId}")
    public GraceJSONResult signRead(@PathVariable("msgId") String msgId) {
        chatMessageService.updateMsgSignRead(msgId);
        return GraceJSONResult.ok();
    }
}