package com.chat.service;

import com.chat.enums.YesOrNo;
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
     * 查询通讯录/黑名单
     * @param myId
     * @return
     */
    public List<ContactsVO> queryMyFriends(String myId,boolean needBlack);

    /**
     * 修改好友备注
     * @param myId
     * @param friendId
     * @param friendRemark
     */
    public void updateFriendRemark(String myId, String friendId, String friendRemark);

    /**
     * 将好友移入黑名单或移出黑名单
     * @param myId
     * @param friendId
     */
    public void updateBlackList(String myId, String friendId,YesOrNo yesOrNo);

    /**
     * 删除好友(删除好友之间的两个记录)
     * @param myId
     * @param friendId
     */
    public void delete(String myId, String friendId);

    /**
     * 判断两个用户之间的关系是否是拉黑关系
     * @param friendId1
     * @param friendId2
     * @return
     */
    public boolean isBlackEachOther(String friendId1, String friendId2);
}