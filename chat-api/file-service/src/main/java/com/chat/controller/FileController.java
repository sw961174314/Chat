package com.chat.controller;

import com.chat.config.MinIOConfig;
import com.chat.config.MinIOUtils;
import com.chat.feign.UserInfoMicroServiceFeign;
import com.chat.grace.result.GraceJSONResult;
import com.chat.grace.result.ResponseStatusEnum;
import com.chat.pojo.vo.UsersVO;
import com.chat.utils.JsonUtils;
import com.chat.utils.QrCodeUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("file")
public class FileController {

    @Resource
    private MinIOConfig minIOConfig;

    @Resource
    private UserInfoMicroServiceFeign userInfoMicroServiceFeign;

    /**
     * 用户头像上传
     * @param file
     * @param userId
     * @param request
     * @return
     * @throws IOException
     */
    @PostMapping("uploadFace")
    public GraceJSONResult uploadFace(@RequestParam("file") MultipartFile file, String userId, HttpServletRequest request) throws Exception {
        if (StringUtils.isBlank(userId)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_FAILD);
        }
        // 获得文件原始名称
        String fileName = file.getOriginalFilename();
        if (StringUtils.isBlank(fileName)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_FAILD);
        }
        fileName = "face" + "/" + userId + "/" + fileName;
        // 上传图片到MinIO
        MinIOUtils.uploadFile(minIOConfig.getBucketName(), fileName, file.getInputStream());
        String faceUrl = minIOConfig.getFileHost() + "/" + minIOConfig.getBucketName() + "/" + fileName;
        /**
         * 微服务远程调用更新用户头像到数据库
         * 如果前段没有保存按钮则可以这样操作，如果有保存提交按钮，则在前端触发提交
         */
        GraceJSONResult jsonResult = userInfoMicroServiceFeign.updateFace(userId, faceUrl);
        Object data = jsonResult.getData();
        String json = JsonUtils.objectToJson(data);
        UsersVO usersVO = JsonUtils.jsonToPojo(json, UsersVO.class);
        return GraceJSONResult.ok(usersVO);
    }

    /**
     * 生成用户二维码
     * @param wechatNumber
     * @param userId
     * @return
     */
    @PostMapping("generatorOrCode")
    public String generatorOrCode(String wechatNumber, String userId) throws Exception {
        // 构建map对象
        Map<String, String> map = new HashMap<>();
        map.put("wechatNumber", wechatNumber);
        map.put("userId", userId);
        // 将map转成字符串
        String data = JsonUtils.objectToJson(map);
        // 生成二维码
        String qrCodePath = QrCodeUtils.generateQRCode(data);
        // 把二维码上传到minio中
        if (StringUtils.isNotBlank(qrCodePath)) {
            String uuid = UUID.randomUUID().toString();
            String objectName = "wechatNumber" + "/" + userId + "/" + uuid + ".png";
            String imageQrCodeUrl = MinIOUtils.uploadFile(minIOConfig.getBucketName(), objectName, qrCodePath, true);
            return imageQrCodeUrl;
        }
        return null;
    }

    /**
     * 朋友圈背景图上传
     * @param file
     * @param userId
     * @return
     * @throws Exception
     */
    @PostMapping("uploadFriendCircleBg")
    public GraceJSONResult uploadFriendCircleBg(@RequestParam("file") MultipartFile file, String userId) throws Exception {
        if (StringUtils.isBlank(userId)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_FAILD);
        }
        // 获得文件原始名称
        String fileName = file.getOriginalFilename();
        if (StringUtils.isBlank(fileName)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_FAILD);
        }
        fileName = "friendCircleBg" + "/" + userId + "/" + dealWithoutFileName(fileName);
        // 上传图片到MinIO
        String bgUrl = MinIOUtils.uploadFile(minIOConfig.getBucketName(), fileName, file.getInputStream(),true);
        /**
         * 微服务远程调用更新朋友圈背景图到数据库
         * 如果前段没有保存按钮则可以这样操作，如果有保存提交按钮，则在前端触发提交
         */
        GraceJSONResult jsonResult = userInfoMicroServiceFeign.uploadFriendCircleBg(userId, bgUrl);
        Object data = jsonResult.getData();
        String json = JsonUtils.objectToJson(data);
        UsersVO usersVO = JsonUtils.jsonToPojo(json, UsersVO.class);
        return GraceJSONResult.ok(usersVO);
    }

    /**
     * 聊天背景图上传
     * @param file
     * @param userId
     * @return
     * @throws Exception
     */
    @PostMapping("uploadChatBg")
    public GraceJSONResult uploadChatBg(@RequestParam("file") MultipartFile file, String userId) throws Exception {
        if (StringUtils.isBlank(userId)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_FAILD);
        }
        // 获得文件原始名称
        String fileName = file.getOriginalFilename();
        if (StringUtils.isBlank(fileName)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_FAILD);
        }
        fileName = "chatBg" + "/" + userId + "/" + dealWithoutFileName(fileName);
        // 上传图片到MinIO
        String bgUrl = MinIOUtils.uploadFile(minIOConfig.getBucketName(), fileName, file.getInputStream(),true);
        /**
         * 微服务远程调用更新聊天背景图到数据库
         * 如果前段没有保存按钮则可以这样操作，如果有保存提交按钮，则在前端触发提交
         */
        GraceJSONResult jsonResult = userInfoMicroServiceFeign.uploadChatBg(userId, bgUrl);
        Object data = jsonResult.getData();
        String json = JsonUtils.objectToJson(data);
        UsersVO usersVO = JsonUtils.jsonToPojo(json, UsersVO.class);
        return GraceJSONResult.ok(usersVO);
    }

    /**
     * 朋友圈图片上传
     * @param file
     * @param userId
     * @return
     * @throws Exception
     */
    @PostMapping("uploadFriendCircleImage")
    public GraceJSONResult uploadFriendCircleImage(@RequestParam("file") MultipartFile file, String userId) throws Exception {
        if (StringUtils.isBlank(userId)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_FAILD);
        }
        // 获得文件原始名称
        String fileName = file.getOriginalFilename();
        if (StringUtils.isBlank(fileName)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_FAILD);
        }
        fileName = "friendCircleImage" + "/" + userId + "/" + dealWithoutFileName(fileName);
        // 上传图片到MinIO
        String imageUrl = MinIOUtils.uploadFile(minIOConfig.getBucketName(), fileName, file.getInputStream(),true);
        return GraceJSONResult.ok(imageUrl);
    }

    /**
     * 朋友圈图片删除
     * @param imageUrl
     * @return
     * @throws Exception
     */
    @PostMapping("deleteFriendCircleImage")
    public GraceJSONResult deleteFriendCircleImage(String imageUrl) throws Exception {
        String imageName = imageUrl.substring((minIOConfig.getEndpoint() + "/" + minIOConfig.getBucketName() + "/").length()-1);
        // 批量删除图片
        MinIOUtils.removeFile(minIOConfig.getBucketName(), imageName);
        return GraceJSONResult.ok();
    }

    /**
     * 用户头像上传（老版本）
     * @param file
     * @param userId
     * @param request
     * @return
     * @throws IOException
     */
    @PostMapping("uploadFaceOld")
    public GraceJSONResult uploadFaceOld(@RequestParam("file") MultipartFile file, String userId, HttpServletRequest request) throws IOException {
        // 获得文件原始名称
        String fileName = file.getOriginalFilename();
        // 获取后缀名
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        // 新文件名
        String newFileName = userId + suffixName;
        // 文件存放路径
        String rootPath = new File("").getCanonicalPath() + "/upload/face";
        File filePath = new File(rootPath);
        if (!filePath.exists()) {
            // 如果目标文件所在目录不存在，则创建父级目录
            filePath.mkdirs();
        }
        File newFile = new File(rootPath, newFileName);
        // 将内存中的数据写入磁盘
        file.transferTo(newFile);
        return GraceJSONResult.ok();
    }

    private String dealWithFileName(String fileName) {
        // 后缀名
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        // 文件名
        String newFileName = fileName.substring(0,fileName.lastIndexOf("."));
        String uuid = UUID.randomUUID().toString();
        return newFileName + "-" + uuid + suffixName;
    }

    private String dealWithoutFileName(String fileName) {
        // 后缀名
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        String uuid = UUID.randomUUID().toString();
        return uuid + suffixName;
    }
}