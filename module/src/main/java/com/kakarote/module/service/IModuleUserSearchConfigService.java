package com.kakarote.module.service;

import com.kakarote.common.servlet.BaseService;
import com.kakarote.module.entity.PO.ModuleUserSearchConfig;

/**
 * @author zjj
 * @title: IModuleUserSearchConfigService
 * @description: 用戶搜索配置
 * @date 2021/11/2314:20
 */
public interface IModuleUserSearchConfigService extends BaseService<ModuleUserSearchConfig> {

    /**
     * 保存用户搜索配置
     *
     * @param searchConfig
     */
    void saveConfig(ModuleUserSearchConfig searchConfig);

    /**
     * 获取用户的搜索配置
     *
     * @param moduleId
     * @return
     */
    ModuleUserSearchConfig getByModuleIdAndUserId(Long moduleId);
}
