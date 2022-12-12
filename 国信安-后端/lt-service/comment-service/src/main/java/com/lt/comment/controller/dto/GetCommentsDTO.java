package com.lt.comment.controller.dto;

import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 得到评论dto
 *
 * @author Lhz
 * @date 2022/11/20
 */
@ToString
public class GetCommentsDTO {
    /**
     * 辩论id
     */
    @NotBlank(message = "debateId不能为空")
    @Size(min = 0, max = 20, message = "debateId格式错误")
    public String debateId;
    /**
     * 页面大小
     */
    @NotNull(message = "pageSize不能为空")
    @Range(min = 0, max = 10, message = "pageSize不能多余10条")
    public Integer pageSize;
    /**
     * 开始
     */
    @NotNull(message = "begin不能为空")
    @Range(max = 100, message = "开始位不能超过100")
    public Integer begin;

    @Size(min = 6, max = 6, message = "userId格式错误")
    public String username;
}
