package com.chat.controller;

import com.chat.base.BaseInfoProperties;
import com.chat.grace.result.GraceJSONResult;
import com.chat.pojo.FriendCircleLiked;
import com.chat.pojo.bo.FriendCircleBO;
import com.chat.pojo.vo.FriendCircleVO;
import com.chat.service.FriendCircleService;
import com.chat.utils.PagedGridResult;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

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
        // 增加朋友圈点赞数据
        List<FriendCircleVO> list = (List<FriendCircleVO>) result.getRows();
        for (FriendCircleVO f : list) {
            String friendCircleId = f.getFriendCircleId();
            List<FriendCircleLiked> likedList = friendCircleService.queryLikedFriends(friendCircleId);
            f.setLikedFriends(likedList);
            boolean res = friendCircleService.doILike(friendCircleId, userId);
            f.setDoILike(res);
        }
        return GraceJSONResult.ok(result);
    }

    /**
     * 朋友圈点赞
     * @param friendCircleId
     * @param request
     * @return
     */
    @PostMapping("like")
    public GraceJSONResult like(String friendCircleId,HttpServletRequest request) {
        String userId = request.getHeader(HEADER_USER_ID);
        if (StringUtils.isBlank(userId)) {
            return GraceJSONResult.error();
        }
        friendCircleService.like(friendCircleId, userId);
        return GraceJSONResult.ok();
    }

    /**
     * 朋友圈取消点赞
     * @param friendCircleId
     * @param request
     * @return
     */
    @PostMapping("unlike")
    public GraceJSONResult unlike(String friendCircleId,HttpServletRequest request) {
        String userId = request.getHeader(HEADER_USER_ID);
        if (StringUtils.isBlank(userId)) {
            return GraceJSONResult.error();
        }
        friendCircleService.unlike(friendCircleId, userId);
        return GraceJSONResult.ok();
    }
}