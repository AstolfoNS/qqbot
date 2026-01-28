package com.timeleafing.qqbot.config.properties;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@ConfigurationProperties(prefix = "qqbot")
public class QqBotProperties {

    @NotNull
    private Long qqBotId;

}
