package com.lt.comment.controller;

import com.lt.comment.config.RsaKeyProperties;
import com.lt.comment.controller.dto.GetCommentsDTO;
import com.lt.comment.controller.dto.IdDTO;
import com.lt.comment.controller.dto.InsertCommentDTO;
import com.lt.comment.service.CommentService;
import com.lt.model.comment.Comment;
import com.lt.model.response.Response;
import com.lt.model.user.User;
import com.lt.utils.JwtUtils;
import com.lt.utils.Payload;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

import static com.lt.utils.TokenUtils.CheckToken;

/**
 * 评论控制器
 *
 * @author Lhz
 * @date 2022/11/20
 */
@RestController
public class CommentController {

    private final CommentService commentService;
    private final RsaKeyProperties prop;
    private final RedisTemplate<String , String> redisTemplate;
    private static final String TOKEN_KEY = "token";
    public CommentController(final CommentService commentService, final @Qualifier("comment-RsaKey") RsaKeyProperties prop, final RedisTemplate<String , String> redisTemplate) {
        this.commentService = commentService;
        this.prop = prop;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 得到评论
     *
     * @param dto   dto
     * @return {@link Response}<{@link List}<{@link Comment}>>
     */
    @PostMapping("/getComments")
    Response<List<Comment>> getComments(@Validated @RequestBody final GetCommentsDTO dto) {

        final List<Comment> res = commentService.getComments(dto.debateId, dto.pageSize, dto.begin, dto.username);
        return new Response<>(200, res);

    }

    /**
     * 点赞
     *
     * @param dto   dto
     * @param token 令牌
     * @return {@link Response}<{@link String}>
     */
    @PreAuthorize("hasAuthority('normal')")
    @PostMapping("/addLikeComment")
    Response<String> addLike(@Validated @RequestBody final IdDTO dto, @RequestHeader(value = TOKEN_KEY) final String token) {
        if(CheckToken(dto.username, token, prop.getPublicKey())){
            return new Response<>(500, "不合法的修改");
        }

        if(!ObjectUtils.isEmpty(redisTemplate.opsForValue().get(dto.username + dto.id))){
            return new Response<>(500, "不能重复点赞");
        }
        redisTemplate.opsForValue().set(dto.username + dto.id, "1");


        long res = commentService.addLike(dto.id);
        return res == 1 ? new Response<>(200, "点赞成功") : new Response<>(500, "点赞失败");
    }

    /**
     * 取消点赞
     *
     * @param dto   dto
     * @param token 令牌
     * @return {@link Response}<{@link String}>
     */
    @PreAuthorize("hasAuthority('normal')")
    @PostMapping("/reduceLikeComment")
    Response<String> cancelLike(@Validated @RequestBody final IdDTO dto, @RequestHeader(value = TOKEN_KEY) final String token) {
        if(CheckToken(dto.username, token, prop.getPublicKey())){
            return new Response<>(500, "不合法的修改");
        }

        if(!ObjectUtils.isEmpty(redisTemplate.opsForValue().get(dto.username + dto.id))){
            redisTemplate.delete(dto.username + dto.id);
            commentService.reduceLike(dto.id);
            return new Response<>(200, "取消点赞成功");
        }else{
            return new Response<>(500, "取消点赞失败");
        }
    }

    /**
     * 插入评论
     *
     * @param dto   dto
     * @param token 令牌
     * @return {@link Response}<{@link String}>
     */
    @PostMapping("/insertComment")
    @PreAuthorize("hasAuthority('normal')")
    Response<String> insertComment(@Validated @RequestBody final InsertCommentDTO dto, @RequestHeader(value = TOKEN_KEY) final String token) {
        if(CheckToken(dto.username, token, prop.getPublicKey())){
            return new Response<>(500, "不合法的修改");
        }

        commentService.insertComment(dto.commentId, dto.username, dto.debateId, dto.nickname, dto.userImage, dto.title, dto.content, dto.createTime);
        return new Response<>(200, "评论成功");
    }

    /**
     * 删除评论
     *
     * @param dto   dto
     * @param token 令牌
     * @return {@link Response}<{@link String}>
     */
    @PostMapping("/deleteComment")
    Response<String> deleteComment(@Validated @RequestBody final IdDTO dto, @RequestHeader(value = TOKEN_KEY) final String token) {
        if(CheckToken(dto.username, token, prop.getPublicKey())){
            return new Response<>(500, "不合法的修改");
        }

        String username = commentService.findByCommentId(dto.id).getUsername();
        if(!Objects.equals(username, dto.username)){
            return new Response<>(500, "不能删除别人评论");
        }

        commentService.deleteComment(dto.id);
        return new Response<>(200, "删除成功");
    }



}
