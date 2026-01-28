package com.timeleafing.qqbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan("com.timeleafing.qqbot")
@SpringBootApplication
public class QqBotApplication {

    static void main(String[] args) {
        SpringApplication.run(QqBotApplication.class, args);
    }

}
