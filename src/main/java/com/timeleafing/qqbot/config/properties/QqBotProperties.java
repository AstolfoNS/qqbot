package com.timeleafing.qqbot.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties(prefix = "qqbot")
@Component
public class QqBotProperties {

    private Long qqBotId;

}
