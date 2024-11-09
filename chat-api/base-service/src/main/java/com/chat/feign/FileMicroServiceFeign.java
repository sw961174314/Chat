package com.chat.feign;

import com.chat.grace.result.GraceJSONResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "file-service")
public interface FileMicroServiceFeign {

    @PostMapping("/file/generatorOrCode")
    public String generatorOrCode(@RequestParam("wechatNumber") String wechatNumber, @RequestParam("userId") String userId) throws Exception;
}