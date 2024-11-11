package com.chat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chat.base.BaseInfoProperties;
import com.chat.enums.YesOrNo;
import com.chat.mapper.FriendshipMapper;
import com.chat.mapper.FriendshipMapperCustom;
import com.chat.pojo.FriendRequest;
import com.chat.pojo.Friendship;
import com.chat.pojo.vo.ContactsVO;
import com.chat.service.FriendshipService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 好友关系表 服务实现类
 */
@Service
public class FriendShipServiceImpl extends BaseInfoProperties implements FriendshipService {

    @Resource
    private FriendshipMapper friendshipMapper;

    @Resource
    private FriendshipMapperCustom friendshipMapperCustom;

    @Override
    public Friendship getFriendship(String myId, String friendId) {
        QueryWrapper queryWrapper = new QueryWrapper<FriendRequest>().eq("my_id", myId).eq("friend_id", friendId);
        return friendshipMapper.selectOne(queryWrapper);
    }

    @Override
    public List<ContactsVO> queryMyFriends(String myId) {
        Map<String, Object> map = new HashMap<>();
        map.put("myId", myId);
        return friendshipMapperCustom.queryMyFriends(map);
    }

    @Override
    public void updateFriendRemark(String myId, String friendId, String friendRemark) {
        QueryWrapper<Friendship> updateWrapper = new QueryWrapper<>();
        updateWrapper.eq("my_id", myId).eq("friend_id", friendId);
        Friendship friendship = new Friendship();
        friendship.setFriendRemark(friendRemark);
        friendship.setUpdatedTime(LocalDateTime.now());
        friendshipMapper.update(friendship, updateWrapper);
    }

    @Override
    @Transactional
    public void updateBlackList(String myId, String friendId, YesOrNo yesOrNo) {
        QueryWrapper<Friendship> updateWrapper = new QueryWrapper<>();
        updateWrapper.eq("my_id", myId).eq("friend_id", friendId);
        Friendship friendship = new Friendship();
        friendship.setIsBlack(yesOrNo.type);
        friendship.setUpdatedTime(LocalDateTime.now());
        friendshipMapper.update(friendship, updateWrapper);
    }
}