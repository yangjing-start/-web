package com.lt.match.initializer;

import io.netty.channel.Channel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLEngine;

/**
 * 安全聊天服务器初始化
 *
 * @author Lhz
 * @date 2022/11/15
 */
public class SecureChatServerInitializer extends ChatServerInitializer{

    /**
     * 上下文
     */
    private final SslContext context;

    /**
     * 安全聊天服务器初始化
     *
     * @param context 上下文
     */
    public SecureChatServerInitializer(SslContext context) {
        this.context = context;
    }

    /**
     * 初始化通道
     *
     * @param channel 通道
     * @throws Exception 异常
     */
    @Override
    protected void initChannel(Channel channel) throws Exception {
        super.initChannel(channel);
        SSLEngine engine = context.newEngine(channel.alloc());
        engine.setUseClientMode(false);
        channel.pipeline().addFirst(new SslHandler(engine));
    }
}
