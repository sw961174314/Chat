package com.chat.service;

import com.chat.pojo.FriendCircleLiked;
import com.chat.pojo.bo.FriendCircleBO;
import com.chat.utils.PagedGridResult;

import java.util.List;

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

    /**
     * 朋友圈点赞
     * @param friendCircleId
     * @param userId
     */
    public void like(String friendCircleId, String userId);

    /**
     * 朋友圈取消点赞
     * @param friendCircleId
     * @param userId
     */
    public void unlike(String friendCircleId, String userId);

    /**
     * 查询朋友圈点赞列表
     * @param friendCircleId
     * @return
     */
    public List<FriendCircleLiked> queryLikedFriends(String friendCircleId);

    /**
     * 判断用户是否点赞过朋友圈
     * @param friendCircleId
     * @param userId
     * @return
     */
    public boolean doILike(String friendCircleId, String userId);

    /**
     * 删除朋友圈数据(图文+评论+点赞)
     * @param friendCircleId
     * @param userId
     */
    public void delete(String friendCircleId, String userId) throws Exception;
}