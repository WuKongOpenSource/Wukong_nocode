package com.kakarote.module.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.googlecode.aviator.runtime.type.AviatorBoolean;
import com.kakarote.common.result.BasePage;
import com.kakarote.common.servlet.ApplicationContextHolder;
import com.kakarote.module.common.ModuleFieldCacheUtil;
import com.kakarote.module.constant.ModuleFieldEnum;
import com.kakarote.module.common.expression.ExpressionUtil;
import com.kakarote.module.entity.BO.ModuleFormulaBO;
import com.kakarote.module.entity.BO.ModuleOptionsBO;
import com.kakarote.module.entity.PO.ModuleField;
import com.kakarote.module.entity.PO.ModuleFieldDataCommon;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zjj
 * @title: IModuleFormulaService
 * @description: 表达式计算
 * @date 2022/3/10 15:48
 */
public interface IModuleFormulaService {

    /**
     *  构建公式的参数
     *
     * @param moduleFields
     * @param fieldNameValue
     * @return
     */
    default Map<String, Object> buildFormulaEnv(List<ModuleField> moduleFields, Map<String, Object> fieldNameValue) {
        Map<String, ModuleField> fieldNameMap = moduleFields.stream().collect(Collectors.toMap(ModuleField::getFieldName, Function.identity()));
        Map<String, Object> env = new HashMap<>(16);
        for (Map.Entry<String, Object> entry : fieldNameValue.entrySet()) {
            String fieldName = entry.getKey();
            Object value = entry.getValue();
            if (ObjectUtil.isNull(value)) {
                continue;
            }
            ModuleField field = fieldNameMap.get(fieldName);
            if (ObjectUtil.isNull(field)) {
                continue;
            }
            String argName = ExpressionUtil.getArgName(field);
            env.put(argName, value);
            // 下拉选
            if (ObjectUtil.equal(ModuleFieldEnum.SELECT, ModuleFieldEnum.parse(field.getType()))) {
                String jsonStr ="";
                if (value instanceof Map) {
                    jsonStr = JSON.toJSONString(value);
                } else {
                    jsonStr = value.toString();
                }
                ModuleOptionsBO optionsBO = JSON.parseObject(jsonStr, ModuleOptionsBO.class);
                env.put(argName, optionsBO.getValue());
            }
            // 数字类型的字段
            if (Arrays.asList(ModuleFieldEnum.NUMBER, ModuleFieldEnum.FLOATNUMBER, ModuleFieldEnum.PERCENT).contains(ModuleFieldEnum.parse(field.getType()))){
                env.put(argName, Long.valueOf(value.toString()));
            }
        }
        return env;
    }

    /**
     * 构建公式的参数
     *
     * @param moduleId       模块ID
     * @param version        版本号
     * @param fieldNameValue 字段名-值 map
     * @return
     */
    default Map<String, Object> buildFormulaEnv(Long moduleId, Integer version, Map<String, Object> fieldNameValue) {
        // 当前模块的所有字段
        List<ModuleField> moduleFields;
        if (ObjectUtil.isNull(version)) {
            moduleFields = ApplicationContextHolder.getBean(IModuleFieldService.class).getByModuleIdAndVersion(moduleId, version, null);
        } else {
            moduleFields = ApplicationContextHolder.getBean(IModuleFieldService.class).getByModuleId(moduleId, null);
        }
        return buildFormulaEnv(moduleFields, fieldNameValue);
    }

    /**
     * 表达式计算
     *
     * @param moduleId       模块ID
     * @param version        版本号
     * @param fieldNameValue 字段名-值 map
     * @param formula        计算公式
     * @return
     */
    default Object calculateFormulaOne(Long moduleId, Integer version, Map<String, Object> fieldNameValue, String formula) {
        if (ObjectUtil.isNotNull(formula)) {
            ModuleFormulaBO formulaBO = BeanUtil.copyProperties(formula, ModuleFormulaBO.class);
            // 当前模块的所有字段
            List<ModuleField> moduleFields = ApplicationContextHolder.getBean(IModuleFieldService.class).getByModuleIdAndVersion(moduleId, version, null);
            Map<String, ModuleField> fieldNameMap = moduleFields.stream().collect(Collectors.toMap(ModuleField::getFieldName, Function.identity()));
            Map<String, Object> env = new HashMap<>(16);
            for (Map.Entry<String, Object> entry : fieldNameValue.entrySet()) {
                String fieldName = entry.getKey();
                Object value = entry.getValue();
                ModuleField field = fieldNameMap.get(fieldName);
                String argName = ExpressionUtil.getArgName(field);
                env.put(argName, value);
                // 下拉选
                if (ObjectUtil.equal(ModuleFieldEnum.SELECT, ModuleFieldEnum.parse(field.getType()))) {
                    ModuleOptionsBO optionsBO = JSON.parseObject(value.toString(), ModuleOptionsBO.class);
                    env.put(argName, optionsBO.getValue());
                }
                // 数字类型的字段
                if (Arrays.asList(ModuleFieldEnum.NUMBER, ModuleFieldEnum.FLOATNUMBER, ModuleFieldEnum.PERCENT).contains(ModuleFieldEnum.parse(field.getType()))){
                    env.put(argName, Long.valueOf(value.toString()));
                }
            }
            formulaBO.setEnv(env);
            return ExpressionUtil.execute(formulaBO);
        }
        return null;
    }


    /**
     * 表达式计算
     *
     * @param moduleId     模块ID
     * @param version      版本号
     * @param fieldIdValue 字段ID-值 map
     * @param formula      计算公式
     * @return
     */
    default Object calculateFormula(Long moduleId, Integer version, Map<Long, Object> fieldIdValue, String formula) {
        if (ObjectUtil.isNotNull(formula)) {
            ModuleFormulaBO formulaBO = JSON.parseObject(formula, ModuleFormulaBO.class);
            Map<String, Object> env = new HashMap<>(16);
            for (Map.Entry<Long, Object> entry : fieldIdValue.entrySet()) {
                Long fieldId = entry.getKey();
                Object value = entry.getValue();
                String argName = ExpressionUtil.getArgName(moduleId, fieldId);
                env.put(argName, value);
                ModuleField field = ModuleFieldCacheUtil.getByIdAndVersion(moduleId, fieldId, version);
                ModuleFieldEnum fieldEnum = ModuleFieldEnum.parse(field.getType());
                if (ObjectUtil.isEmpty(value)) {
                    continue;
                }
                // 非明细表格字段
                if (ObjectUtil.notEqual(ModuleFieldEnum.DETAIL_TABLE.getType(), field.getType())) {
                    // 下拉选
                    if (ObjectUtil.equal(ModuleFieldEnum.SELECT, fieldEnum)) {
                        ModuleOptionsBO optionsBO;
                        if (value instanceof Map) {
                            optionsBO = JSON.parseObject(JSON.toJSONString(value), ModuleOptionsBO.class);
                        } else {
                            optionsBO = JSON.parseObject(value.toString(), ModuleOptionsBO.class);
                        }
                        env.put(argName, optionsBO.getValue());
                    }
                    // 数字类型的字段
                    if (Arrays.asList(ModuleFieldEnum.NUMBER, ModuleFieldEnum.FLOATNUMBER, ModuleFieldEnum.PERCENT, ModuleFieldEnum.STATISTIC).contains(fieldEnum)) {
                        env.put(argName, new BigDecimal(value.toString()));
                    }
                    // 数据关联字段类型
                    if (ObjectUtil.equal(ModuleFieldEnum.DATA_UNION, fieldEnum)) {
                        if (ObjectUtil.isEmpty(value)) {
                            continue;
                        }
                        Long dataId = Long.valueOf(value.toString());
                        ModuleFieldDataCommon dataCommon = ApplicationContextHolder.getBean(IModuleFieldDataCommonService.class).getByDataId(dataId);
                        Map<Long, Object> fieldIdDataMap = ApplicationContextHolder.getBean(IModuleFieldDataService.class).queryAllFieldIdDataMap(dataId);
                        Long targetModuleId = dataCommon.getModuleId();
                        if (ObjectUtil.equal(moduleId, targetModuleId)) {
                            continue;
                        }
                        for (Map.Entry<Long, Object> objectEntry : fieldIdDataMap.entrySet()) {
                            Long targetModuleFieldId = objectEntry.getKey();
                            env.put(ExpressionUtil.getArgName(targetModuleId, targetModuleFieldId), objectEntry.getValue());
                        }
                    }
                } else {
                    // 明细表格字段
                    List<ModuleField> fields = ApplicationContextHolder.getBean(IModuleFieldService.class).getFieldByGroupId(field.getModuleId(), field.getGroupId(), field.getVersion());
                    String valueStr;
                    if (value instanceof Map) {
                        valueStr = JSON.toJSONString(value);
                    } else {
                        valueStr = value.toString();
                    }
                    List<JSONObject> objects = JSON.parseArray(valueStr, JSONObject.class);
                    for (ModuleField moduleField : fields) {
                        if (Arrays.asList(ModuleFieldEnum.NUMBER.getType(), ModuleFieldEnum.FLOATNUMBER.getType(), ModuleFieldEnum.PERCENT.getType()).contains(moduleField.getType())) {
                            List<BigDecimal> valueList = objects.stream().map(o -> o.getBigDecimal(moduleField.getFieldName())).filter(o -> ObjectUtil.isNotNull(o)).collect(Collectors.toList());
                            env.put(ExpressionUtil.getArgName(moduleField.getModuleId(), moduleField.getFieldId()), valueList);
                        }
                    }
                }

            }
            formulaBO.setEnv(env);
            return ExpressionUtil.execute(formulaBO);
        }
        return null;
    }

    /**
     *  处理高级模式
     *
     * @param data
     * @param moduleId
     * @param formulaBO
     * @param page
     * @param limit
     * @return
     */
    default BasePage<Map<String, Object>> dealAdvanceModel(BasePage<Map<String, Object>> data, Long moduleId, ModuleFormulaBO formulaBO, int page, int limit) {
        List<ModuleField> moduleFields = ApplicationContextHolder.getBean(IModuleFieldService.class).getByModuleId(moduleId, null);
        List<Map<String, Object>> mapList = new ArrayList<>();
        for (Map<String, Object> map : data.getList()) {
            Map<String, Object> env = buildFormulaEnv(moduleFields, map);
            formulaBO.setEnv(env);
            try {
                if (ObjectUtil.equal(AviatorBoolean.TRUE, ExpressionUtil.execute(formulaBO)) || ObjectUtil.equal(true, ExpressionUtil.execute(formulaBO))) {
                    mapList.add(map);
                }
            } catch (Exception e) {
                continue;
            }
        }
        BasePage<Map<String, Object>>  result = new BasePage<>(page, limit, mapList.size());
        result.setRecords(CollUtil.sub(mapList, (page - 1) * limit, page * limit));
        return result;
    }
}
