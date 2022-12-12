package com.lt.auth.filter;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.lt.comment.exception.KaptchaException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * @author Lhz
 */
@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    /**
     * @param FROM_KAPTCHA_KEY 写了默认的好处是如果不通过setter方法传值就选默认
     */
    public static final String FROM_KAPTCHA_KEY = "imageVerify";

    public static final String FROM_TIME_KEY = "time";

    public static final String REQUEST_METHOD = "POST";

    private String kaptchaParameter = FROM_KAPTCHA_KEY;

    public String getTimeParameter() {
        return FROM_TIME_KEY;
    }

    public String getKaptchaParameter() {
        return kaptchaParameter;
    }

    public void setKaptchaParameter(String kaptchaParameter) {
        this.kaptchaParameter = kaptchaParameter;
    }

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (!REQUEST_METHOD.equals(request.getMethod())) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        } else {
            //获取请求中的验证码
            try {
                Map<String, String> userInfo = new ObjectMapper().readValue(request.getInputStream(), Map.class);
                final String kaptcha = userInfo.get(getKaptchaParameter());
                final String usernameOrEmail = userInfo.get(getUsernameParameter());
                final String password = userInfo.get(getPasswordParameter());
                final String time = userInfo.get(getTimeParameter().toString()).toString();
                //获取redis中的验证码
                final String realVerify = redisTemplate.opsForValue().get(time);
                if (!ObjectUtils.isEmpty(kaptcha) && !ObjectUtils.isEmpty(realVerify) && kaptcha.equalsIgnoreCase(realVerify)) {
                    //认证密码
                    UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(usernameOrEmail, password);
                    setDetails(request, authRequest);
                    return this.getAuthenticationManager().authenticate(authRequest);
                } else {
                    if (ObjectUtils.isEmpty(realVerify)) {
                        throw new KaptchaException("未发送验证码");
                    } else {
                        throw new KaptchaException("验证码不匹配");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            throw new KaptchaException("验证码不匹配");
        }
    }
}