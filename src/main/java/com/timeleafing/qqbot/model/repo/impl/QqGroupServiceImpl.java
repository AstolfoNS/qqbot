package com.timeleafing.qqbot.model.repo.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.timeleafing.qqbot.mapper.QqGroupMapper;
import com.timeleafing.qqbot.model.entity.QqGroupEntity;
import com.timeleafing.qqbot.model.repo.QqGroupService;
import org.springframework.stereotype.Service;

@Service
public class QqGroupServiceImpl extends ServiceImpl<QqGroupMapper, QqGroupEntity> implements QqGroupService {
}
