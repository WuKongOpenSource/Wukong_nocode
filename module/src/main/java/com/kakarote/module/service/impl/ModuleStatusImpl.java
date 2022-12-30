package com.kakarote.module.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.module.entity.PO.ModuleStatus;
import com.kakarote.module.mapper.ModuleStatusMapper;
import com.kakarote.module.service.IModuleStatusService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author zjj
 * @title: ModuleStatusImpl
 * @description: 模块状态
 * @date 2021/12/1413:23
 */
@Service
public class ModuleStatusImpl extends BaseServiceImpl<ModuleStatusMapper, ModuleStatus> implements IModuleStatusService {

    @Override
    public void updateStatus(Long moduleId, Boolean isEnable) {
        ModuleStatus moduleStatus = this.getStatus(moduleId);
        moduleStatus.setIsEnable(isEnable);
        updateById(moduleStatus);
    }

    @Override
    public ModuleStatus getStatus(Long moduleId) {
        ModuleStatus moduleStatus = lambdaQuery().eq(ModuleStatus::getModuleId, moduleId).one();
        if (ObjectUtil.isNull(moduleStatus)) {
            moduleStatus = new ModuleStatus();
            moduleStatus.setCreateTime(DateUtil.date());
            moduleStatus.setModuleId(moduleId);
            moduleStatus.setIsEnable(true);
            save(moduleStatus);
        }
        return moduleStatus;
    }

    @Override
    public List<ModuleStatus> listByModuleId(Set<Long> moduleIds) {
        if (CollUtil.isEmpty(moduleIds)) {
            return Collections.EMPTY_LIST;
        }
        return lambdaQuery().in(ModuleStatus::getModuleId, moduleIds).list();
    }
}
