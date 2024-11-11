package com.chat.mapper;

import com.chat.pojo.vo.ContactsVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 好友关系表 Mapper 接口
 */
public interface FriendshipMapperCustom {

    public List<ContactsVO> queryMyFriends(@Param("paramMap") Map<String, Object> map);

}