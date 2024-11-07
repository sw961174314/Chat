package com.chat.controller;

import com.chat.base.BaseInfoProperties;
import com.chat.grace.result.ResponseStatusEnum;
import com.chat.pojo.Users;
import com.chat.pojo.bo.RegistLoginBO;
import com.chat.grace.result.GraceJSONResult;
import com.chat.pojo.vo.UsersVO;
import com.chat.service.UsersService;
import com.chat.tasks.SMSTask;
import com.chat.utils.IPUtil;
import com.chat.utils.MyInfo;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("passport")
public class PassportController extends BaseInfoProperties {

    @Resource
    private SMSTask smsTask;

    @Resource
    private UsersService usersService;

    /**
     * 发送短信
     * @return
     * @throws Exception
     */
    @PostMapping("getSMSCode")
    public GraceJSONResult getSMSCode(String mobile, HttpServletRequest request) throws Exception {
        // 判断手机号码是否为空
        if (StringUtils.isBlank(mobile)) {
            return GraceJSONResult.error();
        }
        // 获得用户的手机号/ip
        String userIp = IPUtil.getRequestIp(request);
        // 限制该用户的手机号/ip只能在60秒内获得一次验证码
        redis.setnx60s(MOBILE_SMSCODE + ":" + userIp, mobile);

        // 验证码
        String code = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
        log.info("code:" + code);
        // 发送验证码
        smsTask.sendSMSInTask(MyInfo.getMobile(), code);
        // 把验证码存入Redis
        redis.set(MOBILE_SMSCODE + ":" + mobile, code, 30 * 60);
        return GraceJSONResult.ok();
    }

    /**
     * 注册
     * @param registLoginBO
     * @param request
     * @return
     */
    @PostMapping("regist")
    public GraceJSONResult regist(@Valid @RequestBody RegistLoginBO registLoginBO,HttpServletRequest request) {
        String mobile = registLoginBO.getMobile();
        String code = registLoginBO.getSmsCode();
        String nickName = registLoginBO.getNickname();
        // 1.从Redis中获得验证码进行校验判断是否匹配
        String redisCode = redis.get(MOBILE_SMSCODE + ":" + mobile);
        if (StringUtils.isBlank(redisCode) || !redisCode.equalsIgnoreCase(code)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.SMS_CODE_ERROR);
        }
        // 2.根据mobile查询数据库，如果用户存在，则提示不能重复注册
        Users user = usersService.queryMobileIfExist(mobile);
        // 2.1 如果查询数据库中用户为空，表示用户没有注册过，则需要进行用户信息数据的入库
        if (user == null) {
            user = usersService.createUsers(mobile, nickName);
        } else {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_ALREADY_EXIST_ERROR);
        }
        // 3.用户注册成功后，删除Redis中的短信验证码使其失效
        redis.del(MOBILE_SMSCODE + ":" + mobile);
        // 4.设置用户分布式会话，保存用户的token令牌，存储到Redis中
        String uToken = TOKEN_USER_PREFIX + SYMBOL_DOT + UUID.randomUUID();
        // 本方式只能限制用户在一台设备进行登录
        // redis.set(REDIS_USER_TOKEN + ":" + user.getId(), uToken);   // 设置分布式会话
        // 本方式允许用户在多端多设备进行登录
        redis.set(REDIS_USER_TOKEN + ":" + uToken, user.getId());   // 设置分布式会话
        // 5.返回用户数据给前端
        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(user, usersVO);
        usersVO.setUserToken(uToken);
        return GraceJSONResult.ok(usersVO);
    }

    /**
     * 登录
     * @param registLoginBO
     * @param request
     * @return
     */
    @PostMapping("login")
    public GraceJSONResult login(@Valid @RequestBody RegistLoginBO registLoginBO, HttpServletRequest request) {
        String mobile = registLoginBO.getMobile();
        String code = registLoginBO.getSmsCode();
        // 1.从Redis中获得验证码进行校验判断是否匹配
        String redisCode = redis.get(MOBILE_SMSCODE + ":" + mobile);
        if (StringUtils.isBlank(redisCode) || !redisCode.equalsIgnoreCase(code)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.SMS_CODE_ERROR);
        }
        // 2.根据mobile查询数据库，如果用户存在，则提示不能重复注册
        Users user = usersService.queryMobileIfExist(mobile);
        // 2.1 如果查询数据库中用户为空，表示用户没有注册过，则需要返回错误信息
        if (user == null) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_NOT_EXIST_ERROR);
        }
        // 3.用户登录成功后，删除Redis中的短信验证码使其失效
        redis.del(MOBILE_SMSCODE + ":" + mobile);
        // 4.设置用户分布式会话，保存用户的token令牌，存储到Redis中
        String uToken = TOKEN_USER_PREFIX + SYMBOL_DOT + UUID.randomUUID();
        // 本方式只能限制用户在一台设备进行登录
        // redis.set(REDIS_USER_TOKEN + ":" + user.getId(), uToken);   // 设置分布式会话
        // 本方式允许用户在多端多设备进行登录
        redis.set(REDIS_USER_TOKEN + ":" + uToken, user.getId());   // 设置分布式会话
        // 5.返回用户数据给前端
        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(user, usersVO);
        usersVO.setUserToken(uToken);
        return GraceJSONResult.ok(usersVO);
    }
}