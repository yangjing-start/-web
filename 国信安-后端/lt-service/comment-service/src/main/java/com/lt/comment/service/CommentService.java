package com.lt.comment.service;

import com.lt.comment.dao.impl.CommentDaoImpl;
import com.lt.comment.dao.impl.SubCommentDaoImpl;
import com.lt.model.comment.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.lt.comment.config.RabbitMqConfig.DELETE_COMMENT_QUEUE_NAME;
import static com.lt.comment.config.RabbitMqConfig.DELETE_COMMENT_ROUTING_KEY;

/**
 * @author Lhz
 */
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentDaoImpl commentDao;
    private final RabbitTemplate rabbitTemplate;
    private final RedisTemplate<String ,String > redisTemplate;
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public Comment findByCommentId(final String id){
        return commentDao.findByCommentId(id);
    }

    public void deleteComment(final String id) {
        rabbitTemplate.convertAndSend("comment.topic", DELETE_COMMENT_ROUTING_KEY, id);
    }

    public void insertComment(final String commentId, final String username, final String debateId, final String nickname, final String userImage, final String title, String content, String createTime) {
        commentDao.insertComment(commentId, username, debateId, nickname, userImage, title, content, createTime);
    }

    public List<Comment> getComments(final String debateId, final Integer pageSize, final Integer beginPage, final String username) {
        return commentDao.findAll(debateId, pageSize, beginPage).stream().peek(comment -> {
            String time = comment.getCreateTime();  //获取当前时间
            String date = format.format(Long.parseLong(time));
            comment.setCreateTime(date);
            comment.setIsLike(ObjectUtils.isEmpty(username) ?  "0" : ObjectUtils.isEmpty(redisTemplate.opsForValue().get(comment.getUsername()+comment.getCommentId())) ? "0" : "1");

        }).collect(Collectors.toList());
    }

    public long addLike(final String id) {
        return commentDao.addLike(id);
    }
    public long reduceLike(final String id) {
        return commentDao.reduceLike(id);
    }
}
