package com.timeleafing.qqbot.config;

import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class OkHttpConfig {

    @Value("#{okHttpProperties.connectTimeout}")
    private Integer connectTimeout;

    @Value("#{okHttpProperties.readTimeout}")
    private Integer readTimeout;

    @Value("#{okHttpProperties.writeTimeout}")
    private Integer writeTimeout;


    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient
                .Builder()
                .connectTimeout(Duration.ofSeconds(connectTimeout))
                .readTimeout(Duration.ofSeconds(readTimeout))
                .writeTimeout(Duration.ofSeconds(writeTimeout))
                .build();
    }
}
