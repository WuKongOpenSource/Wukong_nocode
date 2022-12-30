package com.kakarote.module.service;

import com.kakarote.common.servlet.BaseService;
import com.kakarote.module.entity.PO.ModuleStatus;

import java.util.List;
import java.util.Set;

/**
 * @author zjj
 * @title: IModuleVersionService
 * @description: 模块版本
 * @date 2021/12/1413:23
 */
public interface IModuleStatusService extends BaseService<ModuleStatus> {

    void updateStatus(Long moduleId, Boolean isEnable);

    ModuleStatus getStatus(Long moduleId);

    List<ModuleStatus> listByModuleId(Set<Long> moduleIds);

}
