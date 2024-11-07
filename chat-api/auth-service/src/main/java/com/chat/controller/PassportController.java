package com.chat.controller;

import com.chat.base.BaseInfoProperties;
import com.chat.pojo.bo.RegistLoginBO;
import com.chat.grace.result.GraceJSONResult;
import com.chat.tasks.SMSTask;
import com.chat.utils.IPUtil;
import com.chat.utils.MyInfo;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("passport")
public class PassportController extends BaseInfoProperties {

    @Autowired
    private SMSTask smsTask;

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
    public GraceJSONResult regist(@RequestBody RegistLoginBO registLoginBO,HttpServletRequest request) {
        String mobile = registLoginBO.getMobile();
        String smsCode = registLoginBO.getSmsCode();
        // 1.从Redis中获得验证码进行校验判断是否匹配

        // 2.根据mobile查询数据库，如果用户存在，则提示不能重复注册
        // 2.1 如果查询数据库中用户为空，表示用户没有注册过，则需要进行用户信息数据的入库

        // 3.用户注册成功后，删除Redis中的短信验证码使其失效

        // 4.返回用户数据给前端

        return GraceJSONResult.ok();
    }
}