package com.timeleafing.qqbot.domain.bot.impl;

import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import com.mikuac.shiro.model.ArrayMsg;
import com.timeleafing.qqbot.common.util.ShiroMsgUtils;
import com.timeleafing.qqbot.domain.bot.EchoService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EchoServiceImpl implements EchoService {

    @Override
    public List<ArrayMsg> test() {
        return ShiroMsgUtils.text("Success!");
    }

    @Override
    public List<ArrayMsg> chat(AnyMessageEvent event, String prefix) {
        return ShiroMsgUtils.reply(ShiroMsgUtils.removePrefix(event.getArrayMsg(), prefix), event.getMessageId());
    }

}
