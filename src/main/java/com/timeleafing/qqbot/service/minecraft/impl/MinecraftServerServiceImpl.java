package com.timeleafing.qqbot.service.minecraft.impl;

import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotContainer;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import com.timeleafing.qqbot.service.minecraft.MinecraftServerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Service
public class MinecraftServerServiceImpl implements MinecraftServerService {

    @Value("#{minecraftProperties.httpUri}")
    private String minecraftHttpUri;

    @Value("#{qqBotProperties.qqBotId}")
    private Long qqBotId;

    @Value("#{minecraftProperties.qqGroupIdList}")
    private List<Long> qqGroupIdList;

    private final OkHttpClient okHttpClient;

    private final BotContainer botContainer;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();


    @Override
    public void sendMinecraftCommand(String command) {
        String url = minecraftHttpUri.concat("/minecraft/command");

        RequestBody formBody = new FormBody
                .Builder()
                .add("command", command)
                .build();

        Request request = new Request
                .Builder()
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
                if (!response.isSuccessful()) {
                    log.error("Minecraft command failed: HTTP {}", response.code());

                    return;
                }
                String body = response.body() != null ? response.body().string() : "None.";
                CompletableFuture.runAsync(() -> log.info(body));
            }
        });
    }

    @Override
    public void startMinecraftServer(Bot bot, AnyMessageEvent event) {
        String url = minecraftHttpUri.concat("/minecraft/start");

        RequestBody body = RequestBody.create("", null);

        Request request = new Request
                .Builder()
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
                if (!response.isSuccessful()) {
                    log.error("Minecraft server start failed: HTTP {}", response.code());

                    return;
                }
                String body = response.body() != null ? response.body().string() : "None.";
                CompletableFuture.runAsync(() -> bot.sendMsg(event, body, false));
            }
        });
    }

    @Override
    public void stopMinecraftServer(Bot bot, AnyMessageEvent event) {
        String url = minecraftHttpUri.concat("/minecraft/stop");

        RequestBody body = RequestBody.create("", null);

        Request request = new Request
                .Builder()
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
                if (!response.isSuccessful()) {
                    log.error("Minecraft server stop failed: HTTP {}", response.code());

                    return;
                }
                String body = response.body() != null ? response.body().string() : "None.";
                CompletableFuture.runAsync(() -> bot.sendMsg(event, body, false));
            }
        });
    }

    public void sendBotMessage(String logInfo) {
        Bot bot = botContainer.robots.get(qqBotId);
        if (bot == null) {
            log.warn("Bot {} not ready, retrying in 5 seconds...", qqBotId);

            scheduler.schedule(() -> sendBotMessage(logInfo), 5, TimeUnit.SECONDS);

            return;
        }
        try {
            qqGroupIdList.forEach(groupId -> bot.sendGroupMsg(groupId, "[MC] " + logInfo, false));
        } catch (Exception exception) {
            log.error("Failed to send message via Bot: {}", exception.getMessage(), exception);
        }
    }

}
