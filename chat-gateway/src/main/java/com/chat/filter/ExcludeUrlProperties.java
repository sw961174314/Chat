package com.chat.filter;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "exclude")
@PropertySource("classpath:excludeUrlPath.properties")
public class ExcludeUrlProperties {
    private List<String> urls;
}