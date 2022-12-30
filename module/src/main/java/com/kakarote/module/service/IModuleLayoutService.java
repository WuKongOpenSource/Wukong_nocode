package com.kakarote.module.service;

import com.kakarote.common.servlet.BaseService;
import com.kakarote.module.entity.PO.ModuleLayout;

/**
 * @description:
 * @author: zjj
 * @date: 2021-07-13 15:51
 */
public interface IModuleLayoutService extends BaseService<ModuleLayout> {

    /**
     * 获取模块的布局
     *
     * @param moduleId
     * @param version
     * @return
     */
    ModuleLayout getByModuleIdAndVersion(Long moduleId, Integer version);
}
