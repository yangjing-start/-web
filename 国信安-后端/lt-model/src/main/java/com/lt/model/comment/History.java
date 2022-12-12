package com.lt.model.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * 辩论
 *
 * @author Lhz
 * @date 2022/11/18
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Document("history")
public class History {
    /**
     * 辩论id
     */
    @Field
    private String debateId;

    @Field
    private String username;

    @Field
    private String title;

    @Field
    private String debateNickname;

    @Field
    private String userImage;

    @Field
    private String createTime;

}
