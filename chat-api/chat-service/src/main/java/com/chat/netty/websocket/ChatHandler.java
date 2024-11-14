package com.chat.netty.websocket;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * 自定义助手类
 */
// TextWebSocketFrame：用于为websocket专门处理的文本数据对象，Frame是数据的载体
public class ChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    // 用于记录和管理所有客户端的channel组
    public static ChannelGroup clients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        // 获得客户端传输过来的消息
        String content = msg.text();
        System.out.println("接收到的数据：" + content);
        // 获取channel
        Channel currentChannel = ctx.channel();
        String currentChannelIdLong = currentChannel.id().asLongText();
        String currentChannelIdShort = currentChannel.id().asShortText();
        System.out.println("客户端currentChannelIdLong：" + currentChannelIdLong);
        System.out.println("客户端currentChannelIdShort：" + currentChannelIdShort);
        // 将数据返回给前端
        currentChannel.writeAndFlush(new TextWebSocketFrame(currentChannelIdLong));
    }

    /**
     * 客户端建立连接，连接到服务端之后（打开连接）
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel currentChannel = ctx.channel();
        String currentChannelIdLong = currentChannel.id().asLongText();
        System.out.println("客户端建立连接，channel对应的长id为：" + currentChannelIdLong);
        // 获得客户端的channel，并且存入到ChannelGroup中进行管理（作为一个客户端群组）
        clients.add(currentChannel);
    }

    /**
     * 客户端关闭连接，移除channel
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel currentChannel = ctx.channel();
        String currentChannelIdLong = currentChannel.id().asLongText();
        System.out.println("客户端关闭连接，channel对应的长id为：" + currentChannelIdLong);
        // 客户端关闭连接
        clients.remove(currentChannel);
    }

    /**
     * 发生异常并且捕获，移除channel
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel currentChannel = ctx.channel();
        String currentChannelIdLong = currentChannel.id().asLongText();
        System.out.println("发生异常捕获，channel对应的长id为：" + currentChannelIdLong);
        // 发生异常，关闭连接（关闭channel）
        ctx.channel().close();
        // 随后从ChannelGroup中移除对应的channel
        clients.remove(currentChannel);
    }
}