package com.lt.match.handler.match;

import com.lt.match.SessionUtils;
import com.lt.model.message.match.MatchInterruptMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.stereotype.Service;

/**
 * @author Lhz
 *
 * 匹配中断
 *
 */

@Service
@ChannelHandler.Sharable
public class MatchInterruptHandler extends SimpleChannelInboundHandler<MatchInterruptMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MatchInterruptMessage msg) throws Exception {
        Integer matchType = msg.getMatchType();
        if(matchType == 1){
            SessionUtils.LISTEN_USER_SET.remove(msg.getUsername());
        }else{
            SessionUtils.TALK_USER_SET.remove(msg.getUsername());
        }
    }

}
