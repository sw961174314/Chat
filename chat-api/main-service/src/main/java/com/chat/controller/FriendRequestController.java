package com.chat.controller;

import com.chat.base.BaseInfoProperties;
import com.chat.grace.result.GraceJSONResult;
import com.chat.pojo.bo.NewFriendRequestBO;
import com.chat.service.FriendRequestService;
import com.chat.utils.PagedGridResult;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("friendRequest")
public class FriendRequestController extends BaseInfoProperties {

    @Resource
    private FriendRequestService friendRequestService;

    /**
     * 新增添加好友请求
     * @param friendRequestBO
     * @return
     */
    @PostMapping("add")
    public GraceJSONResult add(@Valid @RequestBody NewFriendRequestBO friendRequestBO) {
        friendRequestService.addNewRequest(friendRequestBO);
        return GraceJSONResult.ok();
    }

    /**
     * 查询新朋友的请求记录列表
     * @param page
     * @param pageSize
     * @param request
     * @return
     */
    @PostMapping("queryNew")
    public GraceJSONResult queryNew(@RequestParam(defaultValue = "1", name = "page") Integer page, @RequestParam(defaultValue = "10", name = "pageSize") Integer pageSize, HttpServletRequest request) {
        // 用户id
        String userId = request.getHeader(HEADER_USER_ID);
        PagedGridResult result = friendRequestService.queryNewFriendList(userId, page, pageSize);
        return GraceJSONResult.ok(result);
    }
}