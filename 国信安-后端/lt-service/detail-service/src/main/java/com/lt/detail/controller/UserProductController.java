package com.lt.detail.controller;

import com.lt.detail.config.RsaKeyProperties;
import com.lt.detail.controller.dto.OtherDTO;
import com.lt.detail.controller.vo.OtherPageVO;
import com.lt.detail.controller.dto.UpdateDTO;
import com.lt.detail.controller.dto.UsernameDTO;
import com.lt.detail.service.FansPostsService;
import com.lt.detail.service.UserProductService;
import com.lt.model.response.Response;
import com.lt.model.user.UserProduct;
import com.lt.utils.OssTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

import static com.lt.utils.TokenUtils.CheckToken;


/**
 * @author Lhz
 */
@RestController
@RequestMapping("userProduct")
public class UserProductController {

    private final UserProductService userProductService;
    private final FansPostsService fansPostsService;
    private final OssTemplate ossTemplate;
    private final RsaKeyProperties prop;
    public UserProductController(UserProductService userProductService, FansPostsService fansPostsService, OssTemplate ossTemplate, @Qualifier("detail-RsaKey") RsaKeyProperties prop) {
        this.userProductService = userProductService;
        this.fansPostsService = fansPostsService;
        this.ossTemplate = ossTemplate;
        this.prop = prop;
    }

    @PreAuthorize("hasAuthority('normal')")
    @PostMapping("/getUserDetail")
    Response<UserProduct> getUserDetail(@Validated @RequestBody UsernameDTO dto, @RequestHeader(value = "token") String token) throws IOException {
        if(CheckToken(dto.getUsername(), token, prop.getPublicKey())){
            return new Response<>(500, "不合法的修改");
        }

        UserProduct res = userProductService.getUserDetail(dto.getUsername());
        return new Response<>(200, res);

    }

    /**
     * 更新
     *
     * @param data  数据
     * @param token 令牌
     * @return {@link Response}<{@link String}>
     */
    @PreAuthorize("hasAuthority('normal')")
    @PostMapping("/update")
    Response<String> update(@Validated @RequestBody UpdateDTO data, @RequestHeader(value = "token") String token) {
        if(CheckToken(data.getUsername(), token, prop.getPublicKey())){
            return new Response<>(500, "不合法的修改");
        }

        int res = userProductService.update(data.description, data.value, data.username);

        if (Objects.equals(res, 3)) {
            return new Response<>(500, "电话号码格式不正确");
        } else if (Objects.equals(res, 2)) {
            return new Response<>(200, "更新成功");
        } else if (Objects.equals(res, 4)) {
            return new Response<>(500, "未知修改");
        } else {
            return new Response<>(200, "更新失败");
        }
    }

    /**
     * 更新用户图像
     *
     * @param username 用户名
     * @param file     文件
     * @param token    令牌
     * @return {@link Response}<{@link String}>
     * @throws IOException ioexception
     */
    @PreAuthorize("hasAuthority('normal')")
    @PostMapping("/updateUserImage/{username}")
    Response<String> updateUserImage(@PathVariable("username") String username, MultipartFile file, @RequestHeader(value = "token") String token) throws IOException {
        if(CheckToken(username, token, prop.getPublicKey())){
            return new Response<>(500, "不合法的修改");
        }

        if (username.length() != 6) {
            return new Response<>(500, "用户名不合法");
        }
        ossTemplate.delete(username + "userImage");
        String url = ossTemplate.upload(username + "userImage", file.getInputStream());
        System.out.println(url);
        int res = userProductService.updateUserImage(url, username);
        if (Objects.equals(res, 0)) {
            return new Response<>(500, "更新失败");
        } else {
            return new Response<>(200, url);
        }
    }

    @PostMapping("/getOtherDetail")
    Response<OtherPageVO> getOtherDetail(@Validated @RequestBody OtherDTO dto){
        Boolean followed = false;
        if(!ObjectUtils.isEmpty(dto.getSelfUsername())){
            followed = fansPostsService.judgePost(dto.getSelfUsername(), dto.getOtherUsername());
        }
        UserProduct res = userProductService.getUserDetail(dto.getOtherUsername());
        if(ObjectUtils.isEmpty(res)){
            return new Response<>(200, "无此人，非法访问");
        }
        OtherPageVO otherPageDTO = OtherPageVO.builder()
                .username(res.username)
                .posts(res.posts).fans(res.fans)
                .follows(res.follows).userImage(res.userImage).nickname(res.nickname).synopsis(res.synopsis).followed(followed).build();
        return new Response<>(200, otherPageDTO);

    }
    /**
     * 更新用户帖子数量
     *
     * @param username 用户名
     * @param token    令牌
     * @return {@link Response}<{@link String}>
     * @throws IOException ioexception
     */
    @PreAuthorize("hasAuthority('normal')")
    @PostMapping("/updateUserPosts/{username}")
    Response<String> updateUserPosts(@PathVariable("username") String username, @RequestHeader(value = "token") String token) throws IOException {
        if(CheckToken(username, token, prop.getPublicKey())){
            return new Response<>(500, "不合法的修改");
        }

        if (username.length() != 6) {
            return new Response<>(500, "用户名不合法");
        }
        int res = userProductService.updateUserPosts(username);
        if (res == 1) {
            return new Response<>(500, "更新失败");
        } else {
            return new Response<>(200, "更新成功");
        }
    }

}
