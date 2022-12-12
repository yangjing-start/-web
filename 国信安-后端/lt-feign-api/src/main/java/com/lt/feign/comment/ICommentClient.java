package com.lt.feign.comment;

import com.lt.model.user.PostMessage;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author Lhz
 */
@FeignClient(value = "lt-service-comment", configuration = CommentFeignConfig.class)
public interface ICommentClient {

    @PostMapping("/insertPostMessage")
    ResponseEntity<Void> insertPostMessage(@RequestBody PostMessage postMessage);

    @PostMapping(value = "/deletePostMessage")
    void deletePostMessage(@RequestBody String username);
}
