package com.chat.service;

import com.chat.pojo.bo.FriendCircleBO;

/**
 * 朋友圈表 服务类
 */
public interface FriendCircleService {

    /**
     * 发布朋友圈图文数据，保存到数据库
     * @param friendCircleBO
     */
    public void publish(FriendCircleBO friendCircleBO);
}