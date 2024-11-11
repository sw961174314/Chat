package com.chat.service;

import com.chat.pojo.bo.NewFriendRequestBO;

/**
 * 好友请求表 服务类
 */
public interface FriendRequestService {

    /**
     * 新增添加好友请求
     * @param friendRequestBO
     */
    public void addNewRequest(NewFriendRequestBO friendRequestBO);
}