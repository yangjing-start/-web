package com.lt.detail.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;

/**
 * 更新dto
 *
 * @author Lhz
 * @date 2022/11/16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateDTO {
    /**
     * 描述
     */
    @NotBlank(message = "描述不能为空")
    @Length(max = 20, message = "描述不能超过20个字符")
    public String description;
    /**
     * 价值
     */
    @NotBlank(message = "修改值不能为空")
    @Length(max = 20, message = "修改值不能超过20个字符")
    public String value;
    /**
     * 用户名
     */
    @NotBlank(message = "username不能为空")
    @Size(min = 6, max = 6, message = "username格式错误")
    public String username;
}