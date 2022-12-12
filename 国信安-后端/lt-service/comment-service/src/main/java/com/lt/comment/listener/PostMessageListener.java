package com.lt.comment.listener;

import com.lt.comment.dao.PostMessageDao;
import com.lt.comment.dao.impl.PostMessageDaoImpl;
import com.lt.comment.service.PostMessageService;
import com.lt.model.mq.MQPostMessage;
import com.lt.model.user.PostMessage;
import com.lt.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.lt.comment.config.RabbitMqConfig.POST_MESSAGE_DELETE_QUEUE_NAME;
import static com.lt.comment.config.RabbitMqConfig.POST_MESSAGE_QUEUE_NAME;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostMessageListener {

    private final PostMessageService postMessageService;
    private final PostMessageDaoImpl postMessageDao;
    @RabbitListener(queues = POST_MESSAGE_QUEUE_NAME)
    public void addPostMessage(String postMessage){
        MQPostMessage mqPostMessage = JsonUtils.toBean(postMessage, MQPostMessage.class);
        PostMessage message = PostMessage.builder().message("您被" + mqPostMessage.otherNickname + "关注了")
                .nickName(mqPostMessage.otherNickname).image(mqPostMessage.otherUserImage).type(2).otherId(mqPostMessage.otherUsername)
                .selfId(mqPostMessage.selfUsername).time(String.valueOf(System.currentTimeMillis())).build();
        System.out.println(message.toString());
        postMessageService.insertPostMessage(message);
    }

    @RabbitListener(queues = POST_MESSAGE_DELETE_QUEUE_NAME)
    public void deletePostMessage(String username){
        postMessageDao.deletePostMessage(username);
    }
}
