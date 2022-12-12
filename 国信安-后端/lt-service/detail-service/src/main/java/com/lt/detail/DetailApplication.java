package com.lt.detail;


import com.lt.detail.config.RsaKeyProperties;
import com.lt.model.debate.pojo.DebateContent;
import org.mybatis.spring.annotation.MapperScan;
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
@MapperScan()
@EnableConfigurationProperties(RsaKeyProperties.class)
public class DetailApplication {
    public static void main(String[] args) {
        SpringApplication.run(DetailApplication.class, args);
    }
}
