package com.chat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * 静态资源工具类
 */
@Configuration
public class StaticResourceConfig extends WebMvcConfigurationSupport {

    /**
     * 添加静态资源映射路径，图片、视频、音频等都放在classpath下的static中
     * @param registry
     */
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        /**
         * addResourceHandler：指的是对外暴露的访问路径映射
         * addResourceLocations：指的是本地文件所在目录
         * 访问地址：127.0.0.1:1000/static/文件名
         */
        registry.addResourceHandler("/static/**")
                .addResourceLocations("file:E:/Project/Chat/upload/face/");
        super.addResourceHandlers(registry);
    }
}