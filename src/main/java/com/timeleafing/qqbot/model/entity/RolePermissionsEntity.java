package com.timeleafing.qqbot.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.timeleafing.qqbot.model.entity.base.BaseEntity;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@TableName("role_permissions")
public class RolePermissionsEntity extends BaseEntity {

    private Long roleId;

    private Long permissionId;

}
