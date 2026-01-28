package com.timeleafing.qqbot.model.repo.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.timeleafing.qqbot.mapper.RolePermissionMapper;
import com.timeleafing.qqbot.model.entity.RolePermissionEntity;
import com.timeleafing.qqbot.model.repo.RolePermissionService;
import org.springframework.stereotype.Service;

@Service
public class RolePermissionServiceImpl extends ServiceImpl<RolePermissionMapper, RolePermissionEntity> implements RolePermissionService {
}
