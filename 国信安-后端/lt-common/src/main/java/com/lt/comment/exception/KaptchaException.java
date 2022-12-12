package com.lt.comment.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @author Lhz
 * 自定义验证码异常
 */
public class KaptchaException extends AuthenticationException {

    public KaptchaException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public KaptchaException(String msg) {
        super(msg);
    }
}
