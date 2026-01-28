package com.timeleafing.qqbot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.timeleafing.qqbot.model.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {
}
