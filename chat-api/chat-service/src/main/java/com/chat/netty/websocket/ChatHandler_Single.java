package com.chat.netty.websocket;

import com.a3test.component.idworker.IdWorkerConfigBean;
import com.a3test.component.idworker.Snowflake;
import com.chat.enums.MsgTypeEnum;
import com.chat.grace.result.GraceJSONResult;
import com.chat.netty.mq.MessagePublisher;
import com.chat.netty.util.JedisPoolUtils;
import com.chat.netty.util.ZookeeperRegister;
import com.chat.pojo.netty.ChatMsg;
import com.chat.pojo.netty.DataContent;
import com.chat.pojo.netty.NettyServerNode;
import com.chat.utils.JsonUtils;
import com.chat.utils.LocalDateUtils;
import com.chat.utils.OkHttpUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import redis.clients.jedis.Jedis;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 自定义助手类
 */
// TextWebSocketFrame：用于为websocket专门处理的文本数据对象，Frame是数据的载体
public class ChatHandler_Single extends SimpleChannelInboundHandler<TextWebSocketFrame> {

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
        // 判断是否是黑名单
        GraceJSONResult result = OkHttpUtil.get("http://127.0.0.1:1000/friendship/isBlack?friendId1=" + senderId + "&friendId2=" + receiverId);
        boolean isBlack = (boolean) result.getData();
        if (isBlack) {
            System.out.println("用户：" + senderId + "与用户：" + receiverId + "之间存在黑名单关系，无法正常发送消息");
            return;
        }
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

            // 获取当前节点
            NettyServerNode minNode = dataContent.getServerNode();
            // 初次连接后，该节点下的在线人数累加
            ZookeeperRegister.incrementOnlineCounts(minNode);
            // 获得ip和端口，在Redis中设置关系，以便在前端设备断开后减少在线人数
            Jedis jedis = JedisPoolUtils.getJedis();
            jedis.set(senderId, JsonUtils.objectToJson(minNode));
        } else if (msgType == MsgTypeEnum.WORDS.type || msgType == MsgTypeEnum.IMAGE.type || msgType == MsgTypeEnum.VIDEO.type || msgType == MsgTypeEnum.VOICE.type) {
            // 此处为mq异步解耦，保存信息到数据库，数据库无法获得信息的主键id，所以此处可以用snowflake生成主键id
            Snowflake snowflake = new Snowflake(new IdWorkerConfigBean());
            String sid = snowflake.nextId();
            chatMsg.setMsgId(sid);
            // 接收者channel
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
                        // 发送消息给接收者
                        findChannel.writeAndFlush(new TextWebSocketFrame(JsonUtils.objectToJson(dataContent)));
                    }
                }
            }
            // 把聊天信息作为mq的消息发送给消费者进行消费处理（保存到数据库）
            MessagePublisher.sendMsgToSave(chatMsg);
        }
        // 发送者的其他设备端的channel
        List<Channel> myOtherChannels = UserChannelSession.getMyOtherChannels(senderId, currentChannelIdLong);
        // 同步消息给当前发送者的其他设备端
        for (Channel channel : myOtherChannels) {
            Channel findChannel = clients.find(channel.id());
            if (findChannel != null) {
                dataContent.setChatMsg(chatMsg);
                dataContent.setChatTime(LocalDateUtils.format(chatMsg.getChatTime(), LocalDateUtils.DATETIME_PATTERN));
                // 其他设备同步消息
                findChannel.writeAndFlush(new TextWebSocketFrame(JsonUtils.objectToJson(dataContent)));
            }
        }
        // 输出userId和channel的关联数据
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
        // 获取当前服务节点，并减少人数
        Jedis jedis = JedisPoolUtils.getJedis();
        NettyServerNode minNode = JsonUtils.jsonToPojo(jedis.get(userId), NettyServerNode.class);
        ZookeeperRegister.decrementOnlineCounts(minNode);
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
        // 获取当前服务节点，并减少人数
        Jedis jedis = JedisPoolUtils.getJedis();
        NettyServerNode minNode = JsonUtils.jsonToPojo(jedis.get(userId), NettyServerNode.class);
        ZookeeperRegister.decrementOnlineCounts(minNode);
    }
}