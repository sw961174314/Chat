package com.chat.controller;

import com.chat.grace.result.GraceJSONResult;
import com.chat.pojo.bo.NewFriendRequestBO;
import com.chat.service.FriendRequestService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("friendRequest")
public class FriendRequestController {

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
}