package com.chat.service;

import com.chat.pojo.Friendship;

/**
 * 好友关系表 服务类
 */
public interface FriendshipService {

    /**
     * 获取好友关系数据
     * @param myId
     * @param friendId
     * @return
     */
    public Friendship getFriendship(String myId,String friendId);
}