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
        if (StringUtils.isBlank(userId)) {
            GraceException.display(ResponseStatusEnum.USER_INFO_UPDATED_ERROR);
        }
        Users pendingUser = new Users();
        pendingUser.setId(userId);
        pendingUser.setUpdatedTime(LocalDateTime.now());
        BeanUtils.copyProperties(userBO, pendingUser);
        // 将新的用户数据放入到数据库中
        usersMapper.updateById(pendingUser);
    }

    @Override
    public Users getById(String userId) {
        return usersMapper.selectById(userId);
    }
}