package com.lt.app.gateway.filter;

import com.lt.model.user.User;
import com.lt.utils.JwtUtils;
import com.lt.utils.Payload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.lt.utils.ResponseUtils.GlobalFilterResponse;

/**
 * @author Wx
 */
@Component
@Slf4j
@CrossOrigin
public class AuthorizeFilter implements Ordered, GlobalFilter {

    private static final String LOGIN_URL = "/login";
    private static final String VC_URL = "/vc.jpg";
    private static final String MAIL_URL = "/verification/mail";
    private static final String REGISTER_URL = "/register";
    private static final String GET_OTHER_DETAIL = "/userProduct/getOtherDetail";
    private static final String GET_COMMENTS_URL = "/getComments";
    private static final String GET_SUB_COMMENTS_URL = "/getSubComments";
    private static final String GET_CONTENT_INFO_URL = "/content/getContentInfo";
    private static final String SUGGESTION_URL = "/content/suggestion";
    private static final String GET_HOT_URL = "/content/getHot";
    private static final String GET_BY_KIND_URL = "/content/getByKind";
    private static final String GET_HOT_BY_USER = "/content/getHotByUser";
    private static final String REDA_URL = "/behavior/read";
    private static final String TOKEN_KEY = "token";
    private static final String FANS_KEY = "/userProduct/fans";
    private static final String FOLLOWS_KEY = "/userProduct/follows";
    public static final String GET_CONTENT_BY_ID   = "/content/getContentById";
    private final RedisTemplate<String, String> redisTemplate;
    private final RsaKeyProperties prop;

    public AuthorizeFilter(RedisTemplate<String, String> redisTemplate,@Qualifier("gateway-RsaKey") RsaKeyProperties prop) {
        this.redisTemplate = redisTemplate;
        this.prop = prop;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1.获取request和response对象
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        // 2.判断是否登录
        if (request.getURI().getPath().contains(LOGIN_URL)
                ||request.getURI().getPath().contains(VC_URL)
                ||request.getURI().getPath().contains(MAIL_URL)
                ||request.getURI().getPath().contains(REGISTER_URL)
                ||request.getURI().getPath().contains(GET_COMMENTS_URL)
                ||request.getURI().getPath().contains(GET_SUB_COMMENTS_URL)
                ||request.getURI().getPath().contains(GET_CONTENT_INFO_URL)
                ||request.getURI().getPath().contains(SUGGESTION_URL)
                ||request.getURI().getPath().contains(GET_HOT_URL)
                ||request.getURI().getPath().contains(GET_BY_KIND_URL)
                ||request.getURI().getPath().contains(GET_HOT_BY_USER)
                ||request.getURI().getPath().contains(REDA_URL)
                ||request.getURI().getPath().contains(FANS_KEY)
                ||request.getURI().getPath().contains(FOLLOWS_KEY)
                ||request.getURI().getPath().contains(GET_CONTENT_BY_ID)
                ||request.getURI().getPath().contains(GET_OTHER_DETAIL)
        ) {
            return chain.filter(exchange);
        }

        String token = request.getHeaders().getFirst(TOKEN_KEY);

        // 判断token是否存在
        if (StringUtils.isEmpty(token)) {
            Map<String, String> resultMap = new HashMap<>(10);
            resultMap.put("code", HttpStatus.UNAUTHORIZED.toString());
            resultMap.put("msg", "请登录！");
            return GlobalFilterResponse(resultMap, response);
        }
        String realToken = token.replace("lt ", "");
//        realToken = com.lt.utils.StringUtils.deleteCharString0(realToken);

        // 查看token是否在黑名单中
        if(!ObjectUtils.isEmpty(redisTemplate.opsForValue().get(realToken))){
            Map<String, String> resultMap = new HashMap<>(3);
            resultMap.put("code", HttpStatus.BAD_REQUEST.toString());
            resultMap.put("msg", "token已过期");
            return GlobalFilterResponse(resultMap, response);
        }
        // 验证token是否为最新值 否则为二次登录
        String username = JwtUtils.getUsernameFromToken(realToken, prop.getPublicKey());
        if(!Objects.equals(redisTemplate.opsForValue().get(username + TOKEN_KEY), realToken)){
            Map<String, String> resultMap = new HashMap<>(3);
            resultMap.put("code", HttpStatus.IM_USED.toString());
            resultMap.put("msg", "在别处登录,请重新登陆");
            return GlobalFilterResponse(resultMap, response);
        }

        //  Finally 放行
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
