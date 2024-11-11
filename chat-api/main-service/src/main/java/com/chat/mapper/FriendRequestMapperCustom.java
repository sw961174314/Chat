package com.chat.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chat.pojo.vo.NewFriendsVO;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * 好友请求记录表 Mapper 接口
 */
public interface FriendRequestMapperCustom {

    public Page<NewFriendsVO> queryNewFriendList(@Param("page") Page<NewFriendsVO> page, @Param("paramMap") Map<String, Object> map);
}