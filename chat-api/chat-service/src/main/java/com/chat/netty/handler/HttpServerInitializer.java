package com.chat.netty.handler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * channel初始化器，channel注册后，会执行里面相应的方法
 */
public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        // 通过SocketChannel获得对应的管道
        ChannelPipeline pipeline = channel.pipeline();
        // 通过管道，添加handler处理器
        // HttpServerCodec 是由netty自己提供的助手类，可以理解为管道中的拦截器
        // 当请求到达服务端，需要进行解码，相应到客户端做编码
        pipeline.addLast("HttpServerCodec", new HttpServerCodec());
        // 添加自定义的助手类
        pipeline.addLast("HttpHanndler", new HttpHandler());
    }
}