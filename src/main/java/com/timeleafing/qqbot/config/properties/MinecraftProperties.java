package com.timeleafing.qqbot.config.properties;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@ConfigurationProperties(prefix = "minecraft")
public class MinecraftProperties {

    @NotBlank
    private String wsUri;

    @NotBlank
    private String httpUri;

}
