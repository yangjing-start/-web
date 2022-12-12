package com.lt.auth.controller;

import com.alibaba.nacos.common.utils.Pair;
import com.lt.auth.config.AuthRsaKeyProperties;
import com.lt.auth.controller.dto.*;
import com.lt.auth.service.UserDetailServiceImpl;
import com.lt.model.response.Response;
import com.lt.model.user.User;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Objects;

import static com.lt.utils.TokenUtils.CheckToken;


/**
 * 用户控制器
 *
 * @author Lhz
 * @date 2022/11/16
 */
@RestController
@CrossOrigin
public class UserController {
    /**
     * 用户详细服务impl
     */
    private final UserDetailServiceImpl userDetailServiceImpl;
    /**
     * redis
     */
    private final RedisTemplate<String, String> redisTemplate;

    private final AuthRsaKeyProperties prop;

    public UserController(UserDetailServiceImpl userDetailServiceImpl, RedisTemplate<String, String> redisTemplate,@Qualifier("AuthRsaKey") AuthRsaKeyProperties authRsaKeyProperties) {
        this.userDetailServiceImpl = userDetailServiceImpl;
        this.redisTemplate = redisTemplate;
        this.prop = authRsaKeyProperties;
    }

    /**
     * 登记
     *
     * @param data 数据
     * @return {@link Response}<{@link RegistrationSuccessDTO}>
     */
    @PostMapping("/register")
    Response<RegistrationSuccessDTO> registration(@Validated @RequestBody RegistrationDTO data) throws InterruptedException {
        String mail = data.getMail();
        String verification = data.getVerification();
        String realVerification = redisTemplate.opsForValue().get(mail + "verificationCode");
        if (!ObjectUtils.isEmpty(mail) && !ObjectUtils.isEmpty(realVerification) && realVerification.equalsIgnoreCase(verification)) {
            Pair<Boolean, String> res = userDetailServiceImpl.registration(data);
            if (!res.getFirst()) {
                return new Response<>(HttpStatus.CREATED.value(), "该邮箱已被注册");
            } else {
                return new Response<>(HttpStatus.OK.value(), new RegistrationSuccessDTO("注册成功", res.getSecond()));
            }
        } else {
            if (ObjectUtils.isEmpty(realVerification)) {
                return new Response<>(HttpStatus.OK.value(), "您还未发送验证码");
            }
            return new Response<>(HttpStatus.OK.value(), "验证码错误");
        }
    }

    /**
     * 更新密码
     *
     * @param data 数据
     * @return {@link Response}<{@link String}>
     */
    @PreAuthorize("hasAuthority('normal')")
    @PostMapping("/updatePassword")
    Response<String> updatePassword(@Validated @RequestBody UpdatePasswordDTO data, @RequestHeader(value = "token") String token) {
        if(CheckToken(data.username, token, prop.getPublicKey())){
            return new Response<>(500, "不合法的修改");
        }

        String newPassword = data.newPassword;
        String oldPassword = data.oldPassword;
        userDetailServiceImpl.changePassword(oldPassword, newPassword);
        return new Response<>(200, "更新成功");
    }

    /**
     * 注销用户
     *
     * @param data
     * @param token
     * @return
     */
    @PreAuthorize("hasAuthority('normal')")
    @PostMapping("/deleteUser")
    Response<String> deleteUser(@Validated @RequestBody UsernameDTO data, @RequestHeader(value = "token") String token) {
        if(CheckToken(data.username, token, prop.getPublicKey())){
            return new Response<>(500, "不合法的修改");
        }
        redisTemplate.delete(data.username+"token");
        userDetailServiceImpl.deleteUser(data.username);
        return new Response<>(200, "注销成功");
    }

    /**
     * 检测用户是否存在
     *
     * @param data
     * @param token
     * @return
     */
    @PreAuthorize("hasAuthority('normal')")
    @PostMapping("/findUser")
    Response<String> findUser(@Validated @RequestBody FindUserDTO data, @RequestHeader(value = "token") String token) {
        if(CheckToken(data.username, token, prop.getPublicKey())){
            return new Response<>(500, "不合法的修改");
        }
        if (userDetailServiceImpl.userExists(data.otherUsername)) {
            return new Response<>(200, "用户存在");
        } else {
            return new Response<>(200, "用户不存在");
        }
    }


    @PreAuthorize("hasAuthority('normal')")
    @PostMapping("/updateUserSpecial")
    Response<String> updateUserSpecial(@Validated @RequestBody UpdateSpecialDTO data, @RequestHeader(value = "token") String token) throws IOException {
        if(CheckToken(data.username, token, prop.getPublicKey())){
            return new Response<>(500, "不合法的修改");
        }

        User user = new User();
        if (data.accountNonExpired != null) {
            user.setAccountNonExpired(false);
        }
        if (data.credentialsNonExpired != null) {
            user.setCredentialsNonExpired(false);
        }
        if (data.enabled != null) {
            user.setEnabled(false);
        }
        if (data.accountNonLocked != null) {
            user.setAccountNonLocked(false);
        }
        int res = userDetailServiceImpl.updateUser(user);
        if (Objects.equals(res, 0)) {
            return new Response<>(500, "更新失败");
        } else {
            return new Response<>(200, "更新成功");
        }
    }


}
