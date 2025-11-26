package com.timeleafing.qqbot.common.util;

import com.timeleafing.qqbot.common.security.LoginUser;
import com.timeleafing.qqbot.exception.JwtGenerateException;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Instant;

@Slf4j
@Data
@RequiredArgsConstructor
@Component
public class JwtUtils {

    private final JwtEncoder jwtEncoder;

    private final JwtDecoder jwtDecoder;

    @Value("#{jwtProperties.expire}")
    private Long expire;

    @Value("#{jwtProperties.issuer}")
    private String issuer;

    @Value("#{jwtProperties.jwtAlgorithm}")
    private MacAlgorithm jwtAlgorithm;


    public String generateToken(LoginUser loginUser) {
        // loginUser 和 loginUser.getId() 参数校验
        if (loginUser == null || loginUser.getId() == null) {
            throw new IllegalArgumentException("LoginUser 或者 LoginUser.getId() 不能为 null");
        }
        return generateToken(loginUser.getId());
    }

    /**
     * 根据 UserId 生成 Token (使用默认过期时间)
     */
    public String generateToken(Long userId) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet
                .builder()
                .subject(userId.toString())
                .issuedAt(now)
                .issuer(issuer)
                .expiresAt(now.plusMillis(expire))
                .build();

        JwsHeader jwsHeader = JwsHeader.with(jwtAlgorithm).build();

        try {
            return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
        } catch (JwtEncodingException e) {
            throw new JwtGenerateException("生成 JWT token 失败");
        }
    }

    /**
     * 解析 Token
     * 如果 Token 过期或签名无效，jwtDecoder 会直接抛出 JwtValidationException
     */
    public Jwt decode(String token) {
        // 判断 token 是否为空
        if (!StringUtils.hasText(token)) {
            throw new JwtException("token 不能为空");
        }
        return jwtDecoder.decode(token);
    }

    /**
     * 从 Jwt 对象中获取用户 ID (Long 类型)
     */
    public Long getUserId(Jwt jwt) {
        // 判断 jwt 是否为空
        if (jwt == null) {
            return null;
        }
        try {
            return Long.parseLong(jwt.getSubject());
        } catch (NumberFormatException e) {
            log.error("无法从 Token 中解析出 User ID: {}", jwt.getSubject());

            return null;
        }
    }

    /**
     * 从 Jwt Token 中获取用户 ID（Long 类型）
     */
     public Long getUserId(String token) {
        return getUserId(decode(token));
    }
}
