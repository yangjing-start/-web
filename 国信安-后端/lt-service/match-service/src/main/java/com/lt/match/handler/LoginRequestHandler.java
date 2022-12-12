package com.lt.match.handler;

import com.lt.model.message.user.LoginMessage;
import com.lt.match.SessionUtils;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.stereotype.Service;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * @author Lhz
 */

@Service
@ChannelHandler.Sharable
public class LoginRequestHandler extends SimpleChannelInboundHandler<LoginMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginMessage loginRequest) throws Exception {
        Integer userId = loginRequest.getUsername();
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd hh-mm-ss");
        System.out.println(userId + "已上线" + s.format(new Date()));
        SessionUtils.bindChannel(userId, ctx.channel());
    }

}
