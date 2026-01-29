package com.timeleafing.qqbot.api.listener;

import com.mikuac.shiro.annotation.AnyMessageHandler;
import com.mikuac.shiro.annotation.MessageHandlerFilter;
import com.mikuac.shiro.annotation.common.Shiro;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import com.timeleafing.qqbot.domain.bot.LotteryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;

@Shiro
@RequiredArgsConstructor
@Component
public class LotteryListener {

    private final LotteryService lotteryService;


    @AnyMessageHandler
    @MessageHandlerFilter(cmd = "^/roll\\s+[\\s\\S]*")
    public void roll(Bot bot, AnyMessageEvent event, Matcher matcher) {
        bot.sendMsg(event, lotteryService.roll(event, "^/roll\\s+"), false);
    }

}
