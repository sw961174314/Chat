package com.chat.service;

import com.chat.pojo.netty.ChatMsg;

/**
 * 聊天信息存储表 服务类
 */
public interface ChatMessageService {

    /**
     * 保存聊天消息
     * @param chatMsg
     */
    public void saveMsg(ChatMsg chatMsg);
}