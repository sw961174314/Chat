package com.chat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chat.base.BaseInfoProperties;
import com.chat.enums.FriendRequestVerifyStatus;
import com.chat.enums.YesOrNo;
import com.chat.mapper.FriendRequestMapper;
import com.chat.mapper.FriendRequestMapperCustom;
import com.chat.mapper.FriendshipMapper;
import com.chat.pojo.FriendRequest;
import com.chat.pojo.Friendship;
import com.chat.pojo.bo.NewFriendRequestBO;
import com.chat.pojo.vo.NewFriendsVO;
import com.chat.service.FriendRequestService;
import com.chat.utils.PagedGridResult;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 好友请求表 服务实现类
 */
@Service
public class FriendRequestServiceImpl extends BaseInfoProperties implements FriendRequestService {

    @Resource
    private FriendRequestMapper friendRequestMapper;

    @Resource
    private FriendRequestMapperCustom friendRequestMapperCustom;

    @Resource
    private FriendshipMapper friendshipMapper;

    @Override
    @Transactional
    public void addNewRequest(NewFriendRequestBO friendRequestBO) {
        // 先删除以前的记录
        QueryWrapper deleteWrapper = new QueryWrapper<FriendRequest>().eq("my_id", friendRequestBO.getMyId()).eq("friend_id", friendRequestBO.getFriendId());
        friendRequestMapper.delete(deleteWrapper);
        // 再新增记录
        FriendRequest pendingFriendRequest = new FriendRequest();
        BeanUtils.copyProperties(friendRequestBO, pendingFriendRequest);
        pendingFriendRequest.setVerifyStatus(FriendRequestVerifyStatus.WAIT.type);
        pendingFriendRequest.setRequestTime(LocalDateTime.now());
        friendRequestMapper.insert(pendingFriendRequest);
    }

    @Override
    public PagedGridResult queryNewFriendList(String userId, Integer page, Integer pageSize) {
        Map<String, Object> map = new HashMap<>();
        map.put("mySelfId", userId);
        Page<NewFriendsVO> pageInfo = new Page<>(page,pageSize);
        friendRequestMapperCustom.queryNewFriendList(pageInfo, map);
        return setterPagedGridPlus(pageInfo);
    }

    @Override
    @Transactional
    public void passNewFriend(String friendRequestId, String friendRemark) {
        FriendRequest friendRequest = getSingle(friendRequestId);
        // 通过方的用户id
        String mySelfId = friendRequest.getFriendId();
        // 申请方的用户id
        String myFriendId = friendRequest.getMyId();
        // 创建双方好友关系
        // 当前用户的好友关系
        LocalDateTime now = LocalDateTime.now();
        Friendship friendshipSelf = new Friendship();
        friendshipSelf.setMyId(mySelfId);
        friendshipSelf.setFriendId(myFriendId);
        friendshipSelf.setFriendRemark(friendRemark);
        friendshipSelf.setIsBlack(YesOrNo.NO.type);
        friendshipSelf.setIsMsgIgnore(YesOrNo.NO.type);
        friendshipSelf.setCreatedTime(now);
        friendshipSelf.setUpdatedTime(now);
        friendshipMapper.insert(friendshipSelf);
        // 当前用户的朋友的好友关系
        Friendship friendshipOpposite = new Friendship();
        friendshipOpposite.setMyId(myFriendId);
        friendshipOpposite.setFriendId(mySelfId);
        friendshipOpposite.setFriendRemark(friendRequest.getFriendRemark());
        friendshipOpposite.setIsBlack(YesOrNo.NO.type);
        friendshipOpposite.setIsMsgIgnore(YesOrNo.NO.type);
        friendshipOpposite.setCreatedTime(now);
        friendshipOpposite.setUpdatedTime(now);
        friendshipMapper.insert(friendshipOpposite);
        // A通过B的请求之后，需要把双方的好友请求记录都设置为“通过”
        friendRequest.setVerifyStatus(FriendRequestVerifyStatus.SUCCESS.type);
        friendRequestMapper.updateById(friendRequest);
        // 还有一种情况，A添加B，B没有通过，所以A发出的好友请求过期了
        // 但是，过期后，B向A发起好友请求，所以B被A通过后，那么两边的请求都应该“通过”
        QueryWrapper updateWrapper = new QueryWrapper<FriendRequest>().eq("my_id", myFriendId).eq("friend_id", mySelfId);
        FriendRequest requestOpposite = new FriendRequest();
        requestOpposite.setVerifyStatus(FriendRequestVerifyStatus.SUCCESS.type);
        friendRequestMapper.update(requestOpposite, updateWrapper);
    }

    private FriendRequest getSingle(String friendRequestId) {
        return friendRequestMapper.selectById(friendRequestId);
    }
}