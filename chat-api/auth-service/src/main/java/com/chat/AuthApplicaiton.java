package com.chat;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
// 开启服务的注册和发现功能
@EnableDiscoveryClient
// 指定扫包路径
@MapperScan(basePackages = "com.chat.mapper")
public class AuthApplicaiton {
    public static void main(String[] args) {
        SpringApplication.run(AuthApplicaiton.class, args);
    }
}