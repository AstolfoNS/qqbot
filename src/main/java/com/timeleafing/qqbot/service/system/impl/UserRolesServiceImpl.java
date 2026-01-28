package com.timeleafing.qqbot.service.system.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.timeleafing.qqbot.mapper.UserRolesMapper;
import com.timeleafing.qqbot.model.entity.UserRolesEntity;
import com.timeleafing.qqbot.service.system.UserRolesService;
import org.springframework.stereotype.Service;

@Service
public class UserRolesServiceImpl extends ServiceImpl<UserRolesMapper, UserRolesEntity> implements UserRolesService {
}
