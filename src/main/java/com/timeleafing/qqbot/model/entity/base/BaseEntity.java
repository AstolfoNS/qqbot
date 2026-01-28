package com.timeleafing.qqbot.model.entity.base;

import com.baomidou.mybatisplus.annotation.*;
import com.timeleafing.qqbot.common.enumeration.Status;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public abstract class BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID - 使用唯一自增ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 创建时间 - 插入时自动填充
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间 - 插入和更新时自动填充
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 创建者ID - 插入时自动填充
     * 默认值0表示系统操作
     */
    @TableField(fill = FieldFill.INSERT)
    private Long createBy;

    /**
     * 更新者ID - 插入和更新时自动填充
     * 默认值0表示系统操作
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateBy;

    /**
     * 逻辑删除标记
     * 0-未删除（默认）
     * 1-已删除
     */
    @TableLogic(value = "0", delval = "1")
    private Boolean isDeleted;

    /**
     * 乐观锁版本号
     * 每次更新时自动递增
     */
    @Version
    private Integer version;

    /**
     * 数据状态字段
     * 0-禁用
     * 1-正常（默认）
     * 2-锁定
     */
    private Status status;

}
