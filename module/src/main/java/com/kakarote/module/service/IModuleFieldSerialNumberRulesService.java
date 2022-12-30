package com.kakarote.module.service;

import com.kakarote.common.servlet.BaseService;
import com.kakarote.module.entity.PO.ModuleFieldSerialNumberRules;

import java.util.List;

/**
 * 自定义编号字段-规则
 * @author wwl
 * @date 20220304
 */
public interface IModuleFieldSerialNumberRulesService extends BaseService<ModuleFieldSerialNumberRules> {

    /**
     * 获取字段的标签选项
     *
     * @param moduleId 模块id
     * @param fieldId 字段id
     * @param version 版本号
     * @return data
     */
    List<ModuleFieldSerialNumberRules> querySerialNumberRuleList(Long moduleId, Long fieldId, Integer version);

    /**
     * 根据模块id和版本号获取标签字段的选项
     *
     * @param moduleId 模块id
     * @param version 版本号
     * @return data
     */
    List<ModuleFieldSerialNumberRules> getByModuleIdAndVersion(Long moduleId, Integer version);
}
