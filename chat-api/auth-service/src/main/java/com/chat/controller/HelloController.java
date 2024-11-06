package com.chat.controller;

import com.chat.utils.MyInfo;
import com.chat.utils.SMSUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("a")
public class HelloController {

    @Autowired
    private SMSUtils smsUtils;

    @Autowired
    private SMSTask smsTask;

    @GetMapping("hello")
    public Object Helllo() {
        return "Hello world~";
    }

    /**
     * 发送短信
     * @return
     * @throws Exception
     */
    @GetMapping("sms")
    public Object sms() throws Exception {
        smsUtils.sendSMS(MyInfo.getMobile(), "1234");
        return "Send SMS OK~";
    }

    /**
     * 发送短信
     * @return
     * @throws Exception
     */
    @GetMapping("smsTask")
    public Object smsTask() throws Exception {
        smsTask.sendSMSInTask(MyInfo.getMobile(), "1234");
        return "Send SMS In Task OK~";
    }
}