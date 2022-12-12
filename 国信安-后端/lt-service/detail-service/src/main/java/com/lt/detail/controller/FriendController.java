package com.lt.detail.controller;

import com.lt.detail.config.RsaKeyProperties;
import com.lt.detail.controller.dto.UsernameDTO;
import com.lt.detail.service.FriendService;
import com.lt.model.response.Response;
import com.lt.model.user.User;
import com.lt.model.user.UserProduct;
import com.lt.utils.JwtUtils;
import com.lt.utils.Payload;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

import static com.lt.utils.TokenUtils.CheckToken;


/**
 * @author Lhz
 */
@RestController
@RequestMapping("userProduct")
public class FriendController {

    private final FriendService friendService;
    private final RsaKeyProperties prop;

    public FriendController(FriendService friendService, @Qualifier("detail-RsaKey") RsaKeyProperties prop) {
        this.friendService = friendService;
        this.prop = prop;
    }

    @PostMapping("/friends")
    @PreAuthorize("hasAuthority('normal')")
    public Response<List<UserProduct>> friends(@Validated @RequestBody UsernameDTO dto, @RequestHeader(value = "token") String token) {
        if(CheckToken(dto.getUsername(), token, prop.getPublicKey())){
            return new Response<>(500, "不合法的修改");
        }

        List<UserProduct> friends = friendService.findFriends(dto.getUsername());
        return new Response<>(200, friends);
    }

    @PostMapping("/fans")
    @PreAuthorize("hasAuthority('normal')")
    public Response<List<UserProduct>> fans(@Validated @RequestBody UsernameDTO dto) {

        List<UserProduct> friends = friendService.fans(dto.getUsername());
        return new Response<>(200, friends);
    }

    @PostMapping("/follows")
    @PreAuthorize("hasAuthority('normal')")
    public Response<List<UserProduct>> follows(@Validated @RequestBody UsernameDTO dto) {

        List<UserProduct> friends = friendService.follows(dto.getUsername());
        return new Response<>(200, friends);
    }
}
