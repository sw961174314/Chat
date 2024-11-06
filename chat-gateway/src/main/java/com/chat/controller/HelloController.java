package com.chat.controller;

import com.chat.base.BaseInfoProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("test")
public class HelloController extends BaseInfoProperties {

    @GetMapping("hello")
    public Object Helllo() {
        return "Hello world~";
    }

    @GetMapping("setRedis")
    public Object setRedis(String k,String v) {
        redis.set(k, v);
        return "setRedis OK~";
    }

    @GetMapping("getRedis")
    public Object setRedis(String k) {
        return redis.get(k);
    }
}