package com.chat.controller;

import com.chat.base.BaseInfoProperties;
import com.chat.grace.result.GraceJSONResult;
import com.chat.pojo.bo.CommentBO;
import com.chat.pojo.vo.CommentVO;
import com.chat.service.CommentService;
import com.chat.service.FriendCircleService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("comment")
public class CommentController extends BaseInfoProperties {

    @Resource
    private CommentService commentService;

    /**
     * 对朋友圈发表评论
     * @param friendCircleBO
     * @param request
     * @return
     */
    @PostMapping("create")
    public GraceJSONResult create(@RequestBody CommentBO friendCircleBO, HttpServletRequest request) {
        CommentVO comment = commentService.createComment(friendCircleBO);
        return GraceJSONResult.ok(comment);
    }
}