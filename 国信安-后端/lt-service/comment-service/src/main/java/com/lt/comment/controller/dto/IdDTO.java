package com.lt.comment.controller.dto;

import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;

/**
 * id dto
 *
 * @author Lhz
 * @date 2022/11/20
 */
@ToString
public class IdDTO {
    /**
     * id
     */
    @NotBlank(message = "评论id不能为空")
    @Size(min = 6, max = 20, message = "评论id格式错误")
    public String id;

    /**
     * 谁评论的
     */
    @NotBlank(message = "username不能为空")
    @Size(min = 6, max = 6, message = "username格式错误")
    public String username;
}
