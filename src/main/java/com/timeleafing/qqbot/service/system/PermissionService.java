package com.timeleafing.qqbot.service.system;

import com.baomidou.mybatisplus.extension.service.IService;
import com.timeleafing.qqbot.domain.entity.PermissionEntity;

import java.util.List;

public interface PermissionService extends IService<PermissionEntity> {

    List<PermissionEntity> getPermissionsByUserId(Long userId);

}
