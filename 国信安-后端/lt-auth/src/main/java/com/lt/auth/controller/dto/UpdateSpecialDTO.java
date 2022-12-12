package com.lt.auth.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;

/**
 * 更新密码dto
 *
 * @author Lhz
 * @date 2022/11/16
 */
@Data
@AllArgsConstructor
public class UpdateSpecialDTO {
    /**
     * 用户名
     */
    @NotBlank(message = "username不能为空")
    @Size(min = 6, max = 6, message = "username格式错误")
    public String username;
    @Size(min = 1, max = 1, message = "enabled格式错误")
    public Boolean enabled;
    @Size(min = 1, max = 1, message = "accountNonExpired格式错误")
    public Boolean accountNonExpired;
    @Size(min = 1, max = 1, message = "accountNonLocked格式错误")
    public Boolean accountNonLocked;
    @Size(min = 1, max = 1, message = "credentialsNonExpired格式错误")
    public Boolean credentialsNonExpired;
}
