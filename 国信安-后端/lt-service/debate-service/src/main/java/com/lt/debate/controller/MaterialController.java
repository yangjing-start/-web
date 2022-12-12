package com.lt.debate.controller;

import com.lt.debate.config.RsaKeyProperties;
import com.lt.debate.service.MaterialService;
import com.lt.model.debate.dto.MaterialDto;
import com.lt.model.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

import static com.lt.utils.TokenUtils.CheckToken;

/**
 * @Author WanXin
 * @Date 2022/11/23
 */
@RestController
@RequestMapping("/material")
@RequiredArgsConstructor
@CrossOrigin
public class MaterialController {

    private final MaterialService materialService;
    private final RsaKeyProperties prop;

    @PreAuthorize("hasAuthority('normal')")
    @PostMapping("/save")
    public Response save(MaterialDto dto, @RequestHeader(value = "token") final String token) {
        if(CheckToken(dto.getUsername().toString(), token, prop.getPublicKey())){
            return new Response<>(500, "不合法的修改");
        }
        return materialService.save(dto.getMultipartFile(), dto.getUsername());
    }

    @PreAuthorize("hasAuthority('normal')")
    @PostMapping("/delete")
    public Response delete(@RequestBody Map map, @RequestHeader(value = "token") final String token) {
        if(CheckToken(map.get("username").toString(), token, prop.getPublicKey())){
            return new Response<>(500, "不合法的修改");
        }
        return materialService.delete(map);
    }

    @PreAuthorize("hasAuthority('normal')")
    @PostMapping("/getAll")
    public Response getAll(@RequestBody Map map, @RequestHeader(value = "token") final String token) {
        if(CheckToken(map.get("username").toString(), token, prop.getPublicKey())){
            return new Response<>(500, "不合法的修改");
        }
        return materialService.getAll(map);
    }
}
