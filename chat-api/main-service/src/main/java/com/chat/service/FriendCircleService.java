package com.chat.service;

import com.chat.pojo.bo.FriendCircleBO;
import com.chat.utils.PagedGridResult;

/**
 * 朋友圈表 服务类
 */
public interface FriendCircleService {

    /**
     * 发布朋友圈图文数据，保存到数据库
     * @param friendCircleBO
     */
    public void publish(FriendCircleBO friendCircleBO);

    /**
     * 朋友圈图文查询
     * @param userId
     * @param page
     * @param pageSize
     * @return
     */
    public PagedGridResult queryList(String userId, Integer page, Integer pageSize);
}