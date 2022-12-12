package com.lt.match.config;

import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.ImmediateEventExecutor;

import javax.annotation.Resource;

/**
 * @author Lhz
 */
@Resource
public class NettyConfig {
    /**
     * 创建并保存已连接的websocketChannel
     */
    public static ChannelGroup channelGroup = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);
}
