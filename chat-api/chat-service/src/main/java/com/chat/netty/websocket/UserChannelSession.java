package com.chat.netty.websocket;

import com.chat.pojo.netty.DataContent;
import com.chat.utils.JsonUtils;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 会话管理，用户id和channel的关联处理
 */
public class UserChannelSession {

    // 用于多端同时接收消息，允许同一账号在多个设备同时在线
    // key：userId，value：多个用户的channel
    private static Map<String, List<Channel>> multiSession = new HashMap<>();
    // 用于记录用户id和客户端channel长id的关联关系
    private static Map<String, String> userChannelIdRelation = new HashMap<>();

    public static void putUserChannelIdRelation(String channelId, String userId) {
        userChannelIdRelation.put(channelId, userId);
    }

    public static String getUserIdByChannelId(String channelId) {
        return userChannelIdRelation.get(channelId);
    }

    public static void putMultiChannels(String userId, Channel channel) {
        List<Channel> channels = getMultiChannels(userId);
        if (channels == null || channels.size() == 0) {
            channels = new ArrayList<>();
        }
        channels.add(channel);
        multiSession.put(userId, channels);
    }

    public static List<Channel> getMultiChannels(String userId) {
        return multiSession.get(userId);
    }

    public static void removeUselessChannels(String userId, String channelId) {
        List<Channel> channels = getMultiChannels(userId);
        if (channels == null || channels.size() == 0) {
            return;
        }
        for (int i = 0; i < channels.size(); i++) {
            Channel tempChannel = channels.get(i);
            if (tempChannel.id().asLongText().equals(channelId)) {
                channels.remove(i);
            }
        }
        multiSession.put(userId, channels);
    }

    /**
     * 获取当前用户的其他channel
     * @param userId
     * @param channelId
     * @return
     */
    public static List<Channel> getMyOtherChannels(String userId,String channelId) {
        List<Channel> channels = getMultiChannels(userId);
        if (channels == null || channels.size() == 0) {
            return null;
        }
        List<Channel> myOtherChannels = new ArrayList<>();
        for (int i = 0; i < channels.size(); i++) {
            Channel tempChannel = channels.get(i);
            if (!tempChannel.id().asLongText().equals(channelId)) {
                myOtherChannels.add(tempChannel);
            }
        }
        return myOtherChannels;
    }

    /**
     * 输出userId和channel的关联数据
     */
    public static void outputMulti() {
        System.out.println("========");
        for (Map.Entry<String, List<Channel>> entry : multiSession.entrySet()) {
            System.out.println("UserId：" + entry.getKey());
            List<Channel> temp = entry.getValue();
            for (Channel channel : temp) {
                System.out.println("ChannelId：" + channel.id().asLongText());
            }
        }
        System.out.println("========");
    }

    /**
     * 同步给当前接收者的其他设备聊天信息
     * @param receiverChannels
     * @param dataContent
     */
    public static void sendToTarget(List<Channel> receiverChannels, DataContent dataContent) {
        ChannelGroup clients = ChatHandler.clients;
        if (receiverChannels == null) {
            return;
        }
        for (Channel c : receiverChannels) {
            Channel findChannel = clients.find(c.id());
            if (findChannel != null) {
                findChannel.writeAndFlush(new TextWebSocketFrame(JsonUtils.objectToJson(dataContent)));
            }
        }
    }
}