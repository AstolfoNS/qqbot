package com.timeleafing.qqbot.service.system.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.timeleafing.qqbot.mapper.RoleMapper;
import com.timeleafing.qqbot.domain.entity.RoleEntity;
import com.timeleafing.qqbot.service.system.RoleService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, RoleEntity> implements RoleService {

    @Override
    public List<RoleEntity> getRolesByUserId(Long userId) {
        return baseMapper.getRolesByUserId(userId);
    }

}
