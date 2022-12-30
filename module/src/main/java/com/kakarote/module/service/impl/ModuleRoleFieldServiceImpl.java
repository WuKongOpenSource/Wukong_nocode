package com.kakarote.module.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kakarote.common.servlet.ApplicationContextHolder;
import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.common.utils.UserUtil;
import com.kakarote.module.common.ModuleCacheUtil;
import com.kakarote.module.constant.ModuleFieldEnum;
import com.kakarote.module.entity.BO.RoleFieldRequestBO;
import com.kakarote.module.entity.BO.RoleFieldSaveBO;
import com.kakarote.module.entity.PO.ModuleEntity;
import com.kakarote.module.entity.PO.ModuleField;
import com.kakarote.module.entity.PO.ModuleFieldData;
import com.kakarote.module.entity.PO.ModuleRoleField;
import com.kakarote.module.mapper.ModuleRoleFieldMapper;
import com.kakarote.module.service.IModuleFieldService;
import com.kakarote.module.service.IModuleMetadataService;
import com.kakarote.module.service.IModuleRoleFieldService;
import com.kakarote.module.service.IModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zjj
 * @title: ModuleRoleFieldServiceImpl
 * @description: 角色字段关系表
 * @date 2021/12/410:25
 */
@Service
public class ModuleRoleFieldServiceImpl extends BaseServiceImpl<ModuleRoleFieldMapper, ModuleRoleField> implements IModuleRoleFieldService {

    @Autowired
    private IModuleService moduleService;

    @Autowired
    private IModuleFieldService fieldService;

    @Override
    public JSONObject getAuth(RoleFieldRequestBO requestBO) {
        JSONObject result = new JSONObject();
        ModuleEntity module = moduleService.getNormal(requestBO.getModuleId());
        if (ObjectUtil.isNull(module)) {
            return result;
        }
        // 字段权限
        List<ModuleRoleField> roleFields = lambdaQuery()
                .eq(ModuleRoleField::getRoleId, requestBO.getRoleId())
                .eq(ModuleRoleField::getModuleId, requestBO.getModuleId()).list();
        // 模块的字段
        List<ModuleField> moduleFields = fieldService.lambdaQuery()
                .eq(ModuleField::getModuleId, module.getModuleId())
                .eq(ModuleField::getVersion, module.getVersion())
                .list();
        Map<Long, ModuleField> fieldIdMap = moduleFields.stream().collect(Collectors.toMap(ModuleField::getFieldId, Function.identity()));
        result.fluentPut("moduleId", module.getModuleId()).fluentPut("name", module.getName());
        if (CollUtil.isEmpty(roleFields)) {
            if (CollUtil.isNotEmpty(moduleFields)) {
                List<ModuleRoleField> moduleRoleFields = new ArrayList<>();
                for (ModuleField field : moduleFields) {
                    ModuleRoleField roleField = new ModuleRoleField();
                    roleField.setFieldId(field.getFieldId());
                    roleField.setModuleId(module.getModuleId());
                    roleField.setRoleId(requestBO.getRoleId());
                    if (ObjectUtil.equal(0, field.getFieldType())) {
                        roleField.setAuthLevel(2);
                        roleField.setMaskType(0);
                        roleField.setOperateType(2);
                    } else {
                        roleField.setAuthLevel(3);
                        roleField.setMaskType(0);
                        roleField.setOperateType(1);
                    }
                    moduleRoleFields.add(roleField);
                }
                saveBatch(moduleRoleFields);
            }
            roleFields = lambdaQuery().eq(ModuleRoleField::getRoleId, requestBO.getRoleId())
                    .eq(ModuleRoleField::getModuleId, module.getModuleId()).list();
        }
        JSONArray fs = new JSONArray();
        for (ModuleRoleField roleField : roleFields) {
            JSONObject f = JSON.parseObject(JSON.toJSONString(roleField));
            ModuleField field = fieldIdMap.get(roleField.getFieldId());
            f.fluentPut("fieldName", field.getFieldName());
            f.fluentPut("name", field.getName());
            f.fluentPut("type", field.getType());
            f.fluentPut("fieldType", field.getFieldType());
            fs.add(f);
        }
        result.put("fields", fs);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveRoleField(RoleFieldSaveBO saveBO) {
        if (ObjectUtil.isNull(saveBO) || ObjectUtil.isNull(saveBO.getModuleId())
                || ObjectUtil.isNull(saveBO.getRoleId())
                || CollUtil.isEmpty(saveBO.getRoleFieldList())) {
            return;
        }
        lambdaUpdate().eq(ModuleRoleField::getModuleId, saveBO.getModuleId()).eq(ModuleRoleField::getRoleId, saveBO.getRoleId()).remove();
        List<ModuleRoleField> roleFieldList = new ArrayList<>();
        for (ModuleRoleField moduleRoleField : saveBO.getRoleFieldList()) {
            ModuleRoleField roleField = new ModuleRoleField();
            roleField.setFieldId(moduleRoleField.getFieldId());
            roleField.setModuleId(moduleRoleField.getModuleId());
            roleField.setRoleId(saveBO.getRoleId());
            roleField.setAuthLevel(moduleRoleField.getAuthLevel());
            roleField.setMaskType(moduleRoleField.getMaskType());
            roleField.setOperateType(moduleRoleField.getOperateType());
            roleFieldList.add(roleField);
        }
        saveBatch(roleFieldList);
    }

    @Override
    public void replaceMaskFieldValue(List<? extends Map<String, Object>> list, Long moduleId) {
        ModuleEntity module = ModuleCacheUtil.getActiveById(moduleId);
        List<Map<String, String>> roleFields = getBaseMapper().getUserRoleField(UserUtil.getUserId(), moduleId, module.getVersion());
        if (CollUtil.isEmpty(roleFields) || UserUtil.isAdmin()) {
            return;
        }
        Map<String, Map<String, String>> roleFieldMap = roleFields.stream()
                .collect(Collectors.toMap(roleField->
                        StrUtil.toCamelCase(MapUtil.getStr(roleField, "fieldName")), Function.identity()));
        for (Map<String, Object> map : list) {
            for (Map.Entry<String, Map<String, String>> entry : roleFieldMap.entrySet()) {
                String key = entry.getKey();
                if (map.containsKey(key)) {
                    map.put(key, parseValue(MapUtil.getInt(entry.getValue(), "type"), map.get(key)));
                }
            }
        }
    }

    @Override
    public void replaceMaskFieldValue2(List<ModuleFieldData> fieldDataList, Long moduleId) {
        ModuleEntity module = ApplicationContextHolder.getBean(IModuleService.class).getNormal(moduleId);
        List<Map<String, String>> roleFields = getBaseMapper().getUserRoleField(UserUtil.getUserId(), moduleId, module.getVersion());
        if (CollUtil.isEmpty(roleFields) || UserUtil.isAdmin()) {
            return;
        }
        Map<Long, Map<String, String>> roleFieldMap = roleFields.stream()
                .collect(Collectors.toMap(roleField ->
                        MapUtil.getLong(roleField, "fieldId"), Function.identity()));
        for (ModuleFieldData fieldData : fieldDataList) {
            for (Map.Entry<Long, Map<String, String>> entry : roleFieldMap.entrySet()) {
                Integer maskType = MapUtil.getInt( entry.getValue(),"maskType");
                if (ObjectUtil.equal(2, maskType)) {
                    Long key = entry.getKey();
                    if (ObjectUtil.equal(key, fieldData.getFieldId())) {
                        Object value = parseValue(MapUtil.getInt(entry.getValue(), "type"), fieldData.getValue());
                        String valueStr = Optional.ofNullable(value).orElse("").toString();
                        fieldData.setValue(valueStr);
                    }
                }
            }
        }
    }


    /**
     * 格式化字段值
     * @param type 字段类型
     * @param value 字段值
     * @return 格式化后的值
     */
    @Override
    public Object parseValue(Integer type, Object value) {
        if (ObjectUtil.isEmpty(value)) {
            return "";
        }
        ModuleFieldEnum fieldEnum = ModuleFieldEnum.parse(type);
        switch (fieldEnum) {
            case FLOATNUMBER: {
                return "***";
            }
            case MOBILE: {
                String s = value.toString();
                if (s.length() <= 7) {
                    return "***";
                }
                return StrUtil.replace(s, 3, s.length() - 4, '*');
            }
            case EMAIL: {
                String s = value.toString();
                List<String> split = StrUtil.split(s, "@");
                if (split.size() < 2) {
                    return "***";
                }
                if (split.get(0).length() <= 2) {
                    return "***@" + split.get(1);
                }
                return StrUtil.replace(split.get(0), 2, split.get(0).length(), '*') + split.get(1);
            }
            case AREA_POSITION: {
                try {
                    String jsonStr;
                    if (value instanceof List) {
                        jsonStr = JSON.toJSONString(value);
                    } else {
                        jsonStr = value.toString();
                    }
                    JSONArray array = JSONArray.parseArray(jsonStr);
                    if (array.size() >= 4) {
                        JSONObject jsonObject = array.getJSONObject(3);
                        jsonObject.put("name","***");
                    }
                    return array.toJSONString();
                } catch (Exception e) {
                    return value;
                }
            }
            default:
                return value;
        }
    }
}
