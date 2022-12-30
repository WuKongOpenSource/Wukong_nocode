package com.kakarote.module.service;

import com.kakarote.common.servlet.BaseService;
import com.kakarote.module.entity.BO.RoleModuleSaveBO;
import com.kakarote.module.entity.PO.ModuleRoleModule;

/**
 * @author zjj
 * @title: IModuleRoleModuleService
 * @description: 角色模块关系
 * @date 2021/12/211:30
 */
public interface IModuleRoleModuleService extends BaseService<ModuleRoleModule> {

    /**
     * 保存角色模块关系
     *
     * @param saveBO
     */
    void saveRoleModule(RoleModuleSaveBO saveBO);
}
