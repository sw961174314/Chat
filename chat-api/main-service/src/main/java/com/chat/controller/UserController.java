package com.chat.controller;

import com.chat.base.BaseInfoProperties;
import com.chat.grace.result.GraceJSONResult;
import com.chat.pojo.Users;
import com.chat.pojo.bo.ModifyUserBO;
import com.chat.pojo.vo.UsersVO;
import com.chat.service.UsersService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("userinfo")
public class UserController extends BaseInfoProperties {

    @Resource
    private UsersService usersService;

    /**
     * 修改用户基本信息
     * @param userBO
     * @return
     */
    @PostMapping("modify")
    public GraceJSONResult modify(@RequestBody ModifyUserBO userBO) {
        // 修改用户信息
        usersService.modifyUserInfo(userBO);
        // 返回最新用户信息
        UsersVO usersVO = getUserInfo(userBO.getUserId(), true);
        return GraceJSONResult.ok(usersVO);
    }

    /**
     * 获取用户信息
     * @param userId
     * @return
     */
    @PostMapping("get")
    public GraceJSONResult get(@RequestParam("userId") String userId) {
        return GraceJSONResult.ok(getUserInfo(userId, false));
    }

    /**
     * 用户上传头像
     * @param userId
     * @param face
     * @return
     */
    @PostMapping("updateFace")
    public GraceJSONResult updateFace(@RequestParam("userId") String userId,@RequestParam("face") String face) {
        ModifyUserBO userBO = new ModifyUserBO();
        userBO.setFace(face);
        UsersVO usersVO = getUsersVO(userId, userBO);
        return GraceJSONResult.ok(usersVO);
    }

    /**
     * 用户上传朋友圈背景图
     * @param userId
     * @param friendCircleBg
     * @return
     */
    @PostMapping("uploadFriendCircleBg")
    public GraceJSONResult uploadFriendCircleBg(@RequestParam("userId") String userId,@RequestParam("friendCircleBg") String friendCircleBg) {
        ModifyUserBO userBO = new ModifyUserBO();
        userBO.setFriendCircleBg(friendCircleBg);
        UsersVO usersVO = getUsersVO(userId, userBO);
        return GraceJSONResult.ok(usersVO);
    }

    /**
     * 用户上传聊天背景图上传
     * @param userId
     * @param chatBg
     * @return
     */
    @PostMapping("uploadChatBg")
    public GraceJSONResult uploadChatBg(@RequestParam("userId") String userId,@RequestParam("chatBg") String chatBg) {
        ModifyUserBO userBO = new ModifyUserBO();
        userBO.setChatBg(chatBg);
        UsersVO usersVO = getUsersVO(userId, userBO);
        return GraceJSONResult.ok(usersVO);
    }

    // 获取最新用户信息
    private UsersVO getUserInfo(String userId,boolean needToken) {
        // 查询获得用户的最新信息
        Users latestUser = usersService.getById(userId);
        // 用户封装类
        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(latestUser, usersVO);
        if (needToken) {
            String uToken = TOKEN_USER_PREFIX + SYMBOL_DOT + UUID.randomUUID();
            redis.set(REDIS_USER_TOKEN + ":" + userId, uToken);
            usersVO.setUserToken(uToken);
        }
        return usersVO;
    }

    @NotNull
    private UsersVO getUsersVO(String userId, ModifyUserBO userBO) {
        userBO.setUserId(userId);
        // 修改用户信息
        usersService.modifyUserInfo(userBO);
        // 返回最新用户信息
        UsersVO usersVO = getUserInfo(userBO.getUserId(), true);
        return usersVO;
    }
}