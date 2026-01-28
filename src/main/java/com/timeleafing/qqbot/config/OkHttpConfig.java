package com.timeleafing.qqbot.config;

import com.timeleafing.qqbot.config.properties.OkHttpProperties;
import com.timeleafing.qqbot.interceptor.HmacAuthInterceptor;
import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
public class OkHttpConfig {

    private final OkHttpProperties props;


    @Bean
    public OkHttpClient okHttpClient(HmacAuthInterceptor hmacAuthInterceptor) {
        return new OkHttpClient.Builder()
                .connectTimeout(Duration.ofSeconds(props.getConnectTimeout()))
                .readTimeout(Duration.ofSeconds(props.getReadTimeout()))
                .writeTimeout(Duration.ofSeconds(props.getWriteTimeout()))
                .addInterceptor(hmacAuthInterceptor)
                .build();
    }
}
