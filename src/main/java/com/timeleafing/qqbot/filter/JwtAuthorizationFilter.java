package com.timeleafing.qqbot.filter;

import com.timeleafing.qqbot.common.constant.HttpStatusConst;
import com.timeleafing.qqbot.service.system.AuthenticationService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private static final String AUTH_HEADER_START = "Bearer ";

    private final AuthenticationService authenticationService;


    @Override
    protected void doFilterInternal(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull FilterChain filterChain
    ) throws ServletException, IOException {
        // 判断当前 SecurityContext 中是否已经存在 Authentication
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);

            return;
        }
        // 从 request 中解析出 token
        String token = extractToken(request);
        // 判断 token 是否为空
        if (token == null) {
            filterChain.doFilter(request, response);

            return;
        }
        try {
            Authentication authentication = authenticationService.buildAuthenticationFor(token);
            // 判断构建出的 authentication 是否为空
            if (authentication != null) {
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            log.error("构建 Authentication 失败：{}", e.getMessage(), e);
            // 清除 SecurityContext
            SecurityContextHolder.clearContext();
            // 返回错误回复
            response.sendError(HttpStatusConst.INTERNAL_SERVER_ERROR, "认证服务异常");

            return;
        }
        filterChain.doFilter(request, response);
    }

    @Nullable
    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith(AUTH_HEADER_START)) {
            return authHeader.substring(AUTH_HEADER_START.length());
        }
        return null;
    }
}
