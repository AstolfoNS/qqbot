package com.timeleafing.qqbot.service.system.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.timeleafing.qqbot.mapper.PermissionMapper;
import com.timeleafing.qqbot.domain.entity.PermissionEntity;
import com.timeleafing.qqbot.service.system.PermissionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, PermissionEntity> implements PermissionService {

    @Override
    public List<PermissionEntity> getPermissionsByUserId(Long userId) {
        return baseMapper.getPermissionsByUserId(userId);
    }

}
