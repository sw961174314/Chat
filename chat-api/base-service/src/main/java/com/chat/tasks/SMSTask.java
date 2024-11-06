package com.chat.tasks;

import com.chat.utils.SMSUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 短信发送
 */
@Slf4j
@Component
public class SMSTask {

    @Autowired
    private SMSUtils smsUtils;

    @Async
    public void sendSMSInTask(String mobile,String code) throws Exception {
        // 接入腾讯云短信服务后可以开启
        // smsUtils.sendSMS(mobile,code);
        log.info("异步任务中所发送的验证码为：{}",code);
    }
}