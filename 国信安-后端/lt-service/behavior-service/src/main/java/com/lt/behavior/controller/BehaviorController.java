package com.lt.behavior.controller;

import com.lt.behavior.config.RsaKeyProperties;
import com.lt.behavior.service.BehaviorService;
import com.lt.model.debate.dto.CommentBehaviorDto;
import com.lt.model.debate.dto.ReadBehaviorDto;
import com.lt.model.response.Response;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.lt.utils.TokenUtils.CheckToken;

/**
 * @Author WanXin
 * @Date 2022/11/27
 */
@RestController
@CrossOrigin
@RequestMapping("/behavior")
@RequiredArgsConstructor
public class BehaviorController {

    private final BehaviorService behaviorService;
    private final RsaKeyProperties prop;

    @PreAuthorize("hasAuthority('normal')")
    @PostMapping("/like")
    public Response like(@RequestBody Map map, @RequestHeader(value = "token") final String token) {
        if(CheckToken(map.get("username").toString(), token, prop.getPublicKey())){
            return new Response<>(500, "不合法的修改");
        }
        return behaviorService.like(map);
    }

    @PostMapping("/read")
    public Response read(@RequestBody ReadBehaviorDto dto) {
        return behaviorService.read(dto);
    }

    @PreAuthorize("hasAuthority('normal')")
    @PostMapping("/comment")
    public Response comment(@RequestBody CommentBehaviorDto dto, @RequestHeader(value = "token") final String token) {
        if(CheckToken(dto.getUsername(), token, prop.getPublicKey())){
            return new Response<>(500, "不合法的修改");
        }
        return behaviorService.comment(dto);
    }

}
