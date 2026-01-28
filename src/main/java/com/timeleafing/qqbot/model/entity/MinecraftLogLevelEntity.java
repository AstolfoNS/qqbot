package com.timeleafing.qqbot.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.timeleafing.qqbot.model.entity.base.BaseEntity;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@TableName("minecraft_log_levels")
public class MinecraftLogLevelEntity extends BaseEntity {

    private String code;

    private String name;

    private String description;

}
