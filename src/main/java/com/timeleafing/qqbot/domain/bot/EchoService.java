package com.timeleafing.qqbot.domain.bot;

import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import com.mikuac.shiro.model.ArrayMsg;

import java.util.List;

public interface EchoService {

    List<ArrayMsg> test();

    List<ArrayMsg> chat(AnyMessageEvent event, String prefix);

}
