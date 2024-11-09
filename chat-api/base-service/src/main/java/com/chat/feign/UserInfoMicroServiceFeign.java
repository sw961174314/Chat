package com.chat.feign;

import com.chat.grace.result.GraceJSONResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "main-service")
public interface UserInfoMicroServiceFeign {

    @PostMapping("/userinfo/updateFace")
    public GraceJSONResult updateFace(@RequestParam("userId") String userId, @RequestParam("face") String face);

    @PostMapping("/userinfo/uploadFriendCircleBg")
    public GraceJSONResult uploadFriendCircleBg(@RequestParam("userId") String userId, @RequestParam("friendCircleBg") String friendCircleBg);

    @PostMapping("/userinfo/uploadChatBg")
    public GraceJSONResult uploadChatBg(@RequestParam("userId") String userId, @RequestParam("chatBg") String chatBg);
}