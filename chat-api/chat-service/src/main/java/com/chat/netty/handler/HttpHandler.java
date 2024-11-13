package com.chat.netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.springframework.http.MediaType;

/**
 * 自定义Http助手类
 */
// SimpleChannelInboundHandler：对于请求来说，相当于入站
public class HttpHandler extends SimpleChannelInboundHandler<HttpObject> {

    /**
     * 从管道中读取数据
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        // 获取channel
        Channel channel = ctx.channel();
        // 打印客户端的远程地址
        System.out.println(channel.remoteAddress());
        // 通过缓冲区定义发送的消息，读写数据都是通过缓冲区进行数据交换
        ByteBuf content = Unpooled.copiedBuffer("test", CharsetUtil.UTF_8);
        // 构建http的response
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);
        // 为响应添加数据类型和数据长度
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, MediaType.TEXT_HTML_VALUE);
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());
        // 把响应数据写到缓冲区再写入客户端
        ctx.writeAndFlush(response);
    }
}