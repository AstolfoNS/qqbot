package com.timeleafing.qqbot.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties(prefix = "spring.security.jwt")
@Component
public class JwtProperties {

    private String key;

    private Long expire;

    private String issuer;

    private MacAlgorithm jwtAlgorithm;

    private String jcaAlgorithm;

}
