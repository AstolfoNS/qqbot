package com.timeleafing.qqbot.model.repo;

import com.baomidou.mybatisplus.extension.service.IService;
import com.timeleafing.qqbot.model.entity.PermissionEntity;

import java.util.List;

public interface PermissionService extends IService<PermissionEntity> {

    List<PermissionEntity> getPermissionsByUserId(Long userId);

}
