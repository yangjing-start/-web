package com.lt.comment.controller.dto;

import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;

/**
 * 插入子评论dto
 *
 * @author Lhz
 * @date 2022/11/20
 */
@ToString
public class InsertSubCommentDTO {

    /**
     * f id
     */
    @NotBlank(message = "fId不能为空")
    @Size(min = 6, max = 20, message = "fId格式错误")
    public String fId;

    /**
     * username
     */
    @NotBlank(message = "username不能为空")
    @Size(min = 6, max = 6, message = "username格式错误")
    public String username;

    /**
     * 昵称
     */
    @NotBlank(message = "昵称不能为空")
    @Length(max = 100, message = "昵称不合法")
    public String nickname;

    /**
     * 用户头像
     */
    @NotBlank(message = "userImage不能为空")
    @Length(max = 300, message = "userImage不合法")
    public String userImage;

    /**
     * 回复
     */
    @Length(max = 333, message = "reply过长")
    public String reply;

    /**
     * 内容
     */
    @NotBlank(message = "内容不能为空")
    @Length(max = 333, message = "内容过长")
    public String content;

    /**
     * 创建时间
     */
    @NotBlank(message = "创建时间不能为空")
    @Length(max = 100, message = "时间不合法")
    public String createTime;
}