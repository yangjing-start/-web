package com.lt.comment.service;

import com.lt.comment.dao.impl.CommentDaoImpl;
import com.lt.comment.dao.impl.SubCommentDaoImpl;
import com.lt.comment.listener.dto.SubCommentDTO;
import com.lt.model.comment.SubComment;
import com.lt.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.lt.comment.config.RabbitMqConfig.DELETE_SUB_COMMENT_ROUTING_KEY;

/**
 * @author Lhz
 */
@RequiredArgsConstructor
@Service
public class SubCommentService {

    private final SubCommentDaoImpl subCommentDao;
    private final CommentDaoImpl commentDao;
    private final RabbitTemplate rabbitTemplate;
    private final RedisTemplate<String ,String > redisTemplate;
    ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
            2, 4,
            300,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(10),
            r -> new Thread(r, "mongoSubComment"),
            new ThreadPoolExecutor.CallerRunsPolicy());
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public List<SubComment> getSubComments(final String fId, final Integer pageSize, final Integer beginPage, final String username) {
        return subCommentDao.findAll(fId, pageSize, beginPage).stream().peek(subComment -> {
            String time = subComment.getCreateTime();  //获取当前时间
            String date = format.format(Long.valueOf(time));
            subComment.setCreateTime(date);
            subComment.setIsLike(ObjectUtils.isEmpty(username) ?  "0" : ObjectUtils.isEmpty(redisTemplate.opsForValue().get(subComment.getUsername()+subComment.getSubCommentId())) ? "0" : "1");
        }).collect(Collectors.toList());
    }

    public void deleteSubComment(final String fId, final String id) {
        final SubCommentDTO subCommentDTO = new SubCommentDTO();
        subCommentDTO.setId(id);
        subCommentDTO.setFid(fId);
        rabbitTemplate.convertAndSend("comment.topic", DELETE_SUB_COMMENT_ROUTING_KEY, Objects.requireNonNull(JsonUtils.toString(subCommentDTO)));
    }

    public void insertSubComment(final String fId, final String reply, final String content, final String nickname, final String userImage, final String username) {
        threadPoolExecutor.execute(() -> subCommentDao.insertSubComment(fId, reply, content, nickname, userImage, username));
        threadPoolExecutor.execute(() -> commentDao.addSubCommentCount(fId));
    }

    public SubComment findById(final String id){
        return subCommentDao.findBySubCommentId(id);
    }

    public long addLike(final String id) {
        return subCommentDao.addLike(id);
    }
    public long reduceLike(final String id) {
        return subCommentDao.reduceLike(id);
    }
}
