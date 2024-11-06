package com.chat.config;

import com.chat.interceptor.SMSInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 拦截配置 否则拦截器不生效
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    /**
     * 在SpringBoot容器中放入拦截器
     * @return
     */
    @Bean
    public SMSInterceptor smsInterceptor() {
        return new SMSInterceptor();
    }

    /**
     * 注册拦截器，并且拦截指定的路由
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(smsInterceptor())
                .addPathPatterns(
                        "/passport/getSMSCode"
                );
    }
}