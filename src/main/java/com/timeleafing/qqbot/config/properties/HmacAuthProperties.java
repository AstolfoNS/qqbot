package com.timeleafing.qqbot.config.properties;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;

@Data
@Validated
@ConfigurationProperties(prefix = "hmac-security")
public class HmacAuthProperties {

    @NotBlank
    private String hmacSecret;

    @NotBlank
    private String headerTs = "X-TS";

    @NotBlank
    private String headerNonce = "X-NONCE";

    @NotBlank
    private String headerSign = "X-SIGN";

    private List<String> allowedHosts = new ArrayList<>();

}
