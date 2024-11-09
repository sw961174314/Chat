package com.chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

// exclude = DataSourceAutoConfiguration.class 去除数据源封装类的扫描 否则会因没有配置数据库信息而无法启动项目
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
// 开启服务的注册和发现功能
@EnableDiscoveryClient
@EnableFeignClients("com.chat.feign")
public class FileApplicaiton {
    public static void main(String[] args) {
        SpringApplication.run(FileApplicaiton.class, args);
    }
}