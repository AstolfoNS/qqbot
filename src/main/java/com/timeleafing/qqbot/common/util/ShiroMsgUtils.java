package com.timeleafing.qqbot.common.util;

import com.mikuac.shiro.common.utils.ArrayMsgUtils;
import com.mikuac.shiro.enums.MsgTypeEnum;
import com.mikuac.shiro.model.ArrayMsg;
import com.timeleafing.qqbot.common.instance.WhiteSpace;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ShiroMsgUtils {

    public static ArrayMsg toText(String content) {
        return ArrayMsgUtils.builder()
                .text(content)
                .build()
                .getFirst();
    }

    public static ArrayMsg toAt(long userId) {
        return ArrayMsgUtils.builder()
                .at(userId)
                .build()
                .getFirst();
    }

    public static ArrayMsg toReply(int messageId) {
        return ArrayMsgUtils.builder()
                .reply(messageId)
                .build()
                .getFirst();
    }

    public static String getText(ArrayMsg message) {
        if (message.getType() != MsgTypeEnum.text) {
            throw new IllegalArgumentException("消息类型必须是text");
        }
        return message.getStringData("text");
    }

    public static List<ArrayMsg> text(String content) {
        return ShiroMsgUtils.merge(ShiroMsgUtils.toText(content));
    }

    public static List<ArrayMsg> merge(ArrayMsg... messages) {
        return new ArrayList<>(Arrays.asList(messages));
    }

    public static List<ArrayMsg> merge(List<ArrayMsg> msgList1, List<ArrayMsg> msgList2) {
        if (Objects.isNull(msgList1)) {
            throw new IllegalArgumentException("第一个消息列表不能为null");
        }
        if (Objects.isNull(msgList2)) {
            throw new IllegalArgumentException("第二个消息列表不能为null");
        }
        return Stream.concat(msgList1.stream(), msgList2.stream()).collect(Collectors.toList());
    }

    public static List<ArrayMsg> removePrefix(List<ArrayMsg> msgList, String prefixRegExp) {
        if (CollectionUtils.isEmpty(msgList)) {
            throw new IllegalArgumentException("消息列表不能为空或者null");
        }
        ArrayMsg firstMsg = msgList.getFirst();

        if (Objects.isNull(firstMsg) || firstMsg.getType() != MsgTypeEnum.text) {
            throw new IllegalArgumentException("消息列表的第一个消息不是纯文本消息");
        }
        msgList.set(0, ShiroMsgUtils.toText(ShiroMsgUtils.getText(firstMsg).replaceFirst(prefixRegExp, "")));

        return msgList;
    }

    public static List<ArrayMsg> at(List<ArrayMsg> msgList, long userId) {
        if (Objects.isNull(msgList)) {
            throw new IllegalArgumentException("消息列表不能为null");
        }
        return ShiroMsgUtils.merge(ShiroMsgUtils.merge(ShiroMsgUtils.toAt(userId), ShiroMsgUtils.toText(" ")), msgList);
    }

    public static List<ArrayMsg> reply(List<ArrayMsg> msgList, int messageId) {
        if (Objects.isNull(msgList)) {
            throw new IllegalArgumentException("消息列表不能为null");
        }
        msgList.addFirst(ShiroMsgUtils.toReply(messageId));

        return msgList;
    }

    public static List<ArrayMsg> split(List<ArrayMsg> msgList) {
        if (Objects.isNull(msgList)) {
            throw new IllegalArgumentException("消息列表不能为null");
        }
        return msgList.stream()
                .flatMap(message -> {
                    if (message.getType() == MsgTypeEnum.text) {
                        return Arrays.stream(ShiroMsgUtils.getText(message).split("(?<=\\s)|(?=\\s+)"))
                                .map(token -> token.isBlank() ? WhiteSpace.INSTANCE : ShiroMsgUtils.toText(token));
                    }
                    return Stream.of(message);
                })
                .collect(Collectors.toList());
    }

    public static List<List<ArrayMsg>> group(List<ArrayMsg> msgList) {
        if (Objects.isNull(msgList)) {
            throw new IllegalArgumentException("消息列表不能为null");
        }
        List<List<ArrayMsg>> optionList = new ArrayList<>();
        List<ArrayMsg> currentOption = new ArrayList<>();

        for (ArrayMsg message : msgList) {
            if (message == WhiteSpace.INSTANCE) {
                if (!currentOption.isEmpty()) {
                    optionList.add(currentOption);
                    currentOption = new ArrayList<>();
                }
            } else {
                currentOption.add(message);
            }
        }
        if (!currentOption.isEmpty()) {
            optionList.add(currentOption);
        }
        return optionList;
    }

}
