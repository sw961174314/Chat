package com.chat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chat.base.BaseInfoProperties;
import com.chat.mapper.FriendCircleLikedMapper;
import com.chat.mapper.FriendCircleMapper;
import com.chat.mapper.FriendCircleMapperCustom;
import com.chat.pojo.FriendCircle;
import com.chat.pojo.FriendCircleLiked;
import com.chat.pojo.Users;
import com.chat.pojo.bo.FriendCircleBO;
import com.chat.pojo.vo.FriendCircleVO;
import com.chat.service.FriendCircleService;
import com.chat.service.UsersService;
import com.chat.utils.PagedGridResult;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 朋友圈表 服务实现类
 */
@Service
public class FriendCircleServiceImpl extends BaseInfoProperties implements FriendCircleService {

    @Resource
    private UsersService usersService;

    @Resource
    private FriendCircleMapper friendCircleMapper;

    @Resource
    private FriendCircleMapperCustom friendCircleMapperCustom;

    @Resource
    private FriendCircleLikedMapper friendCircleLikedMapper;

    @Override
    @Transactional
    public void publish(FriendCircleBO friendCircleBO) {
        FriendCircle pendingFriendCircle = new FriendCircle();
        BeanUtils.copyProperties(friendCircleBO, pendingFriendCircle);
        friendCircleMapper.insert(pendingFriendCircle);
    }

    @Override
    public PagedGridResult queryList(String userId, Integer page, Integer pageSize) {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        // 设置分页参数
        Page<FriendCircleVO> pageInfo = new Page<>(page, pageSize);
        friendCircleMapperCustom.queryFriendCircleList(pageInfo,map);
        return setterPagedGridPlus(pageInfo);
    }

    @Override
    @Transactional
    public void like(String friendCircleId, String userId) {
        // 根据朋友圈主键id查询发布人id
        FriendCircle friendCircle = selectFriendCircle(friendCircleId);
        // 根据用户id查询点赞人信息
        Users users = usersService.getById(userId);
        // 朋友圈点赞数据
        FriendCircleLiked circleLiked = new FriendCircleLiked();
        circleLiked.setFriendCircleId(friendCircleId);
        circleLiked.setBelongUserId(friendCircle.getUserId());
        circleLiked.setLikedUserId(userId);
        circleLiked.setLikedUserName(users.getNickname());
        circleLiked.setCreatedTime(LocalDateTime.now());
        friendCircleLikedMapper.insert(circleLiked);

        // 点赞过后，朋友圈的对应点赞数加1
        redis.increment(REDIS_FRIEND_CIRCLE_LIKED_COUNTS + ":" + friendCircleId, 1);
        // 标记哪个用户点赞过该朋友圈
        redis.setnx(REDIS_DOES_USER_LIKE_FRIEND_CIRCLE + ":" + friendCircleId + ":" + userId, userId);
    }

    @Override
    @Transactional
    public void unlike(String friendCircleId, String userId) {
        // 从数据库中删除对应的点赞消息
        QueryWrapper deleteWrapper = new QueryWrapper<FriendCircleLiked>().eq("friend_circle_id", friendCircleId).eq("user_id", userId);
        friendCircleLikedMapper.delete(deleteWrapper);
        // 取消点赞过后，朋友圈的对应点赞数减1
        redis.decrement(REDIS_FRIEND_CIRCLE_LIKED_COUNTS + ":" + friendCircleId, 1);
        // 标记哪个用户点赞过该朋友圈
        redis.del(REDIS_DOES_USER_LIKE_FRIEND_CIRCLE + ":" + friendCircleId + ":" + userId);
    }

    /**
     * 获取朋友圈数据
     * @param friendCircleId
     * @return
     */
    private FriendCircle selectFriendCircle(String friendCircleId) {
        return friendCircleMapper.selectById(friendCircleId);
    }
}