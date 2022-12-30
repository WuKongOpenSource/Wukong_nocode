package com.kakarote.module.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.common.utils.UserUtil;
import com.kakarote.module.entity.BO.RoleUserSaveBO;
import com.kakarote.module.entity.PO.ModuleEntity;
import com.kakarote.module.entity.PO.ModuleRoleUser;
import com.kakarote.module.mapper.ModuleRoleUserMapper;
import com.kakarote.module.service.IModuleRoleUserService;
import com.kakarote.module.service.IModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author zjj
 * @title: ModuleRoleUserServiceImpl
 * @description: 角色用户关系
 * @date 2021/12/213:10
 */
@Service
public class ModuleRoleUserServiceImpl extends BaseServiceImpl<ModuleRoleUserMapper, ModuleRoleUser> implements IModuleRoleUserService {

    @Autowired
    private IModuleService moduleService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveRoleUser(RoleUserSaveBO saveBO) {
        if (ObjectUtil.isNull(saveBO)
                || ObjectUtil.isNull(saveBO.getRoleId())
                || ObjectUtil.isNull(saveBO.getApplicationId())){
            return;
        }
        // 先删除当前角色绑定的用户
        lambdaUpdate().eq(ModuleRoleUser::getRoleId, saveBO.getRoleId()).remove();
        if (CollUtil.isNotEmpty(saveBO.getUserIds())) {
            List<ModuleRoleUser> roleUserList = new ArrayList<>();
            for (Long userId : saveBO.getUserIds()) {
                ModuleRoleUser roleUser = new ModuleRoleUser();
                roleUser.setRoleId(saveBO.getRoleId());
                roleUser.setUserId(userId);
                roleUser.setApplicationId(saveBO.getApplicationId());
                roleUserList.add(roleUser);
            }
            saveBatch(roleUserList);
        }
    }

    @Override
    public List<Map<String, String>> getUserAuth(Long userId) {
        return getBaseMapper().getUserAuth(userId);
    }

    @Override
    public List<Map<String, String>> getUserFieldAuth(Long userId, Long moduleId) {
        ModuleEntity module = moduleService.getNormal(moduleId);
        return getBaseMapper().getUserFieldAuth(userId, moduleId, module.getVersion());
    }

    @Override
    public List<ModuleRoleUser> getByUserId(Long userId) {
        return lambdaQuery().eq(ModuleRoleUser::getUserId, userId).list();
    }

    @Override
    public List<ModuleRoleUser> getByRoleId(Long roleId) {
        return lambdaQuery().eq(ModuleRoleUser::getRoleId, roleId).list();
    }
}
