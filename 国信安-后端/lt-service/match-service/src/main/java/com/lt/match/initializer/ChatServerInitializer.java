package com.lt.match.initializer;

import com.lt.match.handler.LoginRequestHandler;
import com.lt.match.handler.TextWebSocketFrameHandler;
import com.lt.match.handler.match.*;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.springframework.stereotype.Service;

/**
 * @author Lhz
 */
@Service
public class ChatServerInitializer extends ChannelInitializer<Channel> {

    @Override
    protected void initChannel(Channel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();
        //http request 解码器
        pipeline.addLast(new HttpServerCodec());
        //写入一个文件的内容
        pipeline.addLast(new ChunkedWriteHandler());
        //将一个HttpMessage 和跟随他的多个HttpContent聚合为单个FullHttpRequest 安装了这个之后 ChannelPipeline中的下一个ChannelHandler将会收到完整的HTTP请求或响应
        pipeline.addLast(new HttpObjectAggregator(1024 * 1024 * 10));
        //添加websocket解编编码器
        pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
        //Debug
        pipeline.addLast(new LoggingHandler(LogLevel.DEBUG));
        //处理TextWebSocketFrameMessage和握手完成时间
        pipeline.addLast(new TextWebSocketFrameHandler());
        //登录消息
        pipeline.addLast(new LoginRequestHandler());
        //匹配成功(出站Handler)
        pipeline.addLast(new MatchSuccessHandler());
        //匹配发起
        pipeline.addLast(new MatchLaunchHandler());
        //匹配中断
        pipeline.addLast(new MatchInterruptHandler());
        //匹配后退出
        pipeline.addLast(new MatchQuitHandler());
        //匹配失败
        pipeline.addLast(new MatchFailedHandler());
    }
}
