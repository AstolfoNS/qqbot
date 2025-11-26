package com.timeleafing.qqbot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.timeleafing.qqbot.domain.entity.RoleEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RoleMapper extends BaseMapper<RoleEntity> {

    List<RoleEntity> getRolesByUserId(Long userId);

}
