package com.timeleafing.qqbot.handler;

import com.timeleafing.qqbot.config.properties.MinecraftProperties;
import com.timeleafing.qqbot.domain.minecraft.MinecraftServerService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RequiredArgsConstructor
@Component
public class MinecraftLogHandler {

    private final MinecraftProperties mcProps;

    private final MinecraftServerService minecraftServerService;

    private final BlockingQueue<MinecraftLog> messageQueue = new LinkedBlockingQueue<>(20);

    private static final int MAX_TOKENS = 10;

    private static final long REFILL_INTERVAL_MS = 100;

    private final AtomicInteger tokenBucket = new AtomicInteger(MAX_TOKENS);

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    private final ExecutorService senderExecutor = Executors.newSingleThreadExecutor();

    private static final Pattern LOG_PATTERN = Pattern.compile(
            "\\[(?<thread>[^/]+)/(?<level>INFO|WARN|ERROR|DEBUG|TRACE)]\\s+\\[(?<module>[^]]+)]:\\s+(?<msg>.+)"
    );

    private final Set<String> allowedLevels = Set.of("INFO", "ERROR");


    @EventListener(ApplicationReadyEvent.class)
    public void connectAfterStartup() {
        connect();

        startTokenRefill();   // 启动限速器
    }

    private void connect() {
        try {
            CompletableFuture<WebSocketSession> sessionFuture = createSession();

            sessionFuture
                    .thenAccept(session -> log.info("Minecraft WebSocket connected: {}", session.getId()))
                    .exceptionally(ex -> {
                        log.error("Failed to connect Minecraft WebSocket", ex);

                        return null;
                    });
        } catch (Exception ex) {
            log.error("Error initializing Minecraft client", ex);
        }
    }

    private CompletableFuture<WebSocketSession> createSession() {
        StandardWebSocketClient client = new StandardWebSocketClient();

        return client.execute(new TextWebSocketHandler() {
            @Override
            protected void handleTextMessage(@NotNull WebSocketSession session, @NotNull TextMessage message) {
                String payload = message.getPayload();
                Matcher matcher = LOG_PATTERN.matcher(payload);

                // 无法识别的日志直接丢弃
                if (!matcher.find()) {
                    return;
                }
                MinecraftLog logObj = new MinecraftLog(
                        matcher.group("thread"),
                        matcher.group("level"),
                        matcher.group("module"),
                        matcher.group("msg")
                );
                // 抛弃不允许的日志级别
                if (!allowedLevels.contains(logObj.level)) {
                    return;
                }
                // 队列满 → 删除最旧再加入新的 (保证最新日志入队)
                while (!messageQueue.offer(logObj)) {
                    messageQueue.poll();
                }
                log.debug("Queue full, dropped oldest log. New log inserted: {}", logObj);
            }
        }, mcProps.getWsUri());
    }

    @PostConstruct
    public void startConsumer() {
        Thread consumer = new Thread(() -> {
            while (true) {
                try {
                    MinecraftLog logObj = messageQueue.take();
                    senderExecutor.submit(() -> sendWithRateLimit(logObj));
                } catch (Exception ex) {
                    log.error("Minecraft consumer error", ex);
                }
            }
        }, "minecraft-log-consumer");

        consumer.setDaemon(true);
        consumer.start();
    }

    private void sendWithRateLimit(MinecraftLog logObj) {
        acquireToken();

        sendToQQ(logObj);
    }

    private void acquireToken() {
        try {
            while (true) {
                int current = tokenBucket.get();

                if (current > 0) {
                    if (tokenBucket.compareAndSet(current, current - 1)) {
                        return;
                    }
                } else {
                    synchronized (tokenBucket) {
                        tokenBucket.wait();
                    }
                }
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    private void startTokenRefill() {
        scheduler.scheduleAtFixedRate(() -> {
            int old;
            do {
                old = tokenBucket.get();

                if (old >= MAX_TOKENS) {
                    return;
                }
            } while (!tokenBucket.compareAndSet(old, old + 1));

            synchronized (tokenBucket) {
                tokenBucket.notifyAll();
            }
        }, 0, REFILL_INTERVAL_MS, TimeUnit.MILLISECONDS);
    }

    private void sendToQQ(MinecraftLog logObj) {
        try {
            minecraftServerService.sendBotMsgToGroup("[%s] %s".formatted(logObj.level, logObj.msg));
        } catch (Exception e) {
            log.error("Failed to send QQ bot message", e);
        }
    }

    private record MinecraftLog(String thread, String level, String module, String msg) {
        @NotNull
        @Override
        public String toString() {
            return "[" + thread + "/" + level + "][" + module + "]: " + msg;
        }
    }
}
