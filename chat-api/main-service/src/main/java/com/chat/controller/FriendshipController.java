package com.chat.controller;

import com.chat.base.BaseInfoProperties;
import com.chat.grace.result.GraceJSONResult;
import com.chat.pojo.Friendship;
import com.chat.pojo.vo.ContactsVO;
import com.chat.service.FriendshipService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("friendship")
public class FriendshipController extends BaseInfoProperties {

    @Resource
    private FriendshipService friendshipService;

    /**
     * 获取好友关系数据
     * @param friendId
     * @param request
     * @return
     */
    @PostMapping("getFriendship")
    public GraceJSONResult getFriendship(String friendId, HttpServletRequest request) {
        String myId = request.getHeader(HEADER_USER_ID);
        Friendship friendship = friendshipService.getFriendship(myId, friendId);
        return GraceJSONResult.ok(friendship);
    }

    /**
     * 查询通讯录
     * @param request
     * @return
     */
    @PostMapping("queryMyFriends")
    public GraceJSONResult queryMyFriends(HttpServletRequest request) {
        String myId = request.getHeader(HEADER_USER_ID);
        List<ContactsVO> list = friendshipService.queryMyFriends(myId);
        return GraceJSONResult.ok(list);
    }

    /**
     * 修改好友备注
     * @param friendId
     * @param friendRemark
     * @param request
     * @return
     */
    @PostMapping("updateFriendsRemark")
    public GraceJSONResult updateFriendsRemark(String friendId,String friendRemark,HttpServletRequest request) {
        String myId = request.getHeader(HEADER_USER_ID);
        if (StringUtils.isBlank(friendId) || StringUtils.isBlank(friendRemark)) {
            return GraceJSONResult.error();
        }
        friendshipService.updateFriendRemark(myId, friendId, friendRemark);
        return GraceJSONResult.ok();
    }
}