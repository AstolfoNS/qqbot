package com.timeleafing.qqbot.domain.bot;

import com.mikuac.shiro.annotation.AnyMessageHandler;
import com.mikuac.shiro.annotation.MessageHandlerFilter;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import com.mikuac.shiro.model.ArrayMsg;

import java.util.List;

public interface LotteryService {

    List<ArrayMsg> roll(AnyMessageEvent event, String prefix);

}
