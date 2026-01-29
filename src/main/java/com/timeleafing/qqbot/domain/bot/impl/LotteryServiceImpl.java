package com.timeleafing.qqbot.domain.bot.impl;

import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import com.mikuac.shiro.model.ArrayMsg;
import com.timeleafing.qqbot.common.util.NumberUtils;
import com.timeleafing.qqbot.common.util.ShiroMsgUtils;
import com.timeleafing.qqbot.domain.bot.LotteryService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class LotteryServiceImpl implements LotteryService {

    @Override
    public List<ArrayMsg> roll(AnyMessageEvent event, String prefix) {
        List<List<ArrayMsg>> optionList = ShiroMsgUtils.group(ShiroMsgUtils.split(ShiroMsgUtils.removePrefix(event.getArrayMsg(), prefix)));

        if (CollectionUtils.isEmpty(optionList)) {
            return ShiroMsgUtils.reply(ShiroMsgUtils.text("无效的选项 >_<"), event.getMessageId());
        }
        List<ArrayMsg> randomMsg = optionList.get((int) NumberUtils.randomFromZero(optionList.size() - 1));

        return ShiroMsgUtils.reply(randomMsg, event.getMessageId());
    }

}
