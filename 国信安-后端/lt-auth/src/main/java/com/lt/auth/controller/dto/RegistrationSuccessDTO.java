package com.lt.auth.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

/**
 * 注册成功dto
 *
 * @author Lhz
 * @date 2022/11/18
 */
@Data
@AllArgsConstructor
@ToString
public class RegistrationSuccessDTO {
    /**
     * 信息
     */
    String msg;
    /**
     * 用户名
     */
    String username;
}