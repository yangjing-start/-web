package com.lt.comment.listener;

import com.lt.comment.dao.CommentDao;
import com.lt.comment.dao.SubCommentDao;
import com.lt.comment.listener.dto.SubCommentDTO;
import com.lt.comment.service.SubCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import static com.lt.comment.config.RabbitMqConfig.DELETE_SUB_COMMENT_QUEUE_NAME;

/**
 * @author Lhz
 */
@Component
@RequiredArgsConstructor
public class SubCommentListener {

    private final SubCommentDao subCommentDao;
    private final CommentDao commentDao;
    @RabbitListener(queues = DELETE_SUB_COMMENT_QUEUE_NAME)
    public void deleteSubCommentListener(SubCommentDTO dto){
        subCommentDao.deleteSubComment(dto.getFid(), dto.getId());
        commentDao.deleteSubCommentCount(dto.getFid());
    }

}
