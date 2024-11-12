package com.chat.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chat.base.BaseInfoProperties;
import com.chat.mapper.FriendCircleMapper;
import com.chat.mapper.FriendCircleMapperCustom;
import com.chat.pojo.FriendCircle;
import com.chat.pojo.bo.FriendCircleBO;
import com.chat.pojo.vo.FriendCircleVO;
import com.chat.service.FriendCircleService;
import com.chat.utils.PagedGridResult;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * 朋友圈表 服务实现类
 */
@Service
public class FriendCircleServiceImpl extends BaseInfoProperties implements FriendCircleService {

    @Resource
    private FriendCircleMapper friendCircleMapper;

    @Resource
    private FriendCircleMapperCustom friendCircleMapperCustom;

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
}