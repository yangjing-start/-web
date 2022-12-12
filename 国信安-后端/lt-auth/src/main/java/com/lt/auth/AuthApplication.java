package com.lt.auth;

import com.lt.auth.config.AuthRsaKeyProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author Lhz
 */
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.lt"})
@SpringBootApplication
@ComponentScan("com.lt")
@EnableConfigurationProperties(AuthRsaKeyProperties.class)
public class AuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }
}


