package com.lt.comment.service;

import com.lt.comment.dao.impl.PostMessageDaoImpl;
import com.lt.model.user.PostMessage;
import com.lt.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;


import static com.lt.comment.config.RabbitMqConfig.POST_MESSAGE_DELETE_ROUTING_KEY;

/**
 * @author Lhz
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class PostMessageService {

    private final PostMessageDaoImpl postMessageDao;
    private final RabbitTemplate rabbitTemplate;
    public List<PostMessage> getMessages(String username){
        return postMessageDao.getPostMessages(username);
    }

    public void insertPostMessage(PostMessage postMessage){
        postMessageDao.insertPostMessage(postMessage);
    }

    public void deletePostMessage(String username){
        rabbitTemplate.convertAndSend("comment.topic", POST_MESSAGE_DELETE_ROUTING_KEY, username);
    }
}
