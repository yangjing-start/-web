package com.lt.comment.listener;

import com.lt.comment.dao.CommentDao;
import com.lt.comment.dao.SubCommentDao;
import com.lt.comment.service.CommentService;
import com.lt.comment.service.SubCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import static com.lt.comment.config.RabbitMqConfig.DELETE_COMMENT_QUEUE_NAME;

/**
 * @author Lhz
 */
@Component
@RequiredArgsConstructor
public class CommentListener {

    private final CommentDao commentDao;
    private final SubCommentDao subCommentDao;
    @RabbitListener(queues = DELETE_COMMENT_QUEUE_NAME)
    public void deleteCommentListener(String id){
        commentDao.deleteComment(id);
        subCommentDao.deleteAllSubComment(id);
    }

}
