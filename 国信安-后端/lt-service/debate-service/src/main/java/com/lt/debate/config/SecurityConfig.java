package com.lt.debate.config;

import com.lt.debate.filter.JwtVerifyFilter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Collections;

import static com.lt.debate.filter.JwtVerifyFilter.*;

/**
 * @author Lhz
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final RsaKeyProperties prop;
    private final RedisTemplate<String,String> redisTemplate;
    public SecurityConfig(@Qualifier("debate-RsaKey") RsaKeyProperties prop, RedisTemplate<String, String> redisTemplate) {
        this.prop = prop;
        this.redisTemplate = redisTemplate;
    }


    public void configure(HttpSecurity http) throws Exception {
        http.csrf()
                .disable()
                .authorizeRequests()
                .mvcMatchers(GET_CONTENT_INFO_URL,
                        SUGGESTION_URL,
                        GET_HOT_URL,
                        GET_BY_KIND_URL,
                        GET_HOT_BY_USER,
                        GET_CONTENT_BY_ID).permitAll()
                .anyRequest()
                .authenticated()
                .and().cors().configurationSource(configurationSource())
                .and()
                .addFilter(new JwtVerifyFilter(super.authenticationManager(), prop, redisTemplate))
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    CorsConfigurationSource configurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedHeaders(Collections.singletonList("*"));
        corsConfiguration.setAllowedMethods(Collections.singletonList("*"));
        corsConfiguration.setAllowedOrigins(Collections.singletonList("*"));
        corsConfiguration.setMaxAge(3600L);
        corsConfiguration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }
}
