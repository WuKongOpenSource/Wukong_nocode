package com.kakarote.module.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.module.entity.BO.ModuleFieldFormulaBO;
import com.kakarote.module.entity.PO.ModuleFieldFormula;
import com.kakarote.module.mapper.ModuleFieldFormulaMapper;
import com.kakarote.module.service.IModuleFieldFormulaService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zjj
 * @title: ModuleFieldFormulaServiceImpl
 * @description: 字段公式 服务实现类
 * @date 2022/3/4 16:03
 */
@Service
public class ModuleFieldFormulaServiceImpl extends BaseServiceImpl<ModuleFieldFormulaMapper, ModuleFieldFormula> implements IModuleFieldFormulaService {

    @Override
    public ModuleFieldFormulaBO queryFormulaList(Long moduleId, Long fieldId, Integer version) {
        ModuleFieldFormula fieldFormula = lambdaQuery()
                .eq(ModuleFieldFormula::getModuleId, moduleId)
                .eq(ModuleFieldFormula::getFieldId, fieldId)
                .eq(ModuleFieldFormula::getVersion, version)
                .one();
        return BeanUtil.copyProperties(fieldFormula, ModuleFieldFormulaBO.class);
    }

    @Override
    public List<ModuleFieldFormula> getByModuleIdAndVersion(Long moduleId, Integer version) {
        return lambdaQuery()
                .eq(ModuleFieldFormula::getModuleId, moduleId)
                .eq(ModuleFieldFormula::getVersion, version).list();
    }
}
