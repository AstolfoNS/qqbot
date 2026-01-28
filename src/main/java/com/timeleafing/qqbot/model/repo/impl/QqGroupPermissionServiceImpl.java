package com.timeleafing.qqbot.model.repo.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.timeleafing.qqbot.mapper.QqGroupPermissionMapper;
import com.timeleafing.qqbot.model.entity.QqGroupPermissionEntity;
import com.timeleafing.qqbot.model.repo.QqGroupPermissionService;
import org.springframework.stereotype.Service;

@Service
public class QqGroupPermissionServiceImpl extends ServiceImpl<QqGroupPermissionMapper, QqGroupPermissionEntity> implements QqGroupPermissionService {
}
