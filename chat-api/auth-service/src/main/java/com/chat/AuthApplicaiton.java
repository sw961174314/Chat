package com.chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
// 开启服务的注册和发现功能
@EnableDiscoveryClient
public class AuthApplicaiton {
    public static void main(String[] args) {
        SpringApplication.run(AuthApplicaiton.class, args);
    }
}