package com.chat.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chat.pojo.vo.FriendCircleVO;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * 朋友圈表 Mapper 接口
 */
public interface FriendCircleMapperCustom {

    public Page<FriendCircleVO> queryFriendCircleList(@Param("page") Page<FriendCircleVO> page, @Param("paramMap") Map<String, Object> map);
}