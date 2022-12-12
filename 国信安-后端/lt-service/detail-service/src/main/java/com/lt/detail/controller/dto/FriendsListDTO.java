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
public class FriendsListDTO {
    /**
     * 自己用户名
     */
    @NotBlank(message = "selfUsername不能为空")
    @Size(min = 6, max = 6, message = "selfUsername格式错误")
    String nickname;

    @NotBlank(message = "简介不能为空")
    @Size(max = 200, message = "简介太长")
    String synopsis;

    @NotBlank(message = "image不能为空")
    @Size(max = 200, message = "简介太长")
    String image;
}
