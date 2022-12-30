package com.kakarote.module.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.kakarote.module.constant.ModuleFieldEnum;
import com.kakarote.module.entity.BO.ModuleFieldUnionBO;
import com.kakarote.module.entity.BO.ModuleFieldUnionConditionBO;
import com.kakarote.module.entity.BO.ModuleFieldUnionSaveBO;
import com.kakarote.module.entity.BO.SearchEntityBO;
import com.kakarote.module.entity.PO.ModuleField;
import com.kakarote.module.entity.PO.ModuleFieldUnion;
import com.kakarote.module.entity.PO.ModuleFieldUnionCondition;
import com.kakarote.module.service.IModuleFieldService;
import com.kakarote.module.service.IModuleFieldUnionConditionService;
import com.kakarote.module.service.IModuleFieldUnionProvider;
import com.kakarote.module.service.IModuleFieldUnionService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author : zjj
 * @since : 2022/12/28
 */
@Service
public class ModuleFieldUnionProviderImpl implements IModuleFieldUnionProvider {

    @Autowired
    private IModuleFieldService fieldService;

    @Autowired
    private IModuleFieldUnionService fieldUnionService;

    @Autowired
    private IModuleFieldUnionConditionService fieldUnionConditionService;

    @Override
    public List<ModuleFieldUnionSaveBO> query(Long moduleId, Integer version) {
        // 找到 数据关联、数据关联多选 字段
        List<ModuleField> moduleFields = fieldService.lambdaQuery()
                .eq(ModuleField::getModuleId, moduleId)
                .eq(ModuleField::getVersion, version)
                .in(ModuleField::getType, ModuleFieldEnum.DATA_UNION.getType(), ModuleFieldEnum.DATA_UNION_MULTI.getType())
                .list();
        List<ModuleFieldUnionSaveBO> fieldUnionSaveBOS = new ArrayList<>();

        for (ModuleField field : moduleFields) {
            List<ModuleFieldUnion> fieldUnions = fieldUnionService.lambdaQuery()
                    .eq(ModuleFieldUnion::getRelateFieldId, field.getFieldId())
                    .eq(ModuleFieldUnion::getModuleId, moduleId)
                    .eq(ModuleFieldUnion::getVersion, version)
                    .list();
            // 字段关联配置
            List<ModuleFieldUnion> fieldUnionList = fieldUnions.stream().filter(f -> ObjectUtil.equal(0, f.getType())).collect(Collectors.toList());
            List<ModuleFieldUnionBO> fieldUnionBOS = JSON.parseArray(JSON.toJSONString(fieldUnionList), ModuleFieldUnionBO.class);
            ModuleFieldUnionSaveBO fieldUnionSaveBO = new ModuleFieldUnionSaveBO();
            // 模块关联配置
            ModuleFieldUnion moduleUnion = fieldUnions.stream().filter(f -> ObjectUtil.equal(1, f.getType())).findFirst().orElse(null);
            fieldUnionSaveBO.setTargetModuleId(moduleUnion.getTargetModuleId());
            fieldUnionSaveBO.setTargetCategoryIds(moduleUnion.getTargetCategoryIds());
            List<ModuleField> targetModuleFields = fieldService.getByModuleId(moduleUnion.getTargetModuleId(), null);
            Map<Long, ModuleField> targetModuleFieldMap = targetModuleFields.stream().collect(Collectors.toMap(f -> f.getFieldId(), Function.identity()));
            fieldUnionBOS.forEach(fieldUnionBO -> {
                ModuleField targetField = targetModuleFieldMap.get(fieldUnionBO.getTargetFieldId());
                if (ObjectUtil.isNotNull(targetField)) {
                    fieldUnionBO.setTargetFieldType(targetField.getType());
                    fieldUnionBO.setTargetFieldName(targetField.getFieldName());
                }
            });
            fieldUnionSaveBO.setFieldUnionList(fieldUnionBOS);
            List<ModuleFieldUnionCondition> fieldUnionConditions = fieldUnionConditionService.lambdaQuery()
                    .eq(ModuleFieldUnionCondition::getRelateFieldId, field.getFieldId())
                    .eq(ModuleFieldUnionCondition::getModuleId, moduleId)
                    .eq(ModuleFieldUnionCondition::getVersion, version)
                    .list();
            // 转为BO
            List<ModuleFieldUnionConditionBO> fieldUnionConditionBOS = fieldUnionConditions.stream().map(f -> {
                ModuleFieldUnionConditionBO conditionBO = new ModuleFieldUnionConditionBO();
                BeanUtils.copyProperties(f, conditionBO);
                conditionBO.setSearch(JSON.parseObject(f.getSearch(), SearchEntityBO.class));
                return conditionBO;
            }).collect(Collectors.toList());
            if (CollUtil.isNotEmpty(fieldUnionConditions)) {
                fieldUnionSaveBO.setFieldUnionConditionList(fieldUnionConditionBOS);
            }
            fieldUnionSaveBO.setModuleId(field.getModuleId());
            fieldUnionSaveBO.setRelateFieldId(field.getFieldId());
            fieldUnionSaveBOS.add(fieldUnionSaveBO);
        }
        return fieldUnionSaveBOS;
    }
}
