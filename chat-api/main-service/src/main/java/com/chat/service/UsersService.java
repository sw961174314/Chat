package com.chat.service;

import com.chat.pojo.Users;
import com.chat.pojo.bo.ModifyUserBO;

/**
 * 用户表 服务类
 */
public interface UsersService {

    /**
     * 修改用户基本信息
     * @param userBO
     * @return
     */
    public void modifyUserInfo(ModifyUserBO userBO);

    /**
     * 获取最新用户信息
     * @param userId
     * @return
     */
    public Users getById(String userId);
}