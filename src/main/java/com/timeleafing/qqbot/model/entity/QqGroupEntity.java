package com.timeleafing.qqbot.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.timeleafing.qqbot.model.entity.base.BaseEntity;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@TableName("qq_groups")
public class QqGroupEntity extends BaseEntity {

    private  Long qqGroupId;

    private String name;

    private String description;

}
