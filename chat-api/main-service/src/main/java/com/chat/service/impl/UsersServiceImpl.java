package com.chat.service.impl;

import com.chat.base.BaseInfoProperties;
import com.chat.exceptions.GraceException;
import com.chat.grace.result.ResponseStatusEnum;
import com.chat.mapper.UsersMapper;
import com.chat.pojo.Users;
import com.chat.pojo.bo.ModifyUserBO;
import com.chat.service.UsersService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 用户表 服务实现类
 */
@Service
public class UsersServiceImpl extends BaseInfoProperties implements UsersService {

    @Resource
    private UsersMapper usersMapper;

    @Override
    public void modifyUserInfo(ModifyUserBO userBO) {
        // 用户id
        String userId = userBO.getUserId();
        // 微信号
        String wechatNum = userBO.getWechatNum();
        // 判断用户id是否为空
        if (StringUtils.isBlank(userId)) {
            GraceException.display(ResponseStatusEnum.USER_INFO_UPDATED_ERROR);
        }
        // 判断微信号是否还在禁止修改的时间区域内
        if (StringUtils.isNotBlank(wechatNum)) {
            String isExist = redis.get(REDIS_USER_ALREADY_UPDATE_WECHAT_NUM + ":" + userId);
            if (StringUtils.isNotBlank(isExist)) {
                GraceException.display(ResponseStatusEnum.WECHAT_NUM_ALREADY_MODIFIED_ERROR);
            }
        }
        Users pendingUser = new Users();
        pendingUser.setId(userId);
        pendingUser.setUpdatedTime(LocalDateTime.now());
        BeanUtils.copyProperties(userBO, pendingUser);
        // 将新的用户数据放入到数据库中
        usersMapper.updateById(pendingUser);
        // 如果用户修改微信号，一年内只能修改一次，放入Redis进行判断
        if (StringUtils.isNotBlank(wechatNum)) {
            redis.setByDays(REDIS_USER_ALREADY_UPDATE_WECHAT_NUM + ":" + userId, userId, 365);
        }
    }

    @Override
    public Users getById(String userId) {
        return usersMapper.selectById(userId);
    }
}