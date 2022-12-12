package com.lt.match.handler.match;

import com.google.gson.Gson;
import com.lt.match.SessionUtils;
import com.lt.model.message.match.MatchLaunchMessage;
import com.lt.model.message.match.MatchSuccessMessage;
import com.lt.utils.EncodeUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

/**
 * @author Lhz
 *
 * 匹配发起
 *
 */

@Service
@ChannelHandler.Sharable
public class MatchLaunchHandler extends SimpleChannelInboundHandler<MatchLaunchMessage> {
    /**
     *  0 : Talk
     *  1 : Listen
     */
    Gson gson = new Gson();
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MatchLaunchMessage msg) throws Exception {
        Integer matchType = msg.getMatchType();
        Integer talkId, listenId;
        if(matchType == 1){
            System.out.println(msg.getUsername()+"已加入倾听队列");
            listenId = msg.getUsername();
            synchronized (SessionUtils.TALK_USER_SET){
                talkId = SessionUtils.TALK_USER_SET.pollFirst();
            }
            if(!ObjectUtils.isEmpty(talkId)){
                ctx.channel().writeAndFlush(EncodeUtil.toTextWebSocketFrame( ctx, new MatchSuccessMessage(String.valueOf(System.currentTimeMillis()), talkId, listenId)));
                return ;
            }
            SessionUtils.LISTEN_USER_SET.add(msg.getUsername());
        }else{
            System.out.println(msg.getUsername()+"已加入说话队列");
            talkId = msg.getUsername();
            synchronized (SessionUtils.LISTEN_USER_SET){
                listenId = SessionUtils.LISTEN_USER_SET.pollFirst();
            }
            if(!ObjectUtils.isEmpty(listenId)){
                ctx.channel().writeAndFlush(EncodeUtil.toTextWebSocketFrame(ctx, new MatchSuccessMessage(String.valueOf(System.currentTimeMillis()), talkId, listenId)));
                return ;
            }
            SessionUtils.TALK_USER_SET.add(msg.getUsername());
        }
    }
}
