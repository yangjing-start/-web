package com.lt.model.debate.dto;

import lombok.Data;

/**
 * @Author WanXin
 * @Date 2022/11/29
 */
@Data
public class ContentVisitStreamMess {

    /**
     * 文章id
     */
    private Long contentId;
    /**
     * 阅读
     */
    private int view;

    /**
     * 评论
     */
    private int comment;
    /**
     * 点赞
     */
    private int like;
}
