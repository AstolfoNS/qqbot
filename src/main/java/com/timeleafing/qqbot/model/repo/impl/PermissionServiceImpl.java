package com.timeleafing.qqbot.model.repo.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.timeleafing.qqbot.mapper.PermissionMapper;
import com.timeleafing.qqbot.model.entity.PermissionEntity;
import com.timeleafing.qqbot.model.repo.PermissionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, PermissionEntity> implements PermissionService {

    @Override
    public List<PermissionEntity> getPermissionsByUserId(Long userId) {
        return baseMapper.getPermissionsByUserId(userId);
    }

}
