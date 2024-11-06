package com.chat.filter;

import com.chat.base.BaseInfoProperties;
import com.chat.grace.result.GraceJSONResult;
import com.chat.grace.result.ResponseStatusEnum;
import com.chat.utils.IPUtil;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * 网关过滤器
 */
@Slf4j
@Component
// 用于Nacos配置中心的动态刷新
@RefreshScope
public class IPLimitFilter extends BaseInfoProperties implements GlobalFilter, Ordered {

    // 请求次数
    @Value("${blackIp.continueCounts}")
    private Integer continueCounts;

    // 请求时间间隔
    @Value("${blackIp.timeInterval}")
    private Integer timeInterval;

    // 限制时间
    @Value("${blackIp.limitTimes}")
    private Integer limitTimes;

    /**
     * 需求：
     * 判断某个请求的ip在20秒内的请求是否超过3次
     * 如果超过3次，则限制访问30秒
     * 等待30秒静默后，才能够继续恢复访问
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // ip访问次数判断
        return doLimit(exchange, chain);
    }

    /**
     * 限制IP请求次数的判断
     * @param exchange
     * @param chain
     * @return
     */
    public Mono<Void> doLimit(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1.根据request请求获取ip
        ServerHttpRequest request = exchange.getRequest();
        String ip = IPUtil.getIP(request);
        // 2.定义正常ip
        final String ipRedisKey = "gateway-ip:" + ip;
        // 3.定义被拦截的黑名单ip，如果该ip在Redis中存在，则表示目前ip处于限制状态
        final String ipRedisLimitKey = "gateway-ip:limit:" + ip;
        // 4.获取当前的ip并查询还剩下多少时间，如果剩余时间大于0，则表示当前ip处于限制状态
        long limitLeftTimes = redis.ttl(ipRedisLimitKey);
        if (limitLeftTimes > 0) {
            // 终止请求
            return renderErrorMsg(exchange, ResponseStatusEnum.SYSTEM_ERROR_BLACK_IP);
        }
        // 5.在Redis中获得ip的累加次数
        long requestCounts = redis.increment(ipRedisKey, 1);
        // 6.判断如果是第一次进来，也就是从0开始计数，则初期访问就是1，需要设置间隔的时间，也就是连续请求的次数的间隔时间
        if (requestCounts == 1) {
            redis.expire(ipRedisKey, timeInterval);
        }
        // 7.如果还能获得请求的正常次数，说明用户的连续请求落在限定时间之内，一旦请求次数超过限定的连续访问次数，则需要进行限制
        if (requestCounts > continueCounts) {
            // 限制ip访问
            redis.set(ipRedisLimitKey, ipRedisLimitKey, limitTimes);
            // 终止请求
            return renderErrorMsg(exchange, ResponseStatusEnum.SYSTEM_ERROR_BLACK_IP);
        }
        // 8.默认放行请求到后续的路由(服务)
        return chain.filter(exchange);
    }

    /**
     * 重新包装并返回错误信息
     * @param exchange
     * @param statusEnum
     * @return
     */
    public Mono<Void> renderErrorMsg(ServerWebExchange exchange, ResponseStatusEnum statusEnum) {
        // 1.获得响应response
        ServerHttpResponse response = exchange.getResponse();
        // 2.构建jsonResult
        GraceJSONResult jsonResult = GraceJSONResult.exception(statusEnum);
        // 3.设置header类型
        if (!response.getHeaders().containsKey("Content-Type")) {
            response.getHeaders().add("Content-Type", MimeTypeUtils.APPLICATION_JSON_VALUE);
        }
        // 4.修改response的状态码code为500
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        // 5.转换json并向response中写入数据
        String result = new Gson().toJson(jsonResult);
        DataBuffer buffer = response.bufferFactory().wrap(result.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

    /**
     * 过滤器顺序，数字越小则优先级越大
     * @return
     */
    @Override
    public int getOrder() {
        return 1;
    }
}