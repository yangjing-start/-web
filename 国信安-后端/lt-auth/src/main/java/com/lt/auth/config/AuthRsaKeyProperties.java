package com.lt.auth.config;

import com.lt.utils.RsaUtils;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * @author Lhz
 */
@Component("AuthRsaKey")
@ConfigurationProperties(prefix = "rsa.key")
@Data
public class AuthRsaKeyProperties {

    private String pubKeyFile;
    private String priKeyFile;

    private PublicKey publicKey;
    private PrivateKey privateKey;

    @PostConstruct
    public void createRsaKey() throws Exception {
        this.publicKey = RsaUtils.getPublicKey(pubKeyFile);
        this.privateKey = RsaUtils.getPrivateKey(priKeyFile);
    }

}
