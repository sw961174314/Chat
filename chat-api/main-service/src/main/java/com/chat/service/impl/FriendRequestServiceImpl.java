package com.chat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chat.base.BaseInfoProperties;
import com.chat.enums.FriendRequestVerifyStatus;
import com.chat.mapper.FriendRequestMapper;
import com.chat.mapper.FriendRequestMapperCustom;
import com.chat.pojo.FriendRequest;
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
}