package com.chat.netty.websocket;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

/**
 * 自定义助手类
 */
// TextWebSocketFrame：用于为websocket专门处理的文本数据对象，Frame是数据的载体
public class ChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

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
}