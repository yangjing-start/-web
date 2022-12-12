package com.lt.behavior.filter;

import com.lt.behavior.config.RsaKeyProperties;
import com.lt.model.user.User;
import com.lt.utils.JwtUtils;
import com.lt.utils.Payload;
import org.springframework.beans.factory.annotation.Qualifier;
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
import java.util.Objects;

import static com.lt.utils.ResponseUtils.*;

/**
 * @author Lhz
 */
public final class JwtVerifyFilter extends BasicAuthenticationFilter {

    private final RsaKeyProperties prop;
    private final RedisTemplate<String ,String> redisTemplate;
    public static final String REDA_URL = "/behavior/read";
    public JwtVerifyFilter(final AuthenticationManager authenticationManager, @Qualifier("behavior-RsaKey") RsaKeyProperties prop, final RedisTemplate<String, String> redisTemplate) {
        super(authenticationManager);
        this.prop = prop;
        this.redisTemplate = redisTemplate;
    }

    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        final String header = request.getHeader("token");
        final Boolean isRead = Objects.equals(request.getRequestURI(), REDA_URL) ;

        if(isRead){
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