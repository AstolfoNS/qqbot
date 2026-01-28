package com.timeleafing.qqbot.service.system;

import com.baomidou.mybatisplus.extension.service.IService;
import com.timeleafing.qqbot.model.entity.RoleEntity;

import java.util.List;

public interface RoleService extends IService<RoleEntity> {

    List<RoleEntity> getRolesByUserId(Long userId);

}
