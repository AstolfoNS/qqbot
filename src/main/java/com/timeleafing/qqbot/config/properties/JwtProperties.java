package com.timeleafing.qqbot.config.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@ConfigurationProperties(prefix = "spring.security.jwt")
public class JwtProperties {

    @NotBlank
    private String key;

    private long expire;

    @NotBlank
    private String issuer;

    @NotNull
    private MacAlgorithm jwtAlgorithm;

    @NotBlank
    private String jcaAlgorithm;

}
