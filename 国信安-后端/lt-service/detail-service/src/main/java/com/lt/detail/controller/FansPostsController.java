package com.lt.detail.controller;

import com.lt.detail.config.RsaKeyProperties;
import com.lt.detail.controller.dto.UpdateFansDTO;
import com.lt.detail.service.FansPostsService;
import com.lt.model.response.Response;
import com.lt.model.user.User;
import com.lt.utils.JwtUtils;
import com.lt.utils.Payload;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

import static com.lt.utils.TokenUtils.CheckToken;

/**
 * @author Lhz
 */
@RestController
@RequestMapping("userProduct")
public class FansPostsController {

    private final FansPostsService fansPostsService;
    private final RsaKeyProperties prop;

    public FansPostsController(FansPostsService fansPostsService,@Qualifier("detail-RsaKey") RsaKeyProperties prop) {
        this.fansPostsService = fansPostsService;
        this.prop = prop;
    }

    /**
     * 更新粉丝
     *
     * @param data 数据
     * @return {@link Response}<{@link String}>
     */
    @PreAuthorize("hasAuthority('normal') and #data.updateUsername!=#data.username")
    @PostMapping("/updateFans")
    Response<String> updateFans(@Validated @RequestBody UpdateFansDTO data, @RequestHeader(value = "token") String token) {
        if(CheckToken(data.getUsername(), token, prop.getPublicKey())){
            return new Response<>(500, "不合法的修改");
        }
        if(Objects.equals(data.getUsername(), data.getUpdateUsername())){
            return new Response<>(500, "不能关注自己");
        }
        if (!fansPostsService.judgePost(data.username, data.updateUsername)) {
            int res = fansPostsService.updateFans(data.updateUsername, data.username, data.nickname, data.userImage);
            if (Objects.equals(res, 0)) {
                return new Response<>(500, "更新失败");
            } else {
                return new Response<>(200, "更新成功");
            }
        } else {
            return new Response<>(500, "不能重复关注");
        }
    }
    /**
     * 更新粉丝
     *
     * @param data 数据
     * @return {@link Response}<{@link String}>
     */
    @PreAuthorize("hasAuthority('normal') and #data.updateUsername!=#data.username")
    @PostMapping("/reduceFans")
    Response<String> reduceFans(@Validated @RequestBody UpdateFansDTO data, @RequestHeader(value = "token") String token) {
        if(CheckToken(data.getUsername(), token, prop.getPublicKey())){
            return new Response<>(500, "不合法的修改");
        }
        if(Objects.equals(data.getUsername(), data.getUpdateUsername())){
            return new Response<>(500, "不能关注自己");
        }
        if (fansPostsService.judgePost(data.username, data.updateUsername)) {
            int res = fansPostsService.reduceFans(data.updateUsername, data.username);
            if (Objects.equals(res, 0)) {
                return new Response<>(500, "更新失败");
            } else {
                return new Response<>(200, "更新成功");
            }
        } else {
            return new Response<>(500, "非法取消关注");
        }
    }
}
