package com.chat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chat.base.BaseInfoProperties;
import com.chat.mapper.ChatMessageMapper;
import com.chat.pojo.ChatMessage;
import com.chat.pojo.netty.ChatMsg;
import com.chat.service.ChatMessageService;
import com.chat.utils.PagedGridResult;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    public PagedGridResult queryChatMsgList(String senderId, String receiverId, Integer page, Integer pageSize) {
        Page<ChatMessage> pageInfo = new Page<>(page, pageSize);
        QueryWrapper queryWrapper = new QueryWrapper<ChatMessage>().or(qw -> qw.eq("sender_id", senderId).eq("receiver_id", receiverId)).or(qw -> qw.eq("sender_id", receiverId).eq("receiver_id", senderId)).orderByDesc("chat_time");
        chatMessageMapper.selectPage(pageInfo, queryWrapper);
        // 获得列表后，倒排，因为聊天记录是展示最新的数据在聊天框的最下方
        List<ChatMessage> list = pageInfo.getRecords();
        List<ChatMessage> msgList = list.stream().sorted(Comparator.comparing(ChatMessage::getChatTime)).collect(Collectors.toList());
        pageInfo.setRecords(msgList);
        return setterPagedGridPlus(pageInfo);
    }

    @Override
    @Transactional
    public void updateMsgSignRead(String msgId) {
        ChatMessage message = new ChatMessage();
        message.setId(msgId);
        message.setIsRead(true);
        chatMessageMapper.updateById(message);
    }
}