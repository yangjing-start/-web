package com.lt.debate.controller;

import com.lt.debate.config.RsaKeyProperties;
import com.lt.debate.service.DebateContentService;
import com.lt.model.debate.dto.DebateContentDto;
import com.lt.model.debate.dto.DebatePageDto;
import com.lt.model.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.lt.utils.TokenUtils.CheckToken;

/**
 * @Author WanXin
 * @Date 2022/11/15
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/content")
@CrossOrigin
public class ContentController {


    private final DebateContentService contentService;
    private final RsaKeyProperties prop;

    @PreAuthorize("hasAuthority('normal')")
    @PostMapping("/getKinds")
    public Response getKinds(@RequestHeader(value = "token") final String token) {
        return contentService.getKinds();
    }

    @PostMapping("/getByKind")
    public Response getByKind(@RequestBody DebatePageDto dto) {
        return contentService.getByKind(dto);
    }

    @PostMapping("/getHot")
    public Response getHot() {
        return contentService.getHot();
    }

    @PostMapping("/suggestion")
    public Response getSuggestions(@RequestBody Map map) {
        return contentService.getSuggestions(map);
    }

    @PreAuthorize("hasAuthority('normal')")
    @PostMapping("/saveContent")
    public Response saveContent(@RequestBody DebateContentDto dto, @RequestHeader(value = "token") final String token) {
        if(CheckToken(dto.getUsername(), token, prop.getPublicKey())){
            return new Response<>(500, "不合法的修改");
        }
        return contentService.saveContent(dto);
    }

    @PostMapping("/getContentInfo")
    public Response getContentInfo(@RequestBody Map map) {
        return contentService.getContentInfo(map);
    }

    @PreAuthorize("hasAuthority('normal')")
    @PostMapping("/deleteAllById")
    public Response deleteById(@RequestBody Map map, @RequestHeader(value = "token") final String token) {
        if(CheckToken(map.get("username").toString(), token, prop.getPublicKey())){
            return new Response<>(500, "不合法的修改");
        }
        return contentService.deleteById(map);
    }

    @PreAuthorize("hasAuthority('normal')")
    @PostMapping("/updateInfo")
    public Response updateInfo(@RequestBody Map map, @RequestHeader(value = "token") final String token) {
        if(CheckToken(map.get("username").toString(), token, prop.getPublicKey())){
            return new Response<>(500, "不合法的修改");
        }
        return contentService.updateInfo(map);
    }

    @PostMapping("/getHotByUser")
    public Response getHotByUser(@RequestBody Map map) {
        return contentService.getHotByUser(map);
    }

    @PreAuthorize("hasAuthority('normal')")
    @PostMapping("/getContentByUserId")
    public Response getContentByUserId(@RequestBody Map map, @RequestHeader(value = "token") final String token) {
        if(CheckToken(map.get("username").toString(), token, prop.getPublicKey())){
            return new Response<>(500, "不合法的修改");
        }
        return contentService.getContentByUserId(map);
    }

    @PostMapping("/getContentById")
    public Response getContentById(@RequestBody Map map) {
        return contentService.getContentById(map);
    }
}
