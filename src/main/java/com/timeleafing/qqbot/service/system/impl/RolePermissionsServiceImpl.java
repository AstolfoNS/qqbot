package com.timeleafing.qqbot.service.system.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.timeleafing.qqbot.mapper.RolePermissionsMapper;
import com.timeleafing.qqbot.domain.entity.RolePermissionsEntity;
import com.timeleafing.qqbot.service.system.RolePermissionsService;
import org.springframework.stereotype.Service;

@Service
public class RolePermissionsServiceImpl extends ServiceImpl<RolePermissionsMapper, RolePermissionsEntity> implements RolePermissionsService {
}
