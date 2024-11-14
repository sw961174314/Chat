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
    public List<ContactsVO> queryMyFriends(String myId,boolean needBlack) {
        Map<String, Object> map = new HashMap<>();
        map.put("myId", myId);
        map.put("needBlack", needBlack);
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

    @Override
    @Transactional
    public void delete(String myId, String friendId) {
        QueryWrapper<Friendship> deleteWrapper1 = new QueryWrapper<>();
        deleteWrapper1.eq("my_id", myId).eq("friend_id", friendId);
        friendshipMapper.delete(deleteWrapper1);
        QueryWrapper<Friendship> deleteWrapper2 = new QueryWrapper<>();
        deleteWrapper2.eq("my_id", friendId).eq("friend_id", myId);
        friendshipMapper.delete(deleteWrapper2);
    }

    @Override
    public boolean isBlackEachOther(String friendId1, String friendId2) {
        QueryWrapper<Friendship> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("my_id", friendId1).eq("friend_id", friendId2).eq("is_black",YesOrNo.YES.type);
        Friendship friendship1 = friendshipMapper.selectOne(queryWrapper1);
        QueryWrapper<Friendship> queryWrapper2 = new QueryWrapper<>();
        queryWrapper2.eq("my_id", friendId2).eq("friend_id", friendId1).eq("is_black",YesOrNo.YES.type);
        Friendship friendship2 = friendshipMapper.selectOne(queryWrapper1);
        return friendship1 != null || friendship2 != null;
    }
}