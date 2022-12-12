package com.lt.comment.controller;

import com.lt.comment.config.RsaKeyProperties;
import com.lt.comment.service.PostMessageService;
import com.lt.model.response.Response;
import com.lt.model.user.PostMessage;
import com.lt.model.user.User;
import com.lt.utils.JwtUtils;
import com.lt.utils.Payload;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.lt.utils.TokenUtils.CheckToken;


/**
 * @author Lhz
 */
@RestController
public final class PostMessageController {

    private final PostMessageService messageService;
    private final RsaKeyProperties prop;

    public PostMessageController(final PostMessageService messageService, @Qualifier("comment-RsaKey") final RsaKeyProperties prop) {
        this.messageService = messageService;
        this.prop = prop;
    }
    @GetMapping("/postMessage/{username}")
    @PreAuthorize("hasAuthority('normal')")
    public Response<List<PostMessage>> test(@PathVariable("username") final String username, @RequestHeader(value = "token") final String token) {
        if(CheckToken( username, token, prop.getPublicKey())){
            return new Response<>(500, "不合法的访问");
        }

        List<PostMessage> allMessage = messageService.getMessages(username);

        return new Response<>(200, allMessage);
    }

    @GetMapping("/deletePostMessage/{username}")
    @PreAuthorize("hasAuthority('normal')")
    public Response<List<PostMessage>> delete(@PathVariable("username") final String username, @RequestHeader(value = "token") final String token) {
        if(CheckToken( username, token, prop.getPublicKey())){
            return new Response<>(500, "不合法的访问");
        }

        messageService.deletePostMessage(username);
        return new Response<>(200, "OK");
    }
}
