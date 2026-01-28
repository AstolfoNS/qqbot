package com.timeleafing.qqbot.domain.minecraft;

import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;

public interface MinecraftServerService {

    void startServer(Bot bot, AnyMessageEvent event);

    void stopServer(Bot bot, AnyMessageEvent event);

    void sendCmd(String cmd);

    void sendBotMsgToGroup(String logInfo);

}
