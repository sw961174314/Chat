package com.chat.controller;

import com.chat.config.MinIOConfig;
import com.chat.config.MinIOUtils;
import com.chat.grace.result.GraceJSONResult;
import com.chat.grace.result.ResponseStatusEnum;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("file")
public class FileController {

    @Resource
    private MinIOConfig minIOConfig;

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
        return GraceJSONResult.ok(faceUrl);
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
}