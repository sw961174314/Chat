package com.chat.controller;

import com.chat.base.BaseInfoProperties;
import com.chat.enums.YesOrNo;
import com.chat.grace.result.GraceJSONResult;
import com.chat.pojo.Friendship;
import com.chat.pojo.vo.ContactsVO;
import com.chat.service.FriendshipService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
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
        List<ContactsVO> list = friendshipService.queryMyFriends(myId,false);
        return GraceJSONResult.ok(list);
    }

    /**
     * 查询黑名单
     * @param request
     * @return
     */
    @PostMapping("queryMyBlackList")
    public GraceJSONResult queryMyBlackList(HttpServletRequest request) {
        String myId = request.getHeader(HEADER_USER_ID);
        List<ContactsVO> list = friendshipService.queryMyFriends(myId, true);
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

    /**
     * 将好友移入黑名单
     * @param friendId
     * @param request
     * @return
     */
    @PostMapping("tobeBlack")
    public GraceJSONResult tobeBlack(String friendId,HttpServletRequest request) {
        String myId = request.getHeader(HEADER_USER_ID);
        if (StringUtils.isBlank(friendId)) {
            return GraceJSONResult.error();
        }
        friendshipService.updateBlackList(myId, friendId, YesOrNo.NO);
        return GraceJSONResult.ok();
    }

    /**
     * 将好友移出黑名单
     * @param friendId
     * @param request
     * @return
     */
    @PostMapping("moveOutBlack")
    public GraceJSONResult moveOutBlack(String friendId,HttpServletRequest request) {
        String myId = request.getHeader(HEADER_USER_ID);
        if (StringUtils.isBlank(friendId)) {
            return GraceJSONResult.error();
        }
        friendshipService.updateBlackList(myId, friendId, YesOrNo.NO);
        return GraceJSONResult.ok();
    }

    /**
     * 删除好友
     * @param friendId
     * @param request
     * @return
     */
    @PostMapping("delete")
    public GraceJSONResult delete(String friendId,HttpServletRequest request) {
        String myId = request.getHeader(HEADER_USER_ID);
        if (StringUtils.isBlank(friendId)) {
            return GraceJSONResult.error();
        }
        friendshipService.delete(myId, friendId);
        return GraceJSONResult.ok();
    }

    /**
     * 判断两个用户之间的关系是否是拉黑关系
     * @param friendId1
     * @param friendId2
     * @return
     */
    @GetMapping("isBlack")
    public GraceJSONResult isBlack(String friendId1, String friendId2) {
        // 需要查询两次 A拉黑B，B拉黑A，AB相互拉黑
        // 只需要符合其中的一个条件，就表示双方发送消息不可送达
        return GraceJSONResult.ok(friendshipService.isBlackEachOther(friendId1, friendId2));
    }
}