package com.lt.detail.controller.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;

/**
 * @author Lhz
 */
@Data
@ToString
@Builder
public class OtherDTO {
    /**
     * 用户名
     */
    @Size(min = 6, max = 6, message = "username格式错误")
    String selfUsername;

    @NotBlank(message = "otherUsername不能为空")
    @Size(min = 6, max = 6, message = "otherUsername格式错误")
    String otherUsername;
}
