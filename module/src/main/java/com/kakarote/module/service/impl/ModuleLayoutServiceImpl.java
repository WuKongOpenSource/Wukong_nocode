package com.kakarote.module.service.impl;

import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.module.entity.PO.ModuleLayout;
import com.kakarote.module.mapper.ModuleLayoutMapper;
import com.kakarote.module.service.IModuleLayoutService;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @author: zjj
 * @date: 2021-07-13 15:52
 */
@Service
public class ModuleLayoutServiceImpl extends BaseServiceImpl<ModuleLayoutMapper, ModuleLayout> implements IModuleLayoutService {

    @Override
    public ModuleLayout getByModuleIdAndVersion(Long moduleId, Integer version) {
        return lambdaQuery()
                .eq(ModuleLayout::getModuleId, moduleId)
                .eq(ModuleLayout::getVersion, version).one();
    }
}
