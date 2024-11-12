package com.chat.controller;

import com.chat.base.BaseInfoProperties;
import com.chat.grace.result.GraceJSONResult;
import com.chat.pojo.bo.FriendCircleBO;
import com.chat.service.FriendCircleService;
import com.chat.utils.PagedGridResult;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
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

    /**
     * 朋友圈图文查询
     * @param userId
     * @param page
     * @param pageSize
     * @return
     */
    @PostMapping("queryList")
    public GraceJSONResult queryList(String userId,@RequestParam(defaultValue = "1", name = "page") Integer page, @RequestParam(defaultValue = "10", name = "pageSize") Integer pageSize) {
        if (StringUtils.isBlank(userId)) {
            return GraceJSONResult.error();
        }
        PagedGridResult result = friendCircleService.queryList(userId, page, pageSize);
        return GraceJSONResult.ok(result);
    }
}