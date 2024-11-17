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
        if (msg instanceof HttpRequest) {
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

    /**
     * channel注册
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channel注册");
        super.channelRegistered(ctx);
    }

    /**
     * channel取消注册
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channel取消注册");
        super.channelUnregistered(ctx);
    }

    /**
     * 活跃状态
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channel活跃");
        super.channelActive(ctx);
    }

    /**
     * 静默状态
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channel静默状态");
        super.channelInactive(ctx);
    }

    /**
     * channel读取数据完毕
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channel读取数据完毕");
        super.channelReadComplete(ctx);
    }

    /**
     * 用户事件触发
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        System.out.println("用户事件触发");
        super.userEventTriggered(ctx, evt);
    }

    /**
     * channel可写更改
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channel可写更改");
        super.channelWritabilityChanged(ctx);
    }

    /**
     * 捕获异常
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("捕获异常");
        super.exceptionCaught(ctx, cause);
    }

    /**
     * handler添加
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.println("handler添加");
        super.handlerAdded(ctx);
    }

    /**
     * handler移除
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println("handler移除");
        super.handlerRemoved(ctx);
    }
}