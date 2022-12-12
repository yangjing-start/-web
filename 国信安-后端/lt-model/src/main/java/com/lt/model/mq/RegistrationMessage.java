package com.lt.model.mq;

import lombok.Data;

/**
 * @author Lhz
 */
@Data
public class RegistrationMessage {
    /**
     * 邮件
     */
    public String mail;
    /**
     * 昵称
     */
    public String nickname;

    /**
     * 账号
     */
    public String username;
}
