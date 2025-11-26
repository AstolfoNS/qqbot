package com.timeleafing.qqbot.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.timeleafing.qqbot.domain.enumeration.PermType;
import com.timeleafing.qqbot.domain.entity.base.BaseEntity;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@TableName("permission")
public class PermissionEntity extends BaseEntity {

    private String code;

    private String name;

    private PermType type;

    private String description;

    private Integer sortOrder;

}
