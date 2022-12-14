package com.lt.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lt.auth.filter.JwtAuthVerifyFilter;
import com.lt.auth.filter.LoginFilter;
import com.lt.auth.service.UserDetailServiceImpl;
import com.lt.model.user.Authority;
import com.lt.model.user.User;
import com.lt.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.ObjectUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.reactive.HiddenHttpMethodFilter;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.*;


/**
 * @author Lhz
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailServiceImpl userService;
    private final RedisTemplate<String, String> redisTemplate;
    private final AuthRsaKeyProperties prop;

    public SecurityConfig(UserDetailServiceImpl userService, RedisTemplate<String, String> redisTemplate, @Qualifier("AuthRsaKey") AuthRsaKeyProperties prop) {
        this.userService = userService;
        this.redisTemplate = redisTemplate;
        this.prop = prop;
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService);
    }

    //??????AuthenticationManager
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public LoginFilter loginFilter() throws Exception {
        LoginFilter loginFilter = new LoginFilter(redisTemplate);
        //??????url
        loginFilter.setFilterProcessesUrl("/login");
        //???????????????????????????
        loginFilter.setUsernameParameter("username");
        loginFilter.setPasswordParameter("password");
        loginFilter.setKaptchaParameter("imageVerify");
        //?????????????????????
        loginFilter.setAuthenticationManager(authenticationManagerBean());
        //??????????????????
        loginFilter.setAuthenticationSuccessHandler(((request, response, authentication) -> {
            userService.setAuthenticationManager(authentication1 -> authentication);
            Map<String, Object> result = new HashMap<>(16);
            result.put("msg", "????????????");
            result.put("userMessage", authentication.getPrincipal());

            //?????????????????????????????????????????????????????? ???????????????????????????token???????????????
            String lastToken = redisTemplate.opsForValue().get(authentication.getName() + "token");
            if(!ObjectUtils.isEmpty(lastToken)){
                redisTemplate.opsForValue().set(lastToken, "1", Duration.ofMinutes(99));
            }

            //??????Token
            User user = new User();
            user.setUsername(authentication.getName());
            user.setAuthorities((List<Authority>) authentication.getAuthorities());
            String token = JwtUtils.generateTokenExpireInMinutes(user, prop.getPrivateKey(), 24 * 60);
            redisTemplate.opsForValue().set(authentication.getName() + "token", token);
            response.setHeader("Access-Control-Expose-Headers", "Authorization");
            response.setHeader("Authorization", "lt " + token);
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpStatus.OK.value());
            String s = new ObjectMapper().writeValueAsString(result);
            response.getWriter().println(s);
        }));
        //??????????????????
        loginFilter.setAuthenticationFailureHandler(((request, response, exception) -> {
            Map<String, Object> result = new HashMap<>(16);
            result.put("msg", "????????????");
            if (!ObjectUtils.isEmpty(exception)) {
                if (Objects.equals(exception.getMessage(), "Bad credentials")) {
                    result.put("case", "???????????????????????????");
                } else {
                    result.put("case", exception.getMessage());
                }
            }
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            String s = new ObjectMapper().writeValueAsString(result);
            response.getWriter().println(s);
        }));
        return loginFilter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        String regex = "^[a-z0-9]+([._\\\\-]*[a-z0-9])*@([a-z0-9]+[-a-z0-9]*[a-z0-9]+.){1,63}[a-z0-9]+$";
        http.authorizeRequests()
                //????????????????????? ???????????????????????????????????????????????????????????????
                .mvcMatchers(HttpMethod.GET, "/vc.jpg").permitAll()
                .mvcMatchers("/logout").permitAll()
                .mvcMatchers(HttpMethod.GET, "/verification/mail/{param:" + regex + "}").permitAll()
                .mvcMatchers(HttpMethod.POST, "/register").permitAll()
                .anyRequest().authenticated()
                .and().formLogin()
                .and().cors().configurationSource(configurationSource())
                .and().exceptionHandling().authenticationEntryPoint(((request, response, authException) -> {
                    response.setContentType("application/json;charset=UTF-8");
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.getWriter().println("????????????");
                }))
                .and().logout()
                .logoutUrl("/logout")
                .logoutSuccessHandler((request, response, authentication) -> {
                    String realToken = request.getHeader("token");
                    String username = JwtUtils.getUsernameFromToken(realToken, prop.getPublicKey());
                    Map<String, Object> result = new HashMap<>(3);
                    String token = redisTemplate.opsForValue().get(username + "token");
                    if (!ObjectUtils.isEmpty(token)) {
                        //???????????????token??????????????? ????????????token?????????????????????
                        redisTemplate.opsForValue().set(token, "1", Duration.ofMinutes(99));
                        redisTemplate.delete(username+"token");
                        result.put("msg", "????????????");
                    } else {
                        result.put("msg", "????????????");
                    }
                    response.setContentType("application/json;charset=UTF-8");
                    response.setStatus(HttpStatus.OK.value());
                    String s = new ObjectMapper().writeValueAsString(result);
                    response.getWriter().println(s);
                })
                .and().csrf().disable();
        http.addFilterAfter(new JwtAuthVerifyFilter(super.authenticationManager(), prop, redisTemplate), UsernamePasswordAuthenticationFilter.class);
        http.addFilterAt(loginFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public HiddenHttpMethodFilter hiddenHttpMethodFilter() {
        return new HiddenHttpMethodFilter() {
            @Override
            public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
                return chain.filter(exchange);
            }
        };
    }

    CorsConfigurationSource configurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedHeaders(Collections.singletonList("*"));
        corsConfiguration.setAllowedMethods(Collections.singletonList("*"));
        corsConfiguration.setAllowedOrigins(Collections.singletonList("*"));
        corsConfiguration.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }
}
