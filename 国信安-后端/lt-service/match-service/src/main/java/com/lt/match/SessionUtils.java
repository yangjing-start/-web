package com.lt.match;

import io.netty.channel.Channel;
import javafx.util.Pair;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * 会话
 *
 * @author Lhz
 * @date 2022/11/15
 */
public class SessionUtils {
    /**
     * 用户通道映射
     */
    public static final ConcurrentHashMap<Integer, Channel> USER_CHANNEL_MAP = new ConcurrentHashMap<>();

    /**
     * 通道用户地图
     */
    public static final ConcurrentHashMap<Channel, Integer> CHANNEL_USER_MAP = new ConcurrentHashMap<>();

    /**
     * 在线用户组
     */
    public static final ConcurrentSkipListSet<Integer> USER_SET = new ConcurrentSkipListSet<>();

    /**
     * 讨论用户集合
     */
    public static final ConcurrentSkipListSet<Integer> TALK_USER_SET = new ConcurrentSkipListSet<>();

    /**
     * 倾听用户集合
     */
    public static final ConcurrentSkipListSet<Integer> LISTEN_USER_SET = new ConcurrentSkipListSet<>();

    /**
     * 匹配成功集合
     */
    public static final ConcurrentHashMap<String, Pair<Integer, Integer>> SUCCESS_MAP = new ConcurrentHashMap<>();

    /**
     * 绑定通道
     *
     * @param username 用户名
     * @param channel  通道
     */
    public static void bindChannel(Integer username, Channel channel){
        USER_CHANNEL_MAP.put(username, channel);
        CHANNEL_USER_MAP.put(channel, username);
        USER_SET.add(username);
        channel.attr(Attributes.SESSION).set(username);
    }

    /**
     * 解开
     *
     * @param channel 通道
     */
    public static void unbind(Channel channel) {
        Integer username = CHANNEL_USER_MAP.remove(channel);
        if(username!=null){
            USER_CHANNEL_MAP.remove(username);
            USER_SET.remove(username);
            TALK_USER_SET.remove(username);
            LISTEN_USER_SET.remove(username);
        }
        channel.attr(Attributes.SESSION).set(null);
    }

}
