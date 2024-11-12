package com.chat.service.impl;

import com.chat.base.BaseInfoProperties;
import com.chat.mapper.CommentMapper;
import com.chat.mapper.CommentMapperCustom;
import com.chat.pojo.Comment;
import com.chat.pojo.Users;
import com.chat.pojo.bo.CommentBO;
import com.chat.pojo.vo.CommentVO;
import com.chat.service.CommentService;
import com.chat.service.UsersService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 朋友圈评论表 服务实现类
 */
@Service
public class CommentServiceImpl extends BaseInfoProperties implements CommentService {

    @Resource
    private UsersService usersService;

    @Resource
    private CommentMapper commentMapper;

    @Resource
    private CommentMapperCustom commentMapperCustom;

    @Override
    @Transactional
    public CommentVO createComment(CommentBO commentBO) {
        // 新增评论
        Comment pendingComment = new Comment();
        BeanUtils.copyProperties(commentBO, pendingComment);
        pendingComment.setCreatedTime(LocalDateTime.now());
        commentMapper.insert(pendingComment);
        // 评论后的最新评论数据需要返回给前端（提供给前端做扩展数据）
        CommentVO commentVO = new CommentVO();
        BeanUtils.copyProperties(pendingComment, commentVO);
        // 评论用户的数据
        Users commentUser = usersService.getById(commentBO.getCommentUserId());
        commentVO.setCommentUserNickname(commentUser.getNickname());
        commentVO.setCommentUserFace(commentUser.getFace());
        commentVO.setCommentId(pendingComment.getId());
        return commentVO;
    }

    @Override
    public List<CommentVO> queryAll(String friendCircleId) {
        Map<String, Object> map = new HashMap<>();
        map.put("friendCircleId", friendCircleId);
        return commentMapperCustom.queryFriendCircleComments(map);
    }
}