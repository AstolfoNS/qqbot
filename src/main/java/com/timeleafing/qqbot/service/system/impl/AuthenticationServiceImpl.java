package com.timeleafing.qqbot.service.system.impl;

import com.timeleafing.qqbot.common.security.AuthenticationToken;
import com.timeleafing.qqbot.common.security.LoginUser;
import com.timeleafing.qqbot.common.util.JwtUtils;
import com.timeleafing.qqbot.common.util.RedisUtils;
import com.timeleafing.qqbot.model.entity.PermissionEntity;
import com.timeleafing.qqbot.model.entity.RoleEntity;
import com.timeleafing.qqbot.model.entity.UserEntity;
import com.timeleafing.qqbot.exception.TokenAuthenticationException;
import com.timeleafing.qqbot.service.system.AuthenticationService;
import com.timeleafing.qqbot.service.system.PermissionService;
import com.timeleafing.qqbot.service.system.RoleService;
import com.timeleafing.qqbot.service.system.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.function.Supplier;

@RequiredArgsConstructor
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserService userService;

    private final RoleService roleService;

    private final PermissionService permissionService;

    private final JwtUtils jwtUtils;

    private final RedisUtils redisUtils;


    @Override
    public Authentication buildAuthenticationFor(String token) throws TokenAuthenticationException {
        // 校验认证 token 是否为空
        if (!StringUtils.hasText(token)) {
            throw new TokenAuthenticationException("认证 token 不能为空");
        }
        // 从 jwt 中解析出 userId
        Long userId = jwtUtils.getUserId(token);
        // 通过 userId 构建 loginUser
        LoginUser loginUser = getOrLoadFromRedis(userId.toString(), () -> loadLoginUserFromDB(userId));

        return new AuthenticationToken(loginUser, token);
    }

    private LoginUser getOrLoadFromRedis(String key, Supplier<LoginUser> loader) {
        // 从 redis 中查询 loginUser
        LoginUser cached = redisUtils.get(key, LoginUser.class);
        // 判断 cached 是否为空
        if (cached != null) {
            return cached;
        }
        // 从数据库中查询数据并构建 loginUser
        LoginUser loaded = loader.get();
        // 保存在 redis 中
        redisUtils.set(key, loaded);

        return loaded;
    }

    private LoginUser loadLoginUserFromDB(Long userId) {
        // 通过 userId 查询当前用户的详细信息
        UserEntity user = userService.getOptById(userId).orElseThrow(
                () -> new IllegalArgumentException("userId %d 的用户不存在".formatted(userId))
        );
        // 通过 userId 查询当前用户拥有的角色
        List<RoleEntity> roles = roleService.getRolesByUserId(userId);
        // 通过 userId 查询当前用户拥有的权限
        List<PermissionEntity> permissions = permissionService.getPermissionsByUserId(userId);

        return LoginUser.from(user, roles, permissions);
    }
}
