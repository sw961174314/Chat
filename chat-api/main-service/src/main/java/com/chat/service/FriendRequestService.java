package com.chat.service;

import com.chat.pojo.bo.NewFriendRequestBO;
import com.chat.utils.PagedGridResult;

/**
 * 好友请求表 服务类
 */
public interface FriendRequestService {

    /**
     * 新增添加好友请求
     * @param friendRequestBO
     */
    public void addNewRequest(NewFriendRequestBO friendRequestBO);

    /**
     * 查询新朋友的请求记录列表
     * @param userId
     * @param page
     * @param pageSize
     * @return
     */
    public PagedGridResult queryNewFriendList(String userId, Integer page, Integer pageSize);

    /**
     * 通过好友请求
     * @param friendRequestId
     * @param friendRemark
     */
    public void passNewFriend(String friendRequestId, String friendRemark);
}