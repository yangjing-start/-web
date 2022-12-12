package com.lt.utils;

import com.google.gson.Gson;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * @author Lhz
 */
@Component
public class EncodeUtil {

    static Gson gson = new Gson();
    public static TextWebSocketFrame toTextWebSocketFrame(ChannelHandlerContext ctx, Object msg){
        String res = gson.toJson(msg);
        ByteBuf byteBuf = ctx.alloc().buffer();
        byte[] bytes = res.getBytes(StandardCharsets.UTF_8);
        byteBuf.writeBytes(bytes);
        return new TextWebSocketFrame(byteBuf);
    }
}
