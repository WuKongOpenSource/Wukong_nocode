package com.kakarote.module.service;

import com.kakarote.module.entity.PO.ModuleEntity;

import java.util.List;
import java.util.Set;

/**
 * @author : zjj
 * @since : 2022/12/27
 */
public interface IModuleProvider {

    /**
     * 获取模块关联的模块
     *
     * @param moduleId 模块id
     * @param version 版本号
     * @param filterMulti boolean
     * @return 模块信息list
     */
    List<ModuleEntity> getUnionModules(Long moduleId, Integer version, Boolean filterMulti);

    /**
     * 获取模块关联的模块ID
     *
     * @param moduleId  模块 ID
     * @return
     */
    Set<Long> getUnionModuleIds(Long moduleId);
}
