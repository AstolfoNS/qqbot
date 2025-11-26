package com.timeleafing.qqbot.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.timeleafing.qqbot.domain.enumeration.Gender;
import com.timeleafing.qqbot.domain.entity.base.BaseEntity;
import lombok.*;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@TableName("user")
public class UserEntity extends BaseEntity {

    private String qqEmail;

    private String qqId;

    private String username;

    private String password;

    private String avatarUrl;

    private Gender gender;

    private String introduction;

    private LocalDateTime lastLoginTime;

}
