package com.lt.auth.controller;

import com.lt.auth.service.MailService;
import com.lt.model.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import java.time.Duration;

/**
 * 邮件控制器
 *
 * @author Lhz
 * @date 2022/11/15
 */
@RestController
@CrossOrigin
@RequiredArgsConstructor
public class MailController {

    /**
     * 邮件服务
     */
    private final MailService mailService;
    /**
     * 复述,模板
     */
    private final RedisTemplate<String, String> redisTemplate;

    /**
     * 得到邮件
     *
     * @param email 电子邮件
     * @return {@link Response}<{@link String}>
     * @throws MessagingException 通讯异常
     */
    @GetMapping("/verification/mail/{email}")
    Response<String> getMail(@PathVariable("email") String email) throws MessagingException {
        String regex = "^[a-z0-9]+([._\\\\-]*[a-z0-9])*@([a-z0-9]+[-a-z0-9]*[a-z0-9]+.){1,63}[a-z0-9]+$";
        if (email.matches(regex)) {
            int temp = (int) ((Math.random() * 9 + 1) * 1000);
            String mail = String.valueOf(temp);
            redisTemplate.opsForValue().set(email + "verificationCode", mail, Duration.ofMinutes(9990));
            mailService.sendHTMLMail(email, mail, mail);
            return new Response<>(HttpStatus.OK.value(), "邮箱发送成功");
        } else {
            return new Response<>(HttpStatus.OK.value(), "邮箱格式错误");
        }
    }

}
