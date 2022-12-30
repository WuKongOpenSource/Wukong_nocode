package com.kakarote.module.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.kakarote.common.servlet.ApplicationContextHolder;
import com.kakarote.module.common.ModuleCacheUtil;
import com.kakarote.module.entity.PO.ModuleEntity;
import com.kakarote.module.entity.PO.ModuleField;
import com.kakarote.module.entity.PO.ModuleFieldUnion;
import com.kakarote.module.entity.PO.ModuleStatisticFieldUnion;
import com.kakarote.module.mapper.ModuleMapper;
import com.kakarote.module.service.IModuleFieldService;
import com.kakarote.module.service.IModuleFieldUnionService;
import com.kakarote.module.service.IModuleProvider;
import com.kakarote.module.service.IModuleStatisticFieldUnionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author : zjj
 * @since : 2022/12/27
 */
@Service
public class ModuleProviderImpl implements IModuleProvider {

    @Autowired
    private ModuleMapper moduleMapper;

    @Autowired
    private IModuleFieldService fieldService;

    @Autowired
    private IModuleFieldUnionService fieldUnionService;

    @Autowired
    private IModuleStatisticFieldUnionService statisticFieldUnionService;

    @Override
    public List<ModuleEntity> getUnionModules(Long moduleId, Integer version, Boolean filterMulti) {
        if (filterMulti) {
            List<ModuleFieldUnion> fieldUnions = fieldUnionService
                    .lambdaQuery()
                    .eq(ModuleFieldUnion::getTargetModuleId, moduleId)
                    .eq(ModuleFieldUnion::getType, 1).list();
            Set<Long> moduleIdSet = fieldUnions.stream().map(ModuleFieldUnion::getModuleId).collect(Collectors.toSet());
            List<ModuleEntity> modules = ModuleCacheUtil.getActiveByIds(moduleIdSet);
            Map<Long, ModuleEntity> moduleIdVersionMap = modules.stream().collect(Collectors.toMap(ModuleEntity::getModuleId, Function.identity()));
            Set<Long> moduleIds = fieldUnions.stream().filter(f -> {
                ModuleEntity module = moduleIdVersionMap.get(f.getModuleId());
                if (ObjectUtil.isNotNull(module)) {
                    if (ObjectUtil.equal(module.getVersion(), f.getVersion())) {
                        // 明细表格内字段
                        List<ModuleField> fieldsInTable = fieldService.lambdaQuery()
                                .eq(ModuleField::getModuleId, module.getModuleId())
                                .eq(ModuleField::getVersion, module.getVersion())
                                .isNotNull(ModuleField::getGroupId)
                                .list();
                        List<Long> fieldIds = fieldsInTable.stream().map(ModuleField::getFieldId).collect(Collectors.toList());
                        if (CollUtil.isEmpty(fieldIds)) {
                            return true;
                        } else {
                            return !CollUtil.contains(fieldIds, f.getRelateFieldId());
                        }
                    }
                }
                return false;
            }).map(ModuleFieldUnion::getModuleId).collect(Collectors.toSet());
            return ModuleCacheUtil.getActiveByIds(moduleIds);
        }
        return moduleMapper.selectUnionModulesByModuleId(moduleId, version);
    }

    @Override
    public Set<Long> getUnionModuleIds(Long moduleId) {
        Set<Long> result = new HashSet<>();
        result.add(moduleId);
        ModuleEntity module = ModuleCacheUtil.getActiveById(moduleId);
        List<ModuleEntity> modules = this.getUnionModules(module.getModuleId(), module.getVersion(), false);
        List<ModuleStatisticFieldUnion> fieldUnionList = statisticFieldUnionService.lambdaQuery()
                .eq(ModuleStatisticFieldUnion::getTargetModuleId, moduleId)
                .or(i ->
                        i.eq(ModuleStatisticFieldUnion::getModuleId, moduleId)
                                .eq(ModuleStatisticFieldUnion::getVersion, module.getVersion())
                ).list();
        result.addAll(modules.stream().map(ModuleEntity::getModuleId).collect(Collectors.toSet()));
        for (ModuleStatisticFieldUnion union : fieldUnionList) {
            result.add(union.getModuleId());
            result.add(union.getTargetModuleId());
        }
        return result;
    }
}
