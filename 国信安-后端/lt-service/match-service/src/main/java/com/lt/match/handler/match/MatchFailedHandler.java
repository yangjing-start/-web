package com.lt.match.handler.match;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.stereotype.Service;

/**
 * @author Lhz
 *
 * 匹配失败
 *
 */

@Service
@ChannelHandler.Sharable
public class MatchFailedHandler extends SimpleChannelInboundHandler<MatchFailedHandler> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MatchFailedHandler matchFailedHandler) throws Exception {

    }

}
