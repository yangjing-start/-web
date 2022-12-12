package com.lt.comment.controller;

import com.lt.comment.config.RsaKeyProperties;
import com.lt.comment.controller.dto.DeleteSubCommentDTO;
import com.lt.comment.controller.dto.GetSubCommentDTO;
import com.lt.comment.controller.dto.IdDTO;
import com.lt.comment.controller.dto.InsertSubCommentDTO;
import com.lt.comment.controller.vo.GetSubCommentVO;
import com.lt.comment.service.CommentService;
import com.lt.comment.service.SubCommentService;
import com.lt.model.comment.SubComment;
import com.lt.model.response.Response;
import com.lt.model.user.User;
import com.lt.utils.JwtUtils;
import com.lt.utils.Payload;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

import static com.lt.utils.TokenUtils.CheckToken;

/**
 * 子评论控制器
 *
 * @author Lhz
 * @date 2022/11/20
 */
@RestController
public final class SubCommentController {

    /**
     * 子评论服务
     */
    private final SubCommentService subCommentService;
    private final CommentService commentService;
    private final RedisTemplate<String , String> redisTemplate;
    private final RsaKeyProperties prop;
    private static final String TOKEN_KEY = "token";
    public SubCommentController(final SubCommentService subCommentService, final CommentService commentService,
                                final RedisTemplate<String, String> redisTemplate, @Qualifier("comment-RsaKey") final RsaKeyProperties prop) {
        this.subCommentService = subCommentService;
        this.commentService = commentService;
        this.redisTemplate = redisTemplate;
        this.prop = prop;
    }

    /**
     * 得到子评论
     *
     * @param dto   dto
     * @return {@link Response}<{@link List}<{@link SubComment}>>
     */
    @PostMapping("/getSubComments")
    Response<GetSubCommentVO> getSubComment(@RequestBody @Validated final GetSubCommentDTO dto) {

        final List<SubComment> res = subCommentService.getSubComments(dto.fId, dto.pageSize, dto.begin, dto.username);
        GetSubCommentVO getSubCommentVO = new GetSubCommentVO(dto.fId, res);
        return new Response<>(200, getSubCommentVO);
    }

    /**
     * 删除子评论
     *
     * @param dto   dto
     * @param token 令牌
     * @return {@link Response}<{@link String}>
     */
    @PostMapping("/deleteSubComment")
    Response<String> deleteSubComment(@RequestBody @Validated final DeleteSubCommentDTO dto, @RequestHeader(value = TOKEN_KEY) final String token) {
        if(CheckToken(dto.username, token, prop.getPublicKey())){
            return new Response<>(500, "不合法的删除");
        }

        final SubComment subComment = subCommentService.findById(dto.id);
        if(!Objects.equals(subComment.getUsername(), dto.username)){
            return new Response<>(500, "不能删除别人的评论");
        }

        subCommentService.deleteSubComment(dto.fId, dto.id);
        return new Response<>(200, "删除成功");
    }

    /**
     * 点赞
     *
     * @param dto   dto
     * @param token 令牌
     * @return {@link Response}<{@link String}>
     */
    @PostMapping("/addLikeSubComment")
    Response<String> addLike(@RequestBody @Validated final IdDTO dto, @RequestHeader(value = TOKEN_KEY) final String token) {
        if(CheckToken(dto.username, token, prop.getPublicKey())){
            return new Response<>(500, "不合法的点赞");
        }

        if(!ObjectUtils.isEmpty(redisTemplate.opsForValue().get(dto.username + dto.id))){
            return new Response<>(500, "不能重复点赞");
        }
        redisTemplate.opsForValue().set(dto.username + dto.id, "1");

        subCommentService.addLike(dto.id);
        return new Response<>(200, "点赞成功");
    }
    /**
     * 取消点赞
     *
     * @param dto   dto
     * @param token 令牌
     * @return {@link Response}<{@link String}>
     */
    @PreAuthorize("hasAuthority('normal')")
    @PostMapping("/reduceLikeSubComment")
    Response<String> cancelLike(@Validated @RequestBody final IdDTO dto, @RequestHeader(value = TOKEN_KEY) final String token) {
        if(CheckToken(dto.username, token, prop.getPublicKey())){
            return new Response<>(500, "不合法的取消点赞");
        }

        if(!ObjectUtils.isEmpty(redisTemplate.opsForValue().get(dto.username + dto.id))){
            redisTemplate.delete(dto.username + dto.id);
            subCommentService.reduceLike(dto.id);
            return new Response<>(200, "取消点赞成功");
        }else{
            return new Response<>(500, "取消点赞失败");
        }
    }
    /**
     * 插入子评论
     *
     * @param dto   dto
     * @param token 令牌
     * @return {@link Response}<{@link String}>
     */
    @PostMapping("/insertSubComment")
    Response<String> insertSubComment(@RequestBody @Validated final InsertSubCommentDTO dto, @RequestHeader(value = TOKEN_KEY) final String token) {
        if(CheckToken(dto.username, token, prop.getPublicKey())){
            return new Response<>(500, "不合法的评论");
        }

        if(ObjectUtils.isEmpty(commentService.findByCommentId(dto.fId))){
            return new Response<>(500, "父级评论不存在");
        }
        subCommentService.insertSubComment(dto.fId, dto.reply, dto.content, dto.nickname, dto.userImage, dto.username);
        return new Response<>(200, "评论成功");
    }

}
