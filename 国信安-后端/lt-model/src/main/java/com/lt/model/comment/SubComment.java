package com.lt.model.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * 子评论
 *
 * @author Lhz
 * @date 2022/11/18
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
@Document("subcomment")
public class SubComment {
    /**
     * 该条评论id
     */
    @Id
    private String subCommentId;
    /**
     * 父评论 id
     */
    @Field
    private String fId;
    /**
     * 用户昵称
     */
    @Field
    private String nickname;
    /**
     * 用户头像
     */
    @Field
    private String userImage;
    /**
     * 该评论内容
     */
    @Field
    private String content;
    /**
     * 回复的对象的昵称
     */
    @Field
    private String to;
    /**
     * 评论时间
     */
    @Field
    private String createTime;
    /**
     * 点赞数
     */
    @Field
    private Long likes;
    /**
     * 谁评论的
     */
    @Field
    private String username;

    /**
     * 自己是否点过赞
     */
    private String isLike;

    /**
     * 前端状态
     */
    private boolean state;
}
