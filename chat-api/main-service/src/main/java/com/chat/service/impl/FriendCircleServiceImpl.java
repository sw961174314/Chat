package com.chat.service.impl;

import com.chat.base.BaseInfoProperties;
import com.chat.mapper.FriendCircleMapper;
import com.chat.pojo.FriendCircle;
import com.chat.pojo.bo.FriendCircleBO;
import com.chat.service.FriendCircleService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 朋友圈表 服务实现类
 */
@Service
public class FriendCircleServiceImpl extends BaseInfoProperties implements FriendCircleService {

    @Resource
    private FriendCircleMapper friendCircleMapper;

    @Override
    @Transactional
    public void publish(FriendCircleBO friendCircleBO) {
        FriendCircle pendingFriendCircle = new FriendCircle();
        BeanUtils.copyProperties(friendCircleBO, pendingFriendCircle);
        friendCircleMapper.insert(pendingFriendCircle);
    }
}