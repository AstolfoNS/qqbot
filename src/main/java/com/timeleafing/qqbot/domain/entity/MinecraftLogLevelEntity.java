package com.timeleafing.qqbot.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.timeleafing.qqbot.domain.entity.base.BaseEntity;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@TableName("minecraft_log_level")
public class MinecraftLogLevelEntity extends BaseEntity {

    private String code;

    private String name;

    private String description;

    private Boolean isEnabled;

}
