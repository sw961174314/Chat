package com.chat.service.impl;

import com.chat.base.BaseInfoProperties;
import com.chat.mapper.ChatMessageMapper;
import com.chat.pojo.ChatMessage;
import com.chat.pojo.netty.ChatMsg;
import com.chat.service.ChatMessageService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 聊天信息存储表 服务实现类
 */
@Service
public class ChatMessageServiceImpl extends BaseInfoProperties implements ChatMessageService {

    @Resource
    private ChatMessageMapper chatMessageMapper;

    @Override
    @Transactional
    public void saveMsg(ChatMsg chatMsg) {
        ChatMessage message = new ChatMessage();
        BeanUtils.copyProperties(chatMsg, message);
        // 手动设置聊天信息的主键id
        message.setId(chatMsg.getMsgId());
        chatMessageMapper.insert(message);

        // 发送者id
        String senderId = chatMsg.getSenderId();
        // 接收者id
        String receiverId = chatMsg.getReceiverId();
        // 通过Redis累加信息接收者的对应记录
        redis.incrementHash(CHAT_MSG_LIST + ":" + receiverId, senderId, 1);
    }
}