package com.lt.model.user;

import lombok.*;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * @author Lhz
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldNameConstants
@Document("message")
public class PostMessage {
    /**
     * 自己的Id
     */
    @Field
    public String selfId;
    /**

     * 2: 被关注通知
     */
    @Field
    public Integer type;
    /**
     * 粉丝昵称
     */
    @Field
    public String nickName;
    /**
     * 粉丝头像
     */
    @Field
    public String image;
    /**
     * 粉丝Id
     */
    @Field
    public String otherId;
    /**
     * 粉丝留言
     */
    @Field
    public String message;
    /**
     * 消息发送时间
     */
    @Field
    public String time;
}
