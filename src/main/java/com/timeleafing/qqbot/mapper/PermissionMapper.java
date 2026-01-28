package com.timeleafing.qqbot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.timeleafing.qqbot.model.entity.PermissionEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PermissionMapper extends BaseMapper<PermissionEntity> {

    List<PermissionEntity> getPermissionsByUserId(Long userId);

}
