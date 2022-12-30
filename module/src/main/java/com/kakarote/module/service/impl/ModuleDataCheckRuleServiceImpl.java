package com.kakarote.module.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.kakarote.common.servlet.ApplicationContextHolder;
import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.module.constant.ModuleFieldEnum;
import com.kakarote.module.entity.BO.ModuleDataCheckRequestBO;
import com.kakarote.module.entity.PO.ModuleDataCheckRule;
import com.kakarote.module.entity.PO.ModuleField;
import com.kakarote.module.entity.PO.ModuleFieldData;
import com.kakarote.module.entity.VO.ModuleDataCheckResultVO;
import com.kakarote.module.mapper.ModuleDataCheckRuleMapper;
import com.kakarote.module.service.IModuleDataCheckRuleService;
import com.kakarote.module.service.IModuleFieldService;
import com.kakarote.module.service.IModuleFormulaService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zjj
 * @title: ModuleDataCheckServiceImpl
 * @description: 数据校验服务实现
 * @date 2022/3/26 14:15
 */
@Service
public class ModuleDataCheckRuleServiceImpl extends BaseServiceImpl<ModuleDataCheckRuleMapper, ModuleDataCheckRule> implements IModuleDataCheckRuleService, IModuleFormulaService {

    @Override
    public List<ModuleDataCheckRule> getByModuleIdAndVersion(Long moduleId, Integer version) {
        return lambdaQuery()
                .eq(ModuleDataCheckRule::getModuleId, moduleId)
                .eq(ModuleDataCheckRule::getVersion, version)
                .orderByAsc(ModuleDataCheckRule::getSort)
                .list();
    }

    @Override
    public List<ModuleDataCheckResultVO> dataCheck(ModuleDataCheckRequestBO requestBO) {
        List<ModuleDataCheckResultVO> result = new ArrayList<>();
        Long moduleId = requestBO.getModuleId();
        Integer version = requestBO.getVersion();
        List<ModuleFieldData> fieldDataList = requestBO.getFieldDataList();
        if (CollUtil.isEmpty(fieldDataList)) {
            return result;
        }
        List<ModuleField> fieldList = ApplicationContextHolder.getBean(IModuleFieldService.class).lambdaQuery()
                .eq(ModuleField::getModuleId, moduleId)
                .eq(ModuleField::getVersion, version)
                .eq(ModuleField::getFieldType, 1)
                .eq(ModuleField::getIsHidden, 0)
                .list();
        // 不同-表示是填写节点或者其它
        if (ObjectUtil.notEqual(fieldDataList.size(), fieldList.size())) {
            Set<Long> fieldDataIds = fieldDataList.stream().map(ModuleFieldData::getFieldId).collect(Collectors.toSet());
            List<Integer> zeroDefault = Arrays.asList(ModuleFieldEnum.FLOATNUMBER.getType(), ModuleFieldEnum.PERCENT.getType());
            List<ModuleFieldData> otherFields = fieldList.stream()
                    .filter(f -> !fieldDataIds.contains(f.getFieldId()))
                    .map(m -> {
                        ModuleFieldData data = new ModuleFieldData();
                        data.setValue("");
                        if (zeroDefault.contains(m.getType())) {
                            data.setValue("0");
                        }
                        data.setFieldId(m.getFieldId());
                        data.setFieldName(m.getFieldName());
                        return data;
                    })
                    .collect(Collectors.toList());
            fieldDataList.addAll(otherFields);
        }
        Map<Long, Object> fieldIdValue = fieldDataList.stream().collect(Collectors.toMap(ModuleFieldData::getFieldId, ModuleFieldData::getValue));
        List<ModuleDataCheckRule> checkRules = this.getByModuleIdAndVersion(moduleId, version);
        for (ModuleDataCheckRule checkRule : checkRules) {
            ModuleDataCheckResultVO resultVO = new ModuleDataCheckResultVO();
            resultVO.setTip(checkRule.getTip());
            try {
                Object value = calculateFormula(moduleId, version, fieldIdValue, checkRule.getFormula());
                if (value instanceof Boolean) {
                    if (Boolean.parseBoolean(value.toString())) {
                        resultVO.setResult(1);
                    } else {
                        resultVO.setResult(0);
                    }
                } else {
                    resultVO.setResult(2);
                }
            } catch (Exception e) {
                resultVO.setResult(0);
                e.printStackTrace();
                log.error(e.getMessage());
            }
            result.add(resultVO);
        }
        return result;
    }
}
