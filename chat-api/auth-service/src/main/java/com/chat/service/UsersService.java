package com.chat.service;

import com.chat.pojo.Users;

/**
 * 用户表 服务类
 */
public interface UsersService {

    /**
     * 判断用户是否存在，如果存在则返回用户信息，否则null
     * @param mobile
     * @return
     */
    public Users queryMobileIfExist(String mobile);

    /**
     * 创建用户信息，并且返回用户对象
     * @param mobile
     * @param nickName
     * @return
     */
    public Users createUsers(String mobile,String nickName);
}