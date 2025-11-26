package com.timeleafing.qqbot.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties(prefix = "okhttp")
@Component
public class OkHttpProperties {

    private Integer connectTimeout;

    private Integer readTimeout;

    private Integer writeTimeout;

}
