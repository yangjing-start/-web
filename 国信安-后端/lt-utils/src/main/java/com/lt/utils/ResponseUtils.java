package com.lt.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Lhz
 */
public class ResponseUtils {

    public static Mono<Void> GlobalFilterResponse(Map<String ,String> resultMap, ServerHttpResponse response){
        byte[] s;
        try {
            s = new ObjectMapper().writeValueAsBytes(resultMap);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        response.setStatusCode(HttpStatus.LOCKED);
        DataBufferFactory bufferFactory = response.bufferFactory();
        DataBuffer wrap = bufferFactory.wrap(s);
        return response.writeWith(Mono.fromSupplier(() -> wrap));
    }

    public static void UnauthorizedResponse(HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        Map<String, String> resultMap = new HashMap<>(3);
        resultMap.put("code", String.valueOf(HttpServletResponse.SC_UNAUTHORIZED));
        resultMap.put("msg", "请登录！");
        String s = new ObjectMapper().writeValueAsString(resultMap);
        response.getWriter().println(s);
    }

    public static void OtherLoginResponse(HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        Map<String, String> resultMap = new HashMap<>(3);
        resultMap.put("code", String.valueOf(HttpServletResponse.SC_UNAUTHORIZED));
        resultMap.put("msg", "账号在别处登录");
        String s = new ObjectMapper().writeValueAsString(resultMap);
        response.getWriter().println(s);
    }

    public static void ForbiddenResponse(HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        Map<String, String> resultMap = new HashMap<>(3);
        resultMap.put("code", String.valueOf(HttpServletResponse.SC_FORBIDDEN));
        resultMap.put("msg", "非法登录");
        String s = new ObjectMapper().writeValueAsString(resultMap);
        response.getWriter().println(s);
    }
}
