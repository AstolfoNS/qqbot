package com.timeleafing.qqbot.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.timeleafing.qqbot.common.enumeration.Gender;
import com.timeleafing.qqbot.model.entity.base.BaseEntity;
import com.timeleafing.qqbot.model.entity.base.BaseFields;
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

    private Long qqId;

    private String username;

    private String nickname;

    private String password;

    private String avatarUrl;

    private Gender gender;

    private String introduction;

    private LocalDateTime lastLoginTime;

}
