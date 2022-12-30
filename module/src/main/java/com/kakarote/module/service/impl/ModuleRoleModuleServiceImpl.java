package com.kakarote.module.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.common.utils.UserUtil;
import com.kakarote.module.entity.BO.RoleModuleSaveBO;
import com.kakarote.module.entity.PO.ModuleRoleModule;
import com.kakarote.module.mapper.ModuleRoleModuleMapper;
import com.kakarote.module.service.IModuleRoleModuleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zjj
 * @title: ModuleRoleModuleServiceImpl
 * @description: 角色模块关系
 * @date 2021/12/211:30
 */
@Service
public class ModuleRoleModuleServiceImpl extends BaseServiceImpl<ModuleRoleModuleMapper, ModuleRoleModule> implements IModuleRoleModuleService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveRoleModule(RoleModuleSaveBO saveBO) {
        if (ObjectUtil.isNull(saveBO) || ObjectUtil.isNull(saveBO.getRoleId())) {
            return;
        }
        Long roleId = saveBO.getRoleId();
        // 先删除当前角色绑定的模块关系
        lambdaUpdate().eq(ModuleRoleModule::getRoleId, roleId).remove();
        if (CollUtil.isNotEmpty(saveBO.getRoleModules())) {
            List<ModuleRoleModule> roleModuleList = new ArrayList<>();
            for (ModuleRoleModule roleModule : saveBO.getRoleModules()) {
                if (ObjectUtil.isNull(roleModule.getModuleId())) {
                    continue;
                }
                roleModule.setRoleId(roleId);
                roleModule.setCreateTime(DateUtil.date());
                roleModule.setCreateUserId(UserUtil.getUserId());
                roleModuleList.add(roleModule);
            }
            saveBatch(roleModuleList);
        }
    }
}
