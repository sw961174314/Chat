package com.chat.test;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.UploadObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 * MiniO测试类
 */
@Slf4j
public class MinIOTest {

    @Test
    public void testUpload() throws Exception {
        /**
         * 创建客户端
         * endpoint minio地址
         * credentials 账号密码
         */
        MinioClient minioClient = MinioClient.builder().endpoint("http://127.0.0.1:8000").credentials("minioRoot", "123456789").build();
        // 如果没有bucket，则需要创建
        String bucketName = "chat-local";
        // 是否存在对应的bucket
        boolean isExist = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (!isExist) {
            // 不存在则创建
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        } else {
            log.info("当前bucket：{}已存在", bucketName);
        }
        // 上传本地文件到minio中
        minioClient.uploadObject(UploadObjectArgs.builder().bucket(bucketName).object("myTestImage.jpg").filename("C:/Users/hp/Desktop/头像.jpg").build());
    }
}