package com.chat.service;

import com.chat.pojo.bo.CommentBO;
import com.chat.pojo.vo.CommentVO;

/**
 * 朋友圈评论表 服务类
 */
public interface CommentService {

    /**
     * 对朋友圈发表评论
     * @param commentBO
     */
    public CommentVO createComment(CommentBO commentBO);
}