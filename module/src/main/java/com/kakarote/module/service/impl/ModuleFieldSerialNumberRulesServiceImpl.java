package com.kakarote.module.service.impl;

import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.module.entity.PO.ModuleFieldSerialNumberRules;
import com.kakarote.module.mapper.ModuleFieldSerialNumberRulesMapper;
import com.kakarote.module.service.IModuleFieldSerialNumberRulesService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 自定义编号字段-规则
 *
 * @author wwl
 * @date 20220304
 */
@Service
public class ModuleFieldSerialNumberRulesServiceImpl extends BaseServiceImpl<ModuleFieldSerialNumberRulesMapper, ModuleFieldSerialNumberRules> implements IModuleFieldSerialNumberRulesService {

    @Override
    public List<ModuleFieldSerialNumberRules> querySerialNumberRuleList(Long moduleId, Long fieldId, Integer version) {
        List<ModuleFieldSerialNumberRules> rules = lambdaQuery()
                .eq(ModuleFieldSerialNumberRules::getModuleId, moduleId)
                .eq(ModuleFieldSerialNumberRules::getVersion, version)
                .eq(ModuleFieldSerialNumberRules::getFieldId, fieldId)
                .eq(ModuleFieldSerialNumberRules::getVersion, version)
                .orderByAsc(ModuleFieldSerialNumberRules::getSorting).list();
        return rules;
    }

    @Override
    public List<ModuleFieldSerialNumberRules> getByModuleIdAndVersion(Long moduleId, Integer version) {
        return lambdaQuery()
                .eq(ModuleFieldSerialNumberRules::getModuleId, moduleId)
                .eq(ModuleFieldSerialNumberRules::getVersion, version)
                .orderByAsc(ModuleFieldSerialNumberRules::getSorting)
                .list();
    }
}
