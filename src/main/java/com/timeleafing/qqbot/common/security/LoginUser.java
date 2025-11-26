package com.timeleafing.qqbot.common.security;

import com.timeleafing.qqbot.domain.entity.PermissionEntity;
import com.timeleafing.qqbot.domain.entity.RoleEntity;
import com.timeleafing.qqbot.domain.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginUser implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    private String username;

    private String password;

    private String qqId;

    private String qqEmail;

    private List<String> roles;

    private List<String> permissions;


    public static LoginUser from(UserEntity user, List<RoleEntity> roles, List<PermissionEntity> permissions) {
        return builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .qqId(user.getQqId())
                .qqEmail(user.getQqEmail())
                .roles(roles.stream().map(RoleEntity::getCode).toList())
                .permissions(permissions.stream().map(PermissionEntity::getCode).toList())
                .build();
    }

}
