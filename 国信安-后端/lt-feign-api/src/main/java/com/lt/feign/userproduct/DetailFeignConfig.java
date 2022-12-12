package com.lt.feign.userproduct;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

/**
 * @author Lhz
 */
@Configuration
@RequiredArgsConstructor
public class DetailFeignConfig implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        requestTemplate.header("token", "hahaha");
    }

}