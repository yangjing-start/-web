package com.lt.comment.controller;

import com.lt.comment.config.RsaKeyProperties;
import com.lt.comment.service.HistoryService;
import com.lt.model.comment.History;
import com.lt.model.response.Response;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Size;
import java.text.SimpleDateFormat;
import java.util.List;

import static com.lt.utils.TokenUtils.CheckToken;
import com.lt.comment.controller.dto.*;
/**
 * @author Lhz
 */
@RestController
public final class HistoryController {

    private final HistoryService historyService;
    private final RsaKeyProperties prop;

    public HistoryController(final HistoryService historyService, @Qualifier("comment-RsaKey") final RsaKeyProperties prop) {
        this.historyService = historyService;
        this.prop = prop;
    }
    @PreAuthorize("hasAuthority('normal')")
    @PostMapping("/insertHistory")
    public Response<String> insertHistory(@RequestBody final InsertHistoryDTO dto, @RequestHeader(value = "token") final String token) {
        if(CheckToken(dto.getUsername(), token, prop.getPublicKey())){
            return new Response<>(500, "不合法的修改");
        }

        historyService.insertHistory(dto.getUsername(), dto.getDebateId(), dto.getDebateNickname(), dto.getUserImage(), dto.getTitle());
        return new Response<>(200, "历史记录新增成功");
    }
    @PreAuthorize("hasAuthority('normal')")
    @PostMapping("/deleteHistory")
    public Response<String> deleteHistory(@RequestBody final DeleteHistoryDTO dto, @RequestHeader(value = "token") final String token) {
        if(CheckToken(dto.getUsername(), token, prop.getPublicKey())){
            return new Response<>(500, "不合法的修改");
        }

        historyService.deleteHistory(dto.getDebateId());

        return new Response<>(200, "删除成功");
    }
    @PreAuthorize("hasAuthority('normal')")
    @PostMapping("/getHistory")
    public Response<List<History>> getHistory(@RequestBody final GetHistoryDTO dto, @RequestHeader(value = "token") final String token) {
        if(CheckToken(dto.getUsername(), token, prop.getPublicKey())){
            return new Response<>(500, "不合法的修改");
        }

        final List<History> res = historyService.getHistory(dto.getUsername(), dto.getPageSize(), dto.getBeginPage());

        return new Response<>(200, res);
    }



}
