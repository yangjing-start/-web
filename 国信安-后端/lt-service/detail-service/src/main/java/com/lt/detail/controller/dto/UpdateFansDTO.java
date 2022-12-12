package com.lt.detail.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 更新粉丝dto
 *
 * @author Lhz
 * @date 2022/11/18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateFansDTO {

    /**
     * 自己用户名
     */
    @NotBlank(message = "selfUsername不能为空")
    @Size(min = 6, max = 6, message = "selfUsername格式错误")
    public String username;
    /**
     * 更新的用户名
     */
    @NotBlank(message = "updateUsername不能为空")
    @Size(min = 6, max = 6, message = "updateUsername格式错误")
    public String updateUsername;
    /**
     * 昵称
     */
    @NotBlank(message = "昵称不能为空")
    @Length(max = 20, message = "用户名不能超过20个字符")
    @Pattern(regexp = "^[\\u4E00-\\u9FA5A-Za-z0-9\\*]*$", message = "用户昵称限制：最多20字符，包含文字、字母和数字")
    public String nickname;
    /**
     * 头像
     */
    @NotBlank(message = "头像路径不能为空")
    @Length(max = 200, message = "头像路径不能超过200个字符")
    public String userImage;
}