package com.kakarote.module.service;

import com.kakarote.module.entity.PO.ModuleEntity;

import java.util.List;

/**
 * @author : zjj
 * @since : 2022/12/27
 */
public interface IModuleFieldFormulaProvider {

    /**
     * 更新计算公式字段值
     *
     * @param moduleId
     */
    void updateFormulaFieldValue(Long moduleId);

    /**
     * 更新计算公式字段值
     *
     * @param modules
     */
    void updateFormulaFieldValue(List<ModuleEntity> modules);

    /**
     * 更新单个模块计算公式字段值
     *
     * @param moduleId
     */
    void updateFormulaFieldValueByModuleId(Long moduleId);
}
