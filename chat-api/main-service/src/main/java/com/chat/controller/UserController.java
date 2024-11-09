package com.chat.controller;

import com.chat.base.BaseInfoProperties;
import com.chat.grace.result.GraceJSONResult;
import com.chat.pojo.Users;
import com.chat.pojo.bo.ModifyUserBO;
import com.chat.pojo.vo.UsersVO;
import com.chat.service.UsersService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
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
     * @return
     */
    @PostMapping("updateFace")
    public GraceJSONResult updateFace(@RequestParam("userId") String userId,@RequestParam("face") String face) {
        ModifyUserBO userBO = new ModifyUserBO();
        userBO.setUserId(userId);
        userBO.setFace(face);
        // 修改用户信息
        usersService.modifyUserInfo(userBO);
        // 返回最新用户信息
        UsersVO usersVO = getUserInfo(userBO.getUserId(), true);
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
}