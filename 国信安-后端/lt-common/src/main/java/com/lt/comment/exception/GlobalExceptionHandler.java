package com.lt.comment.exception;

import com.lt.model.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.util.Objects;

/**
 * @author Lhz
 */

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final int DUPLICATE_KEY_CODE = 1001;
    private static final int PARAM_FAIL_CODE = 1002;
    private static final int VALIDATION_CODE = 1003;

    /**
     * 方法参数校验
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Response<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return new Response<>(PARAM_FAIL_CODE, Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage());
    }

    /**
     * ValidationException
     */
    @ExceptionHandler(ValidationException.class)
    public Response<String> handleValidationException(ValidationException e) {
        return new Response<>(VALIDATION_CODE, e.getCause().getMessage());
    }

    /**
     * ConstraintViolationException
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public Response<String> handleConstraintViolationException(ConstraintViolationException e) {
        return new Response<>(PARAM_FAIL_CODE, e.getMessage());
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public Response<String> handlerNoFoundException(Exception e) {
        logger.error(e.getMessage(), e);
        return new Response<>(404, "路径不存在，请检查路径是否正确");
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public Response<String> handleDuplicateKeyException(DuplicateKeyException e) {
        logger.error(e.getMessage(), e);
        return new Response<>(DUPLICATE_KEY_CODE, "数据重复，请检查后提交");
    }


    @ExceptionHandler(Exception.class)
    public Response<String> handleException(Exception e) {
        logger.error(e.getMessage(), e);
        return new Response<>(500, "系统繁忙,请稍后再试");
    }

    /**
     * KaptchaException
     */
    @ExceptionHandler(KaptchaException.class)
    public Response<String> handleKaptchaException(Exception e) {
        logger.error(e.getMessage(), e);
        return new Response<>(500, "验证码不匹配");
    }

    /**
     * AuthenticationException
     */
    @ExceptionHandler(AuthenticationException.class)
    public Response<String> handleAuthenticationException(Exception e) {
        logger.error(e.getMessage(), e);
        return new Response<>(500, e.getCause().getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public Response<String> handleAccessDeniedException(Exception e) {
        logger.error(e.getMessage(), e);
        return new Response<>(500, e.getCause().getMessage());
    }

}
