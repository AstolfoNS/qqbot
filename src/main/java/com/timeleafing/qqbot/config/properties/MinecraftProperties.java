package com.timeleafing.qqbot.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@ConfigurationProperties(prefix = "minecraft")
@Component
public class MinecraftProperties {

    private String wsUri;

    private String httpUri;

    private List<Long> qqGroupIdList;

}
