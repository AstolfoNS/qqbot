package com.timeleafing.qqbot.model.repo.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.timeleafing.qqbot.mapper.MinecraftLogLevelMapper;
import com.timeleafing.qqbot.model.entity.MinecraftLogLevelEntity;
import com.timeleafing.qqbot.model.repo.MinecraftLogLevelService;
import org.springframework.stereotype.Service;

@Service
public class MinecraftLogLevelServiceImpl extends ServiceImpl<MinecraftLogLevelMapper, MinecraftLogLevelEntity> implements MinecraftLogLevelService {
}
