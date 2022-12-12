package com.lt.comment.controller.dto;

import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;

/**
 * 删除dto
 *
 * @author Lhz
 * @date 2022/11/20
 */
@ToString
public class DeleteSubCommentDTO {
    /**
     * id
     */
    @NotBlank(message = "id不能为空")
    @Size(min = 6, max = 20, message = "id格式错误")
    public String id;
    /**
     * f id
     */
    @NotBlank(message = "fId不能为空")
    @Size(min = 6, max = 20, message = "fId格式错误")
    public String fId;

    /**
     * 谁评论的
     */
    @NotBlank(message = "username不能为空")
    @Size(min = 6, max = 6, message = "username格式错误")
    public String username;
}
