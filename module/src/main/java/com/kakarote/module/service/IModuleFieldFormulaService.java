package com.kakarote.module.service;

import com.kakarote.common.servlet.BaseService;
import com.kakarote.module.entity.BO.ModuleFieldFormulaBO;
import com.kakarote.module.entity.PO.ModuleEntity;
import com.kakarote.module.entity.PO.ModuleFieldFormula;

import java.util.List;

/**
 * @author zjj
 * @title: IModuleFieldFormulaService
 * @description: 字段公式 service
 * @date 2022/3/4 16:01
 */
public interface IModuleFieldFormulaService extends BaseService<ModuleFieldFormula> {

    /**
     * 获取字段的公式配置
     *
     * @param moduleId
     * @param fieldId
     * @param version
     * @return
     */
    ModuleFieldFormulaBO queryFormulaList(Long moduleId, Long fieldId, Integer version);

    /**
     * 根据模块id和版本号获取公式字段配置
     *
     * @param moduleId
     * @param version
     * @return
     */
    List<ModuleFieldFormula> getByModuleIdAndVersion(Long moduleId, Integer version);
}
