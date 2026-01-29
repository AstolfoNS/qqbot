package com.timeleafing.qqbot.api.listener;

import com.mikuac.shiro.annotation.AnyMessageHandler;
import com.mikuac.shiro.annotation.MessageHandlerFilter;
import com.mikuac.shiro.annotation.common.Shiro;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import com.timeleafing.qqbot.domain.bot.EchoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;

@Shiro
@RequiredArgsConstructor
@Component
public class EchoListener {

    private final EchoService echoService;


    @AnyMessageHandler
    @MessageHandlerFilter(cmd = "^/test")
    public void test(Bot bot, AnyMessageEvent event, Matcher matcher) {
        bot.sendMsg(event, echoService.test(), false);
    }

    @AnyMessageHandler
    @MessageHandlerFilter(cmd = "^/chat\\s+[\\s\\S]*")
    public void chat(Bot bot, AnyMessageEvent event, Matcher matcher) {
        bot.sendMsg(event, echoService.chat(event, "^/chat\\s+"), false);
    }

}
