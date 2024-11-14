package com.chat.netty;

import com.chat.netty.handler.HttpServerInitializer;
import com.chat.netty.websocket.WSServerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Netty服务的启动类
 */
public class ChatApplication {
    public static void main(String[] args) throws Exception {
        // 定义主线程组，用于接收客户端的连接，但是不做任何处理
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        // 定义从线程池，处理主线程池交过来的任务
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            // 构建Netty服务器
            ServerBootstrap server = new ServerBootstrap();
            // 把主从线程池组放入到启动类中
            // channel()设置Nio双向通道
            // childHandler()设置处理器，主要用于处理workerGroup
            server.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).childHandler(new WSServerInitializer());
            // 启动server，并且绑定端口号为875，同时启动方式为同步
            ChannelFuture channelFuture = server.bind(875).sync();
            // 监听关闭的channel
            channelFuture.channel().closeFuture().sync();
        }finally {
            // 关闭线程池组
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}