package com.timeleafing.qqbot.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.timeleafing.qqbot.model.entity.base.BaseFields;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@TableName("qq_group_permissions")
public class QqGroupPermissionEntity extends BaseFields {

    private Long qqGroupId;

    private Long permissionId;

}
