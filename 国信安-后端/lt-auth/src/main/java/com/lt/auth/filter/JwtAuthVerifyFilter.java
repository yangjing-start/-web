package com.lt.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lt.auth.config.AuthRsaKeyProperties;
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
import java.util.Objects;

import static com.lt.utils.ResponseUtils.OtherLoginResponse;
import static com.lt.utils.ResponseUtils.UnauthorizedResponse;

/**
 * @author Lhz
 */
public class JwtAuthVerifyFilter extends BasicAuthenticationFilter {

    private final AuthRsaKeyProperties prop;
    private final RedisTemplate<String ,String > redisTemplate;
    public JwtAuthVerifyFilter(AuthenticationManager authenticationManager, AuthRsaKeyProperties prop, RedisTemplate<String, String> redisTemplate) {
        super(authenticationManager);
        this.prop = prop;
        this.redisTemplate = redisTemplate;
    }

    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        // 因该过滤器在前面，所以要前放行login
        String requestURI = request.getRequestURI();

        if ("/login".equals(requestURI) || "/register".equals(requestURI) || "/vc.jpg".equals(requestURI)|| requestURI.contains("/verification/mail/") || "/logout".equals(requestURI)) {
            // 放行
            chain.doFilter(request, response);
            // 必须加return
            return;
        }
        String header = request.getHeader("token");
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
                if(!Objects.equals(redisTemplate.opsForValue().get(user.getUsername() + "token"), token)){
                    OtherLoginResponse(response);
                }else{
                    UsernamePasswordAuthenticationToken authResult = new UsernamePasswordAuthenticationToken(user.getUsername(), null, user.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authResult);
                    chain.doFilter(request, response);
                }
            } else {
                response.setContentType("application/json;charset=utf-8");
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                Map<String, String> resultMap = new HashMap<>(10);
                resultMap.put("code", String.valueOf(HttpServletResponse.SC_FORBIDDEN));
                resultMap.put("msg", "非法登录");
                String s = new ObjectMapper().writeValueAsString(resultMap);
                response.getWriter().println(s);
                chain.doFilter(request, response);
            }
        }

    }

}


