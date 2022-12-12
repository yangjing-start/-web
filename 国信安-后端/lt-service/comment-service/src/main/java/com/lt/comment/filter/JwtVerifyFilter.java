package com.lt.comment.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lt.comment.config.RsaKeyProperties;
import com.lt.model.user.User;
import com.lt.utils.JwtUtils;
import com.lt.utils.Payload;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.ObjectUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.lt.utils.ResponseUtils.*;

/**
 * @author Lhz
 */
public final class JwtVerifyFilter extends BasicAuthenticationFilter {

    private final RsaKeyProperties prop;
    private final RedisTemplate<String ,String> redisTemplate;
    public JwtVerifyFilter(final AuthenticationManager authenticationManager, @Qualifier("comment-RsaKey") RsaKeyProperties prop, final RedisTemplate<String, String> redisTemplate) {
        super(authenticationManager);
        this.prop = prop;
        this.redisTemplate = redisTemplate;
    }

    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        final String header = request.getHeader("token");
        final Boolean isFeignRequest = ("/insertPostMessage".equals(request.getRequestURI()) || "/deletePostMessage".equals(request.getRequestURI())) && Objects.equals(header, "hahaha");
        final Boolean isGetCommentsRequest = Objects.equals(request.getRequestURI(), "/getComments") ;
        final Boolean isGetSubCommentsRequest = Objects.equals(request.getRequestURI(), "/getSubComments");

        if(isGetSubCommentsRequest || isGetCommentsRequest || isFeignRequest){
            chain.doFilter(request, response);
            return;
        }

        if (header == null || !header.startsWith("lt ")) {
            //如果携带错误的token，则给用户提示请登录！
            UnauthorizedResponse(response);
        } else {
            //如果携带了正确格式的token要先得到token
            final String token = header.replace("lt ", "");
            final Payload<User> payload = JwtUtils.getInfoFromToken(token, prop.getPublicKey(), User.class);
            final User user = payload.getUserInfo();
            if (user != null) {
                if(!redisTemplate.opsForValue().get(user.getUsername()+"token").equals(token)){
                    OtherLoginResponse(response);
                }else{
                    final UsernamePasswordAuthenticationToken authResult = new UsernamePasswordAuthenticationToken(user.getUsername(), null, user.getAuthorities());
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