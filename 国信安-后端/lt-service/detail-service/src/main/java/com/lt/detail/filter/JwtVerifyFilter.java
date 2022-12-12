package com.lt.detail.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lt.detail.config.RsaKeyProperties;
import com.lt.model.user.User;
import com.lt.utils.JwtUtils;
import com.lt.utils.Payload;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.lt.utils.ResponseUtils.*;

/**
 * @author Lhz
 */
public class JwtVerifyFilter extends BasicAuthenticationFilter {

    private final RsaKeyProperties prop;
    private final RedisTemplate<String ,String> redisTemplate;
    public JwtVerifyFilter(AuthenticationManager authenticationManager, RsaKeyProperties prop, RedisTemplate<String, String> redisTemplate) {
        super(authenticationManager);
        this.prop = prop;
        this.redisTemplate = redisTemplate;
    }

    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String requestUri = request.getRequestURI();
        String header = request.getHeader("token");
        Boolean isFeignRequest = ("/detail/registration".equals(requestUri)
                || "/detail/deleteUser".equals(requestUri)) && "hahaha".equals(header);
        Boolean isOtherPageRequest = "/userProduct/getOtherDetail".equals(requestUri);
        Boolean isFansRequest = "/userProduct/fans".equals(requestUri);
        Boolean isFollowsRequest = "/userProduct/follows".equals(requestUri);
        if (isFeignRequest || isOtherPageRequest || isFansRequest || isFollowsRequest) {
            chain.doFilter(request, response);
            return;
        }
        if (header == null || !header.startsWith("lt ")) {
            //如果携带错误的token，则给用户提示请登录！
            UnauthorizedResponse(response);
        } else {
            //如果携带了正确格式的token要先得到token
            String token = header.replace("lt ", "");
            //验证token是否正确
            Payload<User> payload = JwtUtils.getInfoFromToken(token, prop.getPublicKey(), User.class);
            User user = payload.getUserInfo();
            if (user != null) {
                if(!redisTemplate.opsForValue().get(user.getUsername()+"token").equals(token)){
                    OtherLoginResponse(response);
                }else{
                    UsernamePasswordAuthenticationToken authResult = new UsernamePasswordAuthenticationToken(user.getUsername(), null, user.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authResult);
                    chain.doFilter(request, response);
                }
            } else {
                ForbiddenResponse(response);
                chain.doFilter(request, response);
            }
        }
    }

}