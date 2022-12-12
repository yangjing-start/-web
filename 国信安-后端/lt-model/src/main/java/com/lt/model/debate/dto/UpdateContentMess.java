package com.lt.model.debate.dto;

import lombok.Data;

/**
 * @Author WanXin
 * @Date 2022/11/27
 */
@Data
public class UpdateContentMess {
    /**
     * 修改文章的字段类型
     */
    private UpdateContentType type;
    /**
     * 文章ID
     */
    private Long contentId;
    /**
     * 修改数据的增量，可为正负
     */
    private Integer add;

    public enum UpdateContentType{
        COMMENT,LIKES,VIEWS;
    }
}
