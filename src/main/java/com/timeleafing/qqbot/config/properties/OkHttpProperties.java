package com.timeleafing.qqbot.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties(prefix = "okhttp")
public class OkHttpProperties {

    private int connectTimeout;

    private int readTimeout;

    private int writeTimeout;

}
