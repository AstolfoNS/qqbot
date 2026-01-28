package com.timeleafing.qqbot.interceptor;

import com.timeleafing.qqbot.config.properties.HmacAuthProperties;
import lombok.RequiredArgsConstructor;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.List;

@RequiredArgsConstructor
@Component
public class HmacAuthInterceptor implements Interceptor {

    private final HmacAuthProperties props;

    private final SecureRandom random = new SecureRandom();

    private static final int NONCE_BYTES = 16;


    @Override
    public @NonNull Response intercept(Chain chain) throws IOException {
        Request req = chain.request();

        // 可选：host 白名单限制（防止误给其他 API 加签名头）
        if (!shouldSign(req.url())) {
            return chain.proceed(req);
        }

        String secret = props.getHmacSecret();
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("HMAC secret is empty (security.hmac.secret)");
        }
        // 秒级时间戳
        String ts = String.valueOf(Instant.now().getEpochSecond());
        // nonce
        String nonce = randomHex();
        // 只取 path（不含 query、不含 host）
        HttpUrl url = req.url();
        String path = url.encodedPath(); // e.g. "/api/minecraft/cmd"
        // canonical: METHOD\nPATH\nTS\nNONCE
        String method = req.method().toUpperCase();
        String canonical = method + "\n" + path + "\n" + ts + "\n" + nonce;

        String sign = hmacBase64(secret, canonical);

        Request signed = req
                .newBuilder()
                .header(props.getHeaderTs(), ts)
                .header(props.getHeaderNonce(), nonce)
                .header(props.getHeaderSign(), sign)
                .build();

        return chain.proceed(signed);
    }

    private boolean shouldSign(HttpUrl url) {
        List<String> allowed = props.getAllowedHosts();

        if (allowed == null || allowed.isEmpty()) {
            return true;
        }
        return allowed.contains(url.host());
    }

    private static String hmacBase64(String secret, String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] raw = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));

            return Base64.getEncoder().encodeToString(raw);
        } catch (Exception e) {
            throw new RuntimeException("HMAC calc failed", e);
        }
    }

    private String randomHex() {
        byte[] buf = new byte[NONCE_BYTES];
        random.nextBytes(buf);
        StringBuilder sb = new StringBuilder(NONCE_BYTES * 2);
        for (byte b : buf) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
