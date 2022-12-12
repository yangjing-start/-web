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
 * 评论
 *
 * @author Lhz
 * @date 2022/11/18
 */
//{
//        "commentId": "test_c63b8515c007",
//        "fUsername": "test_ad8a029333f5",
//        "createTime": "2017-06-30 15:14:09",
//        "content": "test_f1a4fb6322ac",
//        "likes": 83,
//        "subComments": 95
//        }
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
@Document("comment")
public class Comment {
    /**
     * 该条评论id
     */
    @Id
    private String commentId;
    /**
     * debateId
     */
    @Field
    private String debateId;
    /**
     * 评论的发送者
     */
    @Field
    private String nickname;
    /**
     * 创建时间
     */
    @Field
    private String createTime;
    /**
     * 评论内容
     */
    @Field
    private String content;
    /**
     * 喜欢数量
     */
    @Field
    private Long likes;
    /**
     * 子评论数量
     */
    @Field
    private Long subComments;

    /**
     * 谁评论的
     */
    @Field
    private String username;

    /**
     * 用户头像
     */
    @Field
    private String userImage;

    /**
     * 自己是否点过赞
     */
    private String isLike;

    /**
     * 前端状态
     */
    private boolean state;
}
