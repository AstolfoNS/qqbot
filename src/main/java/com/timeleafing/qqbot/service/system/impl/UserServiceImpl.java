package com.timeleafing.qqbot.service.system.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.timeleafing.qqbot.mapper.UserMapper;
import com.timeleafing.qqbot.domain.entity.UserEntity;
import com.timeleafing.qqbot.service.system.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements UserService {
}
