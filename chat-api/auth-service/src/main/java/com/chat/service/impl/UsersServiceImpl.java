package com.chat.service.impl;

import jakarta.annotation.Resource;
import com.chat.base.BaseInfoProperties;
import com.chat.mapper.UsersMapper;
import com.chat.pojo.Users;
import com.chat.service.UsersService;
import org.springframework.stereotype.Service;

/**
 * 用户表 服务实现类
 */
@Service
public class UsersServiceImpl extends BaseInfoProperties implements UsersService {

    @Resource
    private UsersMapper usersMapper;

    @Override
    public Users queryMobileIfExist(String mobile) {
        return null;
    }

    @Override
    public Users createUsers(String mobile, String nickname) {
        return null;
    }
}