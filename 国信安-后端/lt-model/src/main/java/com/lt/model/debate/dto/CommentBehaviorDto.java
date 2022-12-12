package com.lt.model.debate.dto;

import lombok.Data;

/**
 * @Author WanXin
 * @Date 2022/11/30
 */
@Data
public class CommentBehaviorDto {

    private String contentId;

    private Integer op;

    private Integer count;

    private String username;
}
