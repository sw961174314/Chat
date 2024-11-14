package com.chat.netty.websocket;

import com.chat.enums.MsgTypeEnum;
import com.chat.pojo.netty.ChatMsg;
import com.chat.pojo.netty.DataContent;
import com.chat.utils.JsonUtils;
import com.chat.utils.LocalDateUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.time.LocalDateTime;
import java.util.List;

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
        // 1.获取客户端发来的消息并解析
        DataContent dataContent = JsonUtils.jsonToPojo(content, DataContent.class);
        // 用户的聊天内容entity
        ChatMsg chatMsg = dataContent.getChatMsg();
        // 聊天内容
        String msgText = chatMsg.getMsg();
        // 发送者的用户id
        String senderId = chatMsg.getSenderId();
        // 接受者的用户id
        String receiverId = chatMsg.getReceiverId();
        // 时间校准，以服务器的时间为准
        chatMsg.setChatTime(LocalDateTime.now());
        // 消息类型
        Integer msgType = chatMsg.getMsgType();
        // 获取channel
        Channel currentChannel = ctx.channel();
        String currentChannelIdLong = currentChannel.id().asLongText();
        String currentChannelIdShort = currentChannel.id().asShortText();

        // 2.判断消息类型，根据不同的类型来处理不同的业务
        // CONNECT_INIT：第一次(或重连)初始化连接
        // WORDS：文字消息
        if (msgType == MsgTypeEnum.CONNECT_INIT.type) {
            // 当websocket初次open的时候，初始化channel，把channel和用户userid关联起来
            UserChannelSession.putMultiChannels(senderId, currentChannel);
            UserChannelSession.putUserChannelIdRelation(currentChannelIdLong, senderId);
        } else if (msgType == MsgTypeEnum.WORDS.type) {
            // 发送消息
            List<Channel> receiverChannels = UserChannelSession.getMultiChannels(receiverId);
            // 判断当前接收者是否是离线状态
            if (receiverChannels == null || receiverChannels.size() == 0 || receiverChannels.isEmpty()) {
                // receiverChannels为空，表示用户离线/断线状态，消息不需要发送，后续可以存储到数据库中
                chatMsg.setIsReceiverOnLine(false);
            } else {
                chatMsg.setIsReceiverOnLine(true);
                // 当receiverChannels不为空的时候，同账户多端设备接收消息
                for (Channel channel : receiverChannels) {
                    Channel findChannel = clients.find(channel.id());
                    if (findChannel != null) {
                        dataContent.setChatMsg(chatMsg);
                        dataContent.setChatTime(LocalDateUtils.format(chatMsg.getChatTime(), LocalDateUtils.DATETIME_PATTERN));
                        // 发送消息给在线的用户
                        findChannel.writeAndFlush(new TextWebSocketFrame(JsonUtils.objectToJson(dataContent)));
                    }
                }
            }
        }
        UserChannelSession.outputMulti();
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
        // 获取用户id
        String userId = UserChannelSession.getUserIdByChannelId(currentChannelIdLong);
        // 移除多余的会话
        UserChannelSession.removeUselessChannels(userId, currentChannelIdLong);
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
        // 获取用户id
        String userId = UserChannelSession.getUserIdByChannelId(currentChannelIdLong);
        // 移除多余的会话
        UserChannelSession.removeUselessChannels(userId, currentChannelIdLong);
        // 发生异常，关闭连接（关闭channel）
        ctx.channel().close();
        // 随后从ChannelGroup中移除对应的channel
        clients.remove(currentChannel);
    }
}