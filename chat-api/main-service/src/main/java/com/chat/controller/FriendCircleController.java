package com.chat.controller;

import com.chat.base.BaseInfoProperties;
import com.chat.grace.result.GraceJSONResult;
import com.chat.pojo.bo.FriendCircleBO;
import com.chat.service.FriendCircleService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("friendCircle")
public class FriendCircleController extends BaseInfoProperties {

    @Resource
    private FriendCircleService friendCircleService;

    /**
     * 发布朋友圈图文数据并保存
     * @param friendCircleBO
     * @param request
     * @return
     */
    @PostMapping("publish")
    public GraceJSONResult publish(@RequestBody FriendCircleBO friendCircleBO, HttpServletRequest request) {
        String userId = request.getHeader(HEADER_USER_ID);
        friendCircleBO.setUserId(userId);
        friendCircleBO.setPublishTime(LocalDateTime.now());
        friendCircleService.publish(friendCircleBO);
        return GraceJSONResult.ok();
    }
}