package com.lt.match.handler.match;

import com.google.gson.Gson;
import com.lt.match.SessionUtils;
import com.lt.model.message.match.MatchSuccessMessage;
import com.lt.utils.EncodeUtil;
import io.netty.channel.*;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import javafx.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

/**
 * @author Lhz
 *
 * 匹配成功
 *
 */

@Service
@ChannelHandler.Sharable
public class MatchSuccessHandler extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if(msg instanceof MatchSuccessMessage){
            Integer talkId = ((MatchSuccessMessage) msg).getTalkId();
            Integer listenerId = ((MatchSuccessMessage) msg).getListenId();
            TextWebSocketFrame res = EncodeUtil.toTextWebSocketFrame( ctx, msg);
            Channel talkChannel = SessionUtils.USER_CHANNEL_MAP.get(talkId);
            Channel listenChannel = SessionUtils.USER_CHANNEL_MAP.get(listenerId);
            if(!ObjectUtils.isEmpty(talkChannel) && !ObjectUtils.isEmpty(listenChannel)){
                SessionUtils.SUCCESS_MAP.put(((MatchSuccessMessage) msg).getSuccessId(), new Pair<>(talkId, listenerId));
                System.out.println("说话者："+talkId+" 与 倾听者："+listenerId + "已经加入房间为："+((MatchSuccessMessage) msg).getSuccessId());
                talkChannel.writeAndFlush(res);
                listenChannel.writeAndFlush(res);
            }else{
                throw new RuntimeException("匹配成功时出现异常");
            }
        }
        super.write(ctx, msg, promise);
    }
}
