package com.lt.auth.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 更新密码dto
 *
 * @author Lhz
 * @date 2022/11/16
 */
@Data
@AllArgsConstructor
public class UpdatePasswordDTO {
    /**
     * 用户名
     */
    @NotBlank(message = "username不能为空")
    @Size(min = 6, max = 6, message = "username格式错误")
    public String username;
    /**
     * 旧密码
     */
    @NotBlank(message = "密码不能为空")
    @Length(max = 20, message = "用户密码不能超过20个字符")
    @Pattern(regexp = "^[\\u4E00-\\u9FA5A-Za-z0-9\\*]*$", message = "用户密码限制：最多20字符，包含文字、字母和数字")
    public String oldPassword;

    /**
     * 新密码
     */
    @NotBlank(message = "密码不能为空")
    @Length(max = 20, message = "用户密码不能超过20个字符")
    @Pattern(regexp = "^[\\u4E00-\\u9FA5A-Za-z0-9\\*]*$", message = "用户密码限制：最多20字符，包含文字、字母和数字")
    public String newPassword;

}