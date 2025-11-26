package com.timeleafing.qqbot.service.minecraft;

import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;

public interface MinecraftServerService {

    void startMinecraftServer(Bot bot, AnyMessageEvent event);

    void stopMinecraftServer(Bot bot, AnyMessageEvent event);

    void sendMinecraftCommand(String command);

    void sendBotMessage(String logInfo);

}
