package com.timeleafing.qqbot.api.listener;

import com.mikuac.shiro.annotation.AnyMessageHandler;
import com.mikuac.shiro.annotation.MessageHandlerFilter;
import com.mikuac.shiro.annotation.common.Shiro;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import com.timeleafing.qqbot.domain.minecraft.MinecraftServerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;

@Shiro
@RequiredArgsConstructor
@Component
public class MinecraftListener {

    private final MinecraftServerService minecraftServerService;


    @AnyMessageHandler
    @MessageHandlerFilter(cmd = "^/mc\\s+start")
    public void start(Bot bot, AnyMessageEvent event, Matcher matcher) {
        minecraftServerService.startServer(bot, event);
    }

    @AnyMessageHandler
    @MessageHandlerFilter(cmd = "^/mc\\s+stop")
    public void stop(Bot bot, AnyMessageEvent event, Matcher matcher) {
        minecraftServerService.stopServer(bot, event);
    }

}
