package com.lt.auth.controller.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 注册dto
 *
 * @author Lhz
 * @date 2022/11/18
 */
@Data
public class RegistrationDTO {
    /**
     * 邮件
     */
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式错误")
    public String mail;
    /**
     * 昵称
     */
    @NotBlank(message = "昵称不能为空")
    @Length(max = 20, message = "用户名不能超过20个字符")
    @Pattern(regexp = "^[\\u4E00-\\u9FA5A-Za-z0-9\\*]*$", message = "用户昵称限制：最多20字符，包含文字、字母和数字")
    public String nickname;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Length(max = 20, message = "密码不能超过20个字符")
    public String password;
    /**
     * 验证
     */
    @NotBlank(message = "验证码不能为空")
    @Size(min = 4, max = 4, message = "验证码格式错误")
    public String verification;
}