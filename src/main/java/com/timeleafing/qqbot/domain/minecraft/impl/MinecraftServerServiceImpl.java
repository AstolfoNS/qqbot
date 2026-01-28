package com.timeleafing.qqbot.domain.minecraft.impl;

import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotContainer;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import com.timeleafing.qqbot.config.properties.MinecraftProperties;
import com.timeleafing.qqbot.config.properties.QqBotProperties;
import com.timeleafing.qqbot.domain.minecraft.MinecraftServerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Service
public class MinecraftServerServiceImpl implements MinecraftServerService {

    private final MinecraftProperties mcProps;

    private final QqBotProperties qqProps;

    private final OkHttpClient okHttpClient;

    private final BotContainer botContainer;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();


    private HttpUrl baseUrl() {
        HttpUrl url = HttpUrl.parse(mcProps.getHttpUri());
        if (url == null) {
            throw new IllegalArgumentException("minecraft.http-uri is invalid: " + mcProps.getHttpUri());
        }
        return url;
    }

    @Override
    public void sendCmd(String command) {
        HttpUrl url = baseUrl().newBuilder()
                .addPathSegment("cmd")
                .build();

        RequestBody formBody = new FormBody.Builder()
                .add("command", command)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException exception) {
                log.error("Failed to send command to Minecraft: {}", exception.getMessage(), exception);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String body = response.body() != null ? response.body().string() : "";
                if (!response.isSuccessful()) {
                    log.error("Minecraft command failed: HTTP {}, body={}", response.code(), body);
                    return;
                }
                log.info("Minecraft cmd ok: {}", body);
            }
        });
    }

    @Override
    public void startServer(Bot bot, AnyMessageEvent event) {
        HttpUrl url = baseUrl().newBuilder()
                .addPathSegment("start")
                .build();

        RequestBody body = RequestBody.create(new byte[0], null);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException exception) {
                log.error("Failed to start minecraft server: {}", exception.getMessage(), exception);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String respBody = response.body() != null ? response.body().string() : "";
                if (!response.isSuccessful()) {
                    log.error("Minecraft server start failed: HTTP {}, body={}", response.code(), respBody);
                    return;
                }
                CompletableFuture.runAsync(() -> bot.sendMsg(event, respBody, false));
            }
        });
    }

    @Override
    public void stopServer(Bot bot, AnyMessageEvent event) {
        HttpUrl url = baseUrl().newBuilder()
                .addPathSegment("stop")
                .build();

        RequestBody body = RequestBody.create(new byte[0], null);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException exception) {
                log.error("Failed to stop minecraft server: {}", exception.getMessage(), exception);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String respBody = response.body() != null ? response.body().string() : "";
                if (!response.isSuccessful()) {
                    log.error("Minecraft server stop failed: HTTP {}, body={}", response.code(), respBody);
                    return;
                }
                CompletableFuture.runAsync(() -> bot.sendMsg(event, respBody, false));
            }
        });
    }

    public void sendBotMsgToGroup(String logInfo) {
        Bot bot = botContainer.robots.get(qqProps.getQqBotId());
        if (bot == null) {
            log.warn("Bot {} not ready, retrying in 5 seconds...", qqProps.getQqBotId());
            scheduler.schedule(() -> sendBotMsgToGroup(logInfo), 5, TimeUnit.SECONDS);
            return;
        }
        try {
            List<Long> qqGroupId = new ArrayList<>();

            qqGroupId.forEach(groupId -> bot.sendGroupMsg(groupId, "[MC] " + logInfo, false));
        } catch (Exception exception) {
            log.error("Failed to send message via Bot: {}", exception.getMessage(), exception);
        }
    }
}
