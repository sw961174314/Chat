package com.chat.filter;

import com.chat.base.BaseInfoProperties;
import com.chat.grace.result.ResponseStatusEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 路由鉴权过滤器
 */
@Slf4j
@Component
// 用于Nacos配置中心的动态刷新
@RefreshScope
public class SecurityFilterToken extends BaseInfoProperties implements GlobalFilter, Ordered {

    @Resource
    private ExcludeUrlProperties excludeUrlProperties;

    // 路径匹配规则器
    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1.获得当前用户请求的路径url
        String url = exchange.getRequest().getURI().getPath();
        log.info("当前请求的路径：" + url);
        // 2.获得所有需要排除校验的url
        List<String> excludeList = excludeUrlProperties.getUrls();
        // 3.校验并且排除excludeList
        if (excludeList != null && !excludeList.isEmpty()) {
            for (String excludeUrl : excludeList) {
                if (antPathMatcher.matchStart(excludeUrl, url)) {
                    // 如果匹配到，则直接放行，表示当前url不需要被拦截
                    return chain.filter(exchange);
                }
            }
        }
        // 4.拦截，进行校验
        log.info("当前请求被拦截的路径：" + url);
        // 5.从header获得userId和userToken
        HttpHeaders headers = exchange.getRequest().getHeaders();
        String userId = headers.getFirst(HEADER_USER_ID);
        String userToken = headers.getFirst(HEADER_USER_TOKEN);
        log.info("userId：{}，userToken：{}", userId, userToken);
        // 6.判断header中是否有token，对用户请求进行判断拦截
        if (StringUtils.isNotBlank(userId) && StringUtils.isNotBlank(userToken)) {
            String redisToken = redis.get(REDIS_USER_TOKEN + ":" + userId);
            if (redisToken.equals(userToken)) {
                return chain.filter(exchange);
            }
        }
        // 默认不放行
        return RenderErrorUtils.display(exchange, ResponseStatusEnum.UN_LOGIN);
    }

    /**
     * 过滤器顺序，数字越小则优先级越大
     * @return
     */
    @Override
    public int getOrder() {
        return 0;
    }
}