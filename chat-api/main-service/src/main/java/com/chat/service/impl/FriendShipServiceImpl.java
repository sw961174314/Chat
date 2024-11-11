package com.chat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chat.base.BaseInfoProperties;
import com.chat.mapper.FriendshipMapper;
import com.chat.pojo.FriendRequest;
import com.chat.pojo.Friendship;
import com.chat.service.FriendshipService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * 好友关系表 服务实现类
 */
@Service
public class FriendShipServiceImpl extends BaseInfoProperties implements FriendshipService {

    @Resource
    private FriendshipMapper friendshipMapper;

    @Override
    public Friendship getFriendship(String myId, String friendId) {
        QueryWrapper queryWrapper = new QueryWrapper<FriendRequest>().eq("my_id", myId).eq("friend_id", friendId);
        return friendshipMapper.selectOne(queryWrapper);
    }
}