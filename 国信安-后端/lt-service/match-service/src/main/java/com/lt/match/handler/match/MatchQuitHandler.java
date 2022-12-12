package com.lt.match.handler.match;

import com.lt.match.SessionUtils;
import com.lt.model.message.match.MatchQuitMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.stereotype.Service;

/**
 * @author Lhz
 *
 * 匹配成功后退出
 *
 */

@Service
@ChannelHandler.Sharable
public class MatchQuitHandler extends SimpleChannelInboundHandler<MatchQuitMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MatchQuitMessage msg) throws Exception {
        String successId = msg.getSuccessId();
        System.out.println(successId+"已经解散");
        SessionUtils.SUCCESS_MAP.remove(successId);
    }

}
