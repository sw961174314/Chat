package com.chat.service;

import com.chat.pojo.netty.ChatMsg;
import com.chat.utils.PagedGridResult;

/**
 * 聊天信息存储表 服务类
 */
public interface ChatMessageService {

    /**
     * 保存聊天消息
     * @param chatMsg
     */
    public void saveMsg(ChatMsg chatMsg);

    /**
     * 查询聊天信息列表
     * @param senderId
     * @param receiverId
     * @param page
     * @param pageSize
     * @return
     */
    public PagedGridResult queryChatMsgList(String senderId, String receiverId, Integer page, Integer pageSize);

    /**
     * 标记语音聊天信息的签收已读
     * @param msgId
     */
    public void updateMsgSignRead(String msgId);
}