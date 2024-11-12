package com.chat.mapper;

import org.apache.ibatis.annotations.Param;
import com.chat.pojo.vo.CommentVO;

import java.util.List;
import java.util.Map;

/**
 * 朋友圈评论表 Mapper 接口
 */
public interface CommentMapperCustom {

    public List<CommentVO> queryFriendCircleComments(@Param("paramMap") Map<String, Object> map);
}