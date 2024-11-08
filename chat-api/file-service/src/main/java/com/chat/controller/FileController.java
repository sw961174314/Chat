package com.chat.controller;

import com.chat.grace.result.GraceJSONResult;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("file")
public class FileController {

    /**
     * 用户头像上传
     * @param file
     * @param userId
     * @param request
     * @return
     * @throws IOException
     */
    @PostMapping("uploadFace")
    public GraceJSONResult uploadFace(@RequestParam("file") MultipartFile file, String userId, HttpServletRequest request) throws IOException {
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