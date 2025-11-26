package com.timeleafing.qqbot.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.timeleafing.qqbot.domain.entity.base.BaseEntity;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@TableName("user_roles")
public class UserRolesEntity extends BaseEntity {

    private Long userId;

    private Long roleId;

}
