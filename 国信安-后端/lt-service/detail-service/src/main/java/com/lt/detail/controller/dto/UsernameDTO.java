package com.lt.detail.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;

/**
 * @author Lhz
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsernameDTO {
    /**
     * 用户名
     */
    @NotBlank(message = "username不能为空")
    @Size(min = 6, max = 6, message = "username格式错误")
    String username;
}
