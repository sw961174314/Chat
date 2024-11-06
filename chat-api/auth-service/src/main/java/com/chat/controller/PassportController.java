package com.chat.controller;

import com.chat.base.BaseInfoProperties;
import com.chat.grace.result.GraceJSONResult;
import com.chat.tasks.SMSTask;
import com.chat.utils.MyInfo;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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
    @GetMapping("getSMSCode")
    public GraceJSONResult getSMSCode(String mobile, HttpServletRequest request) throws Exception {
        // 判断手机号码是否为空
        if (StringUtils.isBlank(mobile)) {
            return GraceJSONResult.error();
        }
        // 验证码
        String code = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
        log.info("code:" + code);
        // 发送验证码
         smsTask.sendSMSInTask(MyInfo.getMobile(), code);
        // 把验证码存入Redis
        redis.set(MOBILE_SMSCODE + ":" + mobile, code, 30 * 60);
        return GraceJSONResult.ok();
    }
}