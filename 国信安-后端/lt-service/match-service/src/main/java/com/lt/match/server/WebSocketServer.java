package com.lt.match.server;

import com.lt.match.initializer.ChatServerInitializer;
import com.lt.match.initializer.SecureChatServerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

import static com.lt.match.config.NettyConfig.channelGroup;

/**
 * 网络套接字服务器
 *
 * @author Lhz
 * @date 2022/11/15
 */
@ChannelHandler.Sharable
@Component
public class WebSocketServer {


    /**
     * 运行
     *
     * @param address 地址
     */
    public void run(InetSocketAddress address){
        EventLoopGroup boss = new NioEventLoopGroup(2);
        EventLoopGroup worker = new NioEventLoopGroup(4);
        ServerBootstrap bootstrap = new ServerBootstrap();
        try{
            // 设置SSL证书
//            SelfSignedCertificate cert = new SelfSignedCertificate();
//            SslContext context = SslContext.newServerContext(cert.certificate(), cert.privateKey());
//            final SecureChatServerInitializer chatServerInitializer = new SecureChatServerInitializer(context);
            ChatServerInitializer chatServerInitializer = new ChatServerInitializer();
            // 启动服务
            bootstrap.group(boss, worker)
                    .option(ChannelOption.SO_BACKLOG, 1024 * 50)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(chatServerInitializer);
            // 同步服务
            ChannelFuture future = bootstrap.bind(address).sync();
            future.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            // 关闭所有服务 释放所有资源
            channelGroup.close();
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

}
