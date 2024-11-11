package com.chat.service;

import com.chat.pojo.Friendship;
import com.chat.pojo.vo.ContactsVO;

import java.util.List;

/**
 * 好友关系表 服务类
 */
public interface FriendshipService {

    /**
     * 获取好友关系数据
     * @param myId
     * @param friendId
     * @return
     */
    public Friendship getFriendship(String myId,String friendId);

    /**
     * 查询通讯录
     * @param myId
     * @return
     */
    public List<ContactsVO> queryMyFriends(String myId);
}