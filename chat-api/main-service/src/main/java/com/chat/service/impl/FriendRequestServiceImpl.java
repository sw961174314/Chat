package com.chat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chat.base.BaseInfoProperties;
import com.chat.enums.FriendRequestVerifyStatus;
import com.chat.mapper.FriendRequestMapper;
import com.chat.pojo.FriendRequest;
import com.chat.pojo.bo.NewFriendRequestBO;
import com.chat.service.FriendRequestService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 好友请求表 服务实现类
 */
@Service
public class FriendRequestServiceImpl extends BaseInfoProperties implements FriendRequestService {

    @Resource
    private FriendRequestMapper friendRequestMapper;

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
}