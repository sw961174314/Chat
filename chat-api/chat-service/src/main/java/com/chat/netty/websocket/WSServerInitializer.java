package com.chat.netty.websocket;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * WebSocket初始化器，channel注册后，会执行里面相应的方法
 */
public class WSServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        // 通过SocketChannel获得对应的管道
        ChannelPipeline pipeline = channel.pipeline();

        /* 用于支持http协议相关的handler */
        // websocket 基于http协议，所以需要有http的编解码器
        pipeline.addLast(new HttpServerCodec());
        // 对大数据流的支持
        pipeline.addLast(new ChunkedWriteHandler());
        // 对httpMessage进行聚合，聚合成FullHttpRequest或FullHttpResponse（几乎在netty的编程中，都会使用到此handler）
        pipeline.addLast(new HttpObjectAggregator(1024 * 64));

        /* 用于支持websocket */
        // WebSocket服务器处理的协议，用于指定给客户端连接的时候访问路由：/ws
        // 此handler会帮我们处理一些比较复杂繁重的操作，会处理一些握手操作：handShaking（clost,ping,pong）ping+pong=心跳
        // 对于WebSocket来说，数据都是以frames进行传输的，不同的数据类型对应不同的frames
        pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
        // 添加自定义的助手类
        pipeline.addLast(new ChatHandler());
    }
}