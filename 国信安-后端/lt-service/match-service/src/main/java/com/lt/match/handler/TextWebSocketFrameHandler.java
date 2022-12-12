package com.lt.match.handler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lt.match.SessionUtils;
import com.lt.match.config.NettyConfig;
import com.lt.model.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


import java.nio.charset.StandardCharsets;

/**
 * @author Lhz
 */
@Service
@ChannelHandler.Sharable
public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<WebSocketFrame> {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    Gson gson = new Gson();
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame webSocketFrame) throws Exception {
        if(webSocketFrame instanceof CloseWebSocketFrame){
            ctx.channel().close();
        }
        if(webSocketFrame instanceof PingWebSocketFrame){
            ctx.channel().write(new PongWebSocketFrame(webSocketFrame.content().retain()));
        }
        assert webSocketFrame instanceof TextWebSocketFrame;
        TextWebSocketFrame textWebSocketFrame = (TextWebSocketFrame) webSocketFrame;
        ByteBuf byteBuf = textWebSocketFrame.content();

        String content = byteBuf.toString(StandardCharsets.UTF_8);
        System.out.println(content);
        JsonObject jsonObject = JsonParser.parseString(content).getAsJsonObject();
        byte type = jsonObject.get("messageType").getAsByte();
        ctx.fireChannelRead(gson.fromJson(content, Message.MESSAGE_CLASSES.get(type)));
//        Gson gson = new Gson();
//        Message message = Message.MESSAGE_CLASSES.get(type).newInstance();
//
//        System.out.println("----------------------MessageType--------------------" + type);
//        if (type == UserMessage.LOGIN_MESSAGE) {
//            message = gson.fromJson(content, LoginMessage.class);
//            System.out.println(message.toString());
//        } else if (type == UserMessage.LOGOUT_MESSAGE) {
//            message = gson.fromJson(content, LogoutMessage.class);
//            System.out.println(message.toString());
//        }else if(type == MatchMessage.HEART_FAILED_MESSAGE){
//            message = gson.fromJson(content, MatchFailedMessage.class);
//        }else if(type == MatchMessage.HEART_INTERRUPT_MESSAGE){
//            message = gson.fromJson(content, MatchInterruptMessage.class);
//        }else if (type == MatchMessage.HEART_TALK_LAUNCH_MESSAGE) {
//            message = gson.fromJson(content, MatchLaunchMessage.class);
//        }else if (type == MatchMessage.HEART_QUIT_MESSAGE) {
//            message = gson.fromJson(content, MatchQuitMessage.class);
//        }else if (type == MatchMessage.HEART_SUCCESS_MESSAGE) {
//            message = gson.fromJson(content, MatchSuccessMessage.class);
//        }
//        ctx.fireChannelRead(Message.MESSAGE_CLASSES.get(type));
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        NettyConfig.channelGroup.add(ctx.channel());
        System.out.println("客户端与服务端连接开启....");
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("新客户端通道已注册。。。");
        super.channelRegistered(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        NettyConfig.channelGroup.remove(ctx.channel());
        SessionUtils.unbind(ctx.channel());
        System.out.println("客户端与服务端连接关闭....");
    }

    /**
     * 接收结束之后 read相对于服务端
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    /**
     *  出现异常时调用
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("客户端通道出现异常。。。");
        cause.printStackTrace();
        SessionUtils.unbind(ctx.channel());
        ctx.close();
    }
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("客户端通道已取消注册。。。");
        super.channelUnregistered(ctx);
    }


}
