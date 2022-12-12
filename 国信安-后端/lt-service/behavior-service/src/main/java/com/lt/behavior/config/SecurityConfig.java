package com.lt.behavior.config;

import com.lt.behavior.filter.JwtVerifyFilter;
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

import static com.lt.behavior.filter.JwtVerifyFilter.REDA_URL;

/**
 * @author Lhz
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final RsaKeyProperties prop;
    private final RedisTemplate<String,String> redisTemplate;
    public SecurityConfig(@Qualifier("behavior-RsaKey") RsaKeyProperties prop, RedisTemplate<String, String> redisTemplate) {
        this.prop = prop;
        this.redisTemplate = redisTemplate;
    }


    public void configure(HttpSecurity http) throws Exception {
        http.csrf()
                .disable()
                .authorizeRequests()
                .mvcMatchers(REDA_URL).permitAll()
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
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }

}
