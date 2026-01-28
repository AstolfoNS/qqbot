package com.timeleafing.qqbot.model.repo.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.timeleafing.qqbot.mapper.UserRoleMapper;
import com.timeleafing.qqbot.model.entity.UserRoleEntity;
import com.timeleafing.qqbot.model.repo.UserRoleService;
import org.springframework.stereotype.Service;

@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRoleEntity> implements UserRoleService {
}
