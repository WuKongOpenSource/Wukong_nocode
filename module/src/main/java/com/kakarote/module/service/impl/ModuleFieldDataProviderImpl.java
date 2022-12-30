package com.kakarote.module.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.util.TypeUtils;
import com.kakarote.common.constant.Const;
import com.kakarote.common.entity.SimpleUser;
import com.kakarote.common.exception.BusinessException;
import com.kakarote.common.servlet.ApplicationContextHolder;
import com.kakarote.common.utils.BaseUtil;
import com.kakarote.common.utils.UserUtil;
import com.kakarote.ids.provider.utils.UserCacheUtil;
import com.kakarote.module.common.ModuleCacheUtil;
import com.kakarote.module.common.ModuleFieldCacheUtil;
import com.kakarote.module.common.mq.ProducerUtil;
import com.kakarote.module.constant.*;
import com.kakarote.module.entity.BO.*;
import com.kakarote.module.entity.PO.*;
import com.kakarote.module.mapper.ModuleFieldDataMapper;
import com.kakarote.module.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author : zjj
 * @since : 2022/12/27
 */
@Service
public class ModuleFieldDataProviderImpl implements IFlowCommonService, IModuleFieldDataProvider {

    @Autowired
    private ProducerUtil producerUtil;

    @Autowired
    private IModuleFieldService fieldService;

    @Autowired
    private IModuleFieldUnionService fieldUnionService;

    @Autowired
    private IModuleTreeDataService treeDataService;

    @Autowired
    private ModuleFieldDataMapper fieldDataMapper;

    @Autowired
    private IModuleFieldDataService fieldDataService;

    @Autowired
    private IModuleFieldDataCommonService fieldDataCommonService;

    @Autowired
    private IModuleDataOperationRecordService operationRecordService;

    @Autowired
    private IModuleFieldSerialNumberService serialNumberService;

    @Autowired
    private IFlowExamineRecordService examineRecordService;

    @Autowired
    private IFlowExamineProvider examineProvider;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long save(ModuleFieldDataSaveBO fieldDataBO) {
        Long moduleId = fieldDataBO.getModuleId();
        Integer version = fieldDataBO.getVersion();
        ModuleEntity module = ModuleCacheUtil.getByIdAndVersion(moduleId, version);
        if (ObjectUtil.isNull(module)) {
            throw new BusinessException(ModuleCodeEnum.MODULE_NOT_FOUND);
        }
        Long dataId = fieldDataBO.getDataId();
        ModuleFieldDataCommon fieldDataCommon;
        ActionTypeEnum actionTypeEnum = ActionTypeEnum.INSERT;
        // 审批流程数据
        ExamineRecordSaveBO examineRecordSaveBO = fieldDataBO.getFlowSaveBO();
        MsgBodyBO msgBody = new MsgBodyBO();
        // 编辑数据
        // 旧数据
        List<ModuleFieldValueBO> oldData = new ArrayList<>();
        if (ObjectUtil.isNotNull(dataId)) {
            // 旧数据
            oldData = this.queryValueMap(moduleId, version, dataId, null);
            // 删除历史数据
            fieldDataService.lambdaUpdate().eq(ModuleFieldData::getDataId, dataId).remove();
            fieldDataCommon = fieldDataCommonService.getByDataId(dataId);
            Integer status = fieldDataCommon.getFlowStatus();
            // 当前数据状态为作废
            if (ObjectUtil.equal(FlowStatusEnum.INVALID.getStatus(), status)) {
                fieldDataCommon.setFlowStatus(FlowStatusEnum.UN_SUBMITTED.getStatus());
            }
            if (ObjectUtil.isNotNull(examineRecordSaveBO)) {
                if (!FlowStatusEnum.editable(status)) {
                    throw new BusinessException(ModuleCodeEnum.MODULE_FIELD_DATA_CAN_NOT_EDIT_ERROR);
                }
            }
            fieldDataCommon.setVersion(version);
            fieldDataCommon.setUpdateTime(DateUtil.date());
            actionTypeEnum = ActionTypeEnum.UPDATE;
            msgBody.setOldData(oldData);
            msgBody.setMsgTag(MessageTagEnum.UPDATE_DATA);
        } else {
            dataId = BaseUtil.getNextId();
            fieldDataCommon = new ModuleFieldDataCommon();
            fieldDataCommon.setBatchId(fieldDataBO.getBatchId());
            fieldDataCommon.setDataId(dataId);
            fieldDataCommon.setCreateTime(DateUtil.date());
            fieldDataCommon.setCreateUserId(UserUtil.getUserId());
            fieldDataCommon.setUpdateTime(DateUtil.date());
            fieldDataCommon.setOwnerUserId(UserUtil.getUserId());
            fieldDataCommon.setCategoryId(fieldDataBO.getCategoryId());
            fieldDataCommon.setModuleId(moduleId);
            fieldDataCommon.setVersion(version);
            fieldDataCommon.setFlowStatus(FlowStatusEnum.WAIT.getStatus());
            msgBody.setMsgTag(MessageTagEnum.INSERT_DATA);
        }

        // 获取所有的字段
        List<ModuleField> fieldList = ModuleFieldCacheUtil.getByIdAndVersion(moduleId, version);
        Map<String, ModuleField> fieldNameEntityMap = fieldList.stream().filter(f -> ObjectUtil.isNotNull(f.getGroupId()))
                .collect(Collectors.toMap(ModuleField::getFieldName, Function.identity()));
        // 所有的自定义字段 ID
        List<Long> fieldIds = fieldList.stream()
                .filter(f -> ObjectUtil.equal(1, f.getFieldType()))
                .map(ModuleField::getFieldId).collect(Collectors.toList());
        // 保存字段值
        List<ModuleFieldData> fieldDataList = fieldDataBO.getFieldDataList().stream()
                .filter(d -> CollUtil.contains(fieldIds, d.getFieldId())).collect(Collectors.toList());
        Long finalDataId = dataId;
        fieldDataList.forEach(e -> {
            e.setDataId(finalDataId);
            e.setVersion(module.getVersion());
            e.setCreateTime(DateUtil.date());
            e.setModuleId(moduleId);
            ModuleField moduleField = ModuleFieldCacheUtil.getByIdAndVersion(moduleId, e.getFieldId(), version);
            e.setFieldName(moduleField.getFieldName());
            List<JSONObject> value = new ArrayList<>();
            // 明细表格字段值处理
            if (ObjectUtil.equal(ModuleFieldEnum.DETAIL_TABLE.getType(), moduleField.getType())) {
                List<JSONObject> jsonObjects = JSON.parseArray(e.getValue(), JSONObject.class);
                for (JSONObject object : jsonObjects) {
                    JSONObject valueObject = new JSONObject();
                    for (Map.Entry<String, Object> entry : object.entrySet()) {
                        String key = entry.getKey();
                        Object v = entry.getValue();
                        ModuleField field = fieldNameEntityMap.get(key);
                        if (ObjectUtil.isNotEmpty(v)) {
                            if (ObjectUtil.equal(ModuleFieldEnum.SELECT, ModuleFieldEnum.parse(field.getType()))) {
                                valueObject.put(key, JSON.parseObject(String.valueOf(v), JSONObject.class));
                                valueObject.put(String.format("%sSize", key), 1);
                            } else if (Arrays.asList(ModuleFieldEnum.CHECKBOX, ModuleFieldEnum.TAG).contains(ModuleFieldEnum.parse(field.getType()))) {
                                List<JSONObject> jsonObjectList = JSON.parseArray(String.valueOf(v), JSONObject.class);
                                valueObject.put(key, jsonObjectList);
                                valueObject.put(String.format("%sSize", key), jsonObjectList.size());
                            } else if (ObjectUtil.equal(ModuleFieldEnum.DATE_INTERVAL, ModuleFieldEnum.parse(field.getType()))) {
                                valueObject.put(key, JSON.parseObject(String.valueOf(v), JSONObject.class));
                            } else if (ObjectUtil.equal(ModuleFieldEnum.AREA_POSITION, ModuleFieldEnum.parse(field.getType()))) {
                                List<JSONObject> jsonObjectList = JSON.parseArray(String.valueOf(v), JSONObject.class);
                                valueObject.put(key, jsonObjectList);
                                valueObject.put(String.format("%sSize", key), jsonObjectList.size());
                            } else {
                                valueObject.put(field.getFieldName(), v);
                            }
                        } else {
                            valueObject.put(field.getFieldName(), v);
                        }
                    }
                    value.add(valueObject);
                }
                e.setValue(JSON.toJSONString(value));
            }
            // 树字段值处理
            if (ObjectUtil.equal(ModuleFieldEnum.TREE.getType(), moduleField.getType())) {
                treeDataService.save(moduleId, e.getFieldId(), finalDataId, e.getValue());
            }
        });
        fieldDataService.saveOrUpdateBatch(fieldDataList);
        fieldDataCommonService.saveOrUpdate(fieldDataCommon);

        // 获取模块主字段值
        String mainFieldValue = this.queryValue(dataId, module.getMainFieldId());
        // 未提交状态
        if (ObjectUtil.equal(FlowStatusEnum.UN_SUBMITTED.getStatus(), fieldDataBO.getStatus())) {
            fieldDataCommon.setFlowStatus(FlowStatusEnum.UN_SUBMITTED.getStatus());
            fieldDataCommonService.saveOrUpdate(fieldDataCommon);
        } else {
            // 无流程直接通过
            if (ObjectUtil.isNull(examineRecordSaveBO)) {
                fieldDataCommon.setFlowStatus(FlowStatusEnum.DEFAULT.getStatus());
                fieldDataCommonService.saveOrUpdate(fieldDataCommon);
            } else {
                examineRecordSaveBO.setCreateUserId(UserUtil.getUserId());
                // 获取模块主字段值，并设置为审核通知消息标题
                examineRecordSaveBO.setTitle(mainFieldValue);
                examineRecordSaveBO.setDataId(dataId);
                examineProvider.save(examineRecordSaveBO);
            }
        }
        // 自定义编号字段
        List<ModuleField> serialFields = fieldList.stream().filter(f -> ObjectUtil.equal(ModuleFieldEnum.SERIAL_NUMBER.getType(), f.getType())).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(serialFields)) {
            Map<String, Object> fieldNameDataMap = queryFieldNameDataMap(dataId);
            for (ModuleField serial : serialFields) {
                if (ObjectUtil.isNotNull(serial.getGroupId())) {
                    continue;
                }
                if (ObjectUtil.isNotEmpty(fieldNameDataMap.get(serial.getFieldName()))) {
                    continue;
                }
                String serialNumber = serialNumberService.generateNumber(fieldNameDataMap, fieldList, serial.getFieldId(), moduleId, version);
                fieldNameDataMap.put(serial.getFieldName(), serialNumber);
                fieldDataService.saveOrUpdate(serial, serialNumber, dataId, version, moduleId);
            }
        }
        // 明细表格字段
        List<ModuleField> detailTableFields = fieldList.stream().filter(f -> ObjectUtil.equal(ModuleFieldEnum.DETAIL_TABLE.getType(), f.getType())).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(detailTableFields)) {
            Map<String, Object> fieldNameDataMap = queryFieldNameDataMap(dataId);
            for (ModuleField detailTableField : detailTableFields) {
                List<ModuleField> serialNumberFieldsInTable = serialFields.stream()
                        .filter(f -> ObjectUtil.equal(detailTableField.getGroupId(), f.getGroupId()))
                        .filter(f -> ObjectUtil.equal(ModuleFieldEnum.SERIAL_NUMBER.getType(), f.getType()))
                        .collect(Collectors.toList());
                if (CollUtil.isNotEmpty(serialNumberFieldsInTable)) {
                    String detailValue = MapUtil.getStr(fieldNameDataMap, detailTableField.getFieldName());
                    if (StrUtil.isEmpty(detailValue)) {
                        continue;
                    }
                    List<JSONObject> jsonObjects = JSONArray.parseArray(detailValue, JSONObject.class);
                    for (ModuleField serialNumberField : serialNumberFieldsInTable) {
                        Long fieldId = serialNumberField.getFieldId();
                        String fieldName = serialNumberField.getFieldName();
                        for (JSONObject jsonObject : jsonObjects) {
                            if (ObjectUtil.isNotEmpty(jsonObject.get(fieldName))) {
                                continue;
                            }
                            String serialNumber = serialNumberService.generateNumber(fieldNameDataMap, fieldList, fieldId, moduleId, version);
                            jsonObject.put(fieldName, serialNumber);
                        }
                    }
                    fieldNameDataMap.put(detailTableField.getFieldName(), detailValue);
                    fieldDataService.saveOrUpdate(detailTableField, JSON.toJSONString(jsonObjects), dataId, version, moduleId);
                }
            }
        }
        // 当前数据
        List<ModuleFieldValueBO> currentData = this.queryValueMap(moduleId, version, dataId, null);
        // 保存操作记录
        ModuleDataOperationRecord operationRecord = operationRecordService.initEntity(module.getModuleId(), module.getVersion(), dataId, mainFieldValue, oldData, currentData, actionTypeEnum);
        operationRecordService.save(operationRecord);
        msgBody.setCurrentData(currentData);
        // 发送MQ消息
        msgBody.setMsgKey(IdUtil.simpleUUID());
        msgBody.setModuleId(moduleId);
        msgBody.setVersion(version);
        msgBody.setDataId(dataId);
        msgBody.setUserId(UserUtil.getUserId());
        msgBody.setDelayTime(2000L);
        producerUtil.sendMsgToTopicOne(msgBody);
        // 保存ES
        savePage(dataId, moduleId, version);
        return dataId;
    }

    @Override
    public List<ModuleFieldValueBO> queryValueMap(Long moduleId, Integer version, Long dataId, List<String> fieldNames) {
        List<ModuleField> moduleFields = ModuleFieldCacheUtil.getByIdAndVersion(moduleId, version);
        Map<String, ModuleField> fieldNameFieldMap = moduleFields.stream().collect(Collectors.toMap(ModuleField::getFieldName,
                Function.identity()));
        Map<Long, ModuleField> idFieldMap = moduleFields.stream().collect(Collectors.toMap(ModuleField::getFieldId, Function.identity()));
        List<ModuleFieldData> fieldDataList = fieldDataMapper.getByDataId(dataId);
        ModuleFieldDataCommon fieldDataCommon = fieldDataCommonService.getByDataId(dataId);
        Map<String, Object> commonDataMap = BeanUtil.beanToMap(fieldDataCommon);

        List<ModuleFieldValueBO> fieldValueBOS = new ArrayList<>();
        if (CollUtil.isEmpty(fieldDataList)) {
            return fieldValueBOS;
        }
        if (CollUtil.isEmpty(fieldNames)) {
            for (ModuleFieldData fieldData : fieldDataList) {
                ModuleField field = idFieldMap.get(fieldData.getFieldId());
                if (ObjectUtil.isNull(field)) {
                    continue;
                }
                ModuleFieldValueBO fieldValueBO = new ModuleFieldValueBO();
                fieldValueBO.setModuleId(moduleId);
                fieldValueBO.setFieldId(fieldData.getFieldId());
                fieldValueBO.setType(field.getType());
                fieldValueBO.setFormType(field.getFormType());
                fieldValueBO.setFieldType(field.getFieldType());
                fieldValueBO.setFieldName(field.getFieldName());
                fieldValueBO.setName(field.getName());
                fieldValueBO.setValue(String.valueOf(fieldData.getValue()));
                fieldValueBOS.add(fieldValueBO);
            }

            for (Map.Entry<String, Object> entry : commonDataMap.entrySet()) {
                String fieldName = entry.getKey();
                ModuleField field = fieldNameFieldMap.get(fieldName);
                if (ObjectUtil.isNull(field)) {
                    continue;
                }
                ModuleFieldValueBO fieldValueBO = new ModuleFieldValueBO();
                fieldValueBO.setModuleId(moduleId);
                fieldValueBO.setFieldId(field.getFieldId());
                fieldValueBO.setType(field.getType());
                fieldValueBO.setFormType(field.getFormType());
                fieldValueBO.setFieldType(field.getFieldType());
                fieldValueBO.setFieldName(fieldName);
                fieldValueBO.setName(field.getName());
                if (ObjectUtil.equal(ModuleFieldEnum.DATETIME.getType(), field.getType())) {
                    if (ObjectUtil.isNotNull(entry.getValue())) {
                        fieldValueBO.setValue(DateUtil.formatDateTime((Date) entry.getValue()));
                    }
                } else {
                    fieldValueBO.setValue(Optional.ofNullable(entry.getValue()).map(String::valueOf).orElse(""));
                }
                fieldValueBOS.add(fieldValueBO);
            }
        } else {
            for (String fieldName : fieldNames) {
                ModuleField field = fieldNameFieldMap.get(fieldName);
                if (ObjectUtil.isNull(field)) {
                    continue;
                }
                ModuleFieldData fieldData = fieldDataList.stream().filter(f -> ObjectUtil.equal(field.getFieldId(),
                        f.getFieldId())).findFirst().orElse(null);
                Object value;
                if (ObjectUtil.isNull(fieldData)) {
                    value = commonDataMap.get(fieldName);
                    if (ObjectUtil.equal(ModuleFieldEnum.DATETIME.getType(), field.getType())) {
                        if (ObjectUtil.isNotNull(value)) {
                            value = DateUtil.formatDateTime((Date) value);
                        }
                    }
                } else {
                    value = fieldData.getValue();
                }
                ModuleFieldValueBO fieldValueBO = new ModuleFieldValueBO();
                fieldValueBO.setModuleId(moduleId);
                fieldValueBO.setFieldId(field.getFieldId());
                fieldValueBO.setType(field.getType());
                fieldValueBO.setFormType(field.getFormType());
                fieldValueBO.setFieldType(field.getFieldType());
                fieldValueBO.setFieldName(fieldName);
                fieldValueBO.setName(field.getName());
                fieldValueBO.setValue(String.valueOf(value));
                fieldValueBOS.add(fieldValueBO);
            }
        }
        return fieldValueBOS;
    }

    @Override
    public String queryValue(Long dataId, Long fieldId) {
        ModuleFieldDataCommon fieldDataCommon = fieldDataCommonService.getByDataId(dataId);
        ModuleEntity module = ModuleCacheUtil.getActiveById(fieldDataCommon.getModuleId());
        ModuleField field = ModuleFieldCacheUtil.getByIdAndVersion(module.getModuleId(), fieldId, module.getVersion());
        // 系统字段 TODO:其他字段类型值也需处理
        if (ObjectUtil.equal(0, field.getFieldType())) {
            Map<String, Object> commonDataMap = BeanUtil.beanToMap(fieldDataCommon);
            if (ObjectUtil.equal(ModuleFieldEnum.DATETIME.getType(), field.getType())) {
                return DateUtil.formatDateTime(MapUtil.getDate(commonDataMap, field.getFieldName()));
            }
            return MapUtil.getStr(commonDataMap, field.getFieldName());
        }
        return fieldDataMapper.getValueByDataIdAndFieldId(dataId, fieldId);
    }

    /**
     * 获取字段值map
     *
     * @param dataId 数据ID
     * @return data
     */
    @Override
    public Map<String, Object> queryFieldNameDataMap(Long dataId) {
        List<Map<String, Object>> dataMap = fieldDataMapper.getFieldNameValueByDataId(dataId);
        ModuleFieldDataCommon fieldDataCommon = fieldDataCommonService.getByDataId(dataId);
        Map<String, Object> data = dataMap
                .stream()
                .filter(m -> ObjectUtil.isNotEmpty(m.get("value")))
                .collect(Collectors.toMap(m -> MapUtil.getStr(m, "fieldName"), m -> m.get("value")));
        data.putAll(BeanUtil.beanToMap(fieldDataCommon));
        return data;
    }

    @Override
    public ModuleFieldDataResponseBO queryById(Long dataId, Boolean replaceMask) {
        List<ModuleFieldData> fieldDataList = fieldDataMapper.getByDataId(dataId);
        ModuleFieldDataCommon fieldDataCommon = fieldDataCommonService.getByDataId(dataId);
        if(CollUtil.isEmpty(fieldDataList) || ObjectUtil.isNull(fieldDataCommon)) {
            throw new BusinessException(ModuleCodeEnum.DATA_NOT_EXIST_OR_DELETE);
        }
        ModuleEntity module = ModuleCacheUtil.getActiveById(fieldDataCommon.getModuleId());
        List<ModuleField> moduleFields = fieldService.lambdaQuery()
                .eq(ModuleField::getModuleId, module.getModuleId())
                .eq(ModuleField::getVersion, module.getVersion())
                .list();
        List<Long> userFieldIds = moduleFields.stream()
                .filter(f -> ObjectUtil.equal(ModuleFieldEnum.USER.getType(), f.getType()))
                .map(ModuleField::getFieldId).collect(Collectors.toList());
        List<Long> structureFieldIds = moduleFields.stream()
                .filter(f -> ObjectUtil.equal(ModuleFieldEnum.STRUCTURE.getType(), f.getType()))
                .map(ModuleField::getFieldId).collect(Collectors.toList());
        // 表格字段
        Map<Long, ModuleField> tableId2FieldMap = moduleFields.stream()
                .filter(f -> ObjectUtil.equal(ModuleFieldEnum.DETAIL_TABLE.getType(), f.getType()))
                .collect(Collectors.toMap(ModuleField::getFieldId, Function.identity()));
        // 数据关联字段
        List<ModuleField> unionFields = moduleFields.stream()
                .filter(f -> Arrays.asList(ModuleFieldEnum.DATA_UNION.getType(), ModuleFieldEnum.DATA_UNION_MULTI.getType()).contains(f.getType()))
                .collect(Collectors.toList());
        // 数据关联字段ID
        List<Long> unionFieldIds = unionFields.stream().map(ModuleField::getFieldId).collect(Collectors.toList());
        // 数据关联配置
        Map<Long, ModuleFieldUnion> relateFieldUnionMap = new HashMap<>();
        Map<Long, ModuleEntity> moduleIdMap = new HashMap<>();
        if (CollUtil.isNotEmpty(unionFields)) {
            List<ModuleFieldUnion> fieldUnions = fieldUnionService.lambdaQuery()
                    .in(ModuleFieldUnion::getRelateFieldId, unionFieldIds)
                    .eq(ModuleFieldUnion::getType, 1)
                    .eq(ModuleFieldUnion::getModuleId, module.getModuleId())
                    .eq(ModuleFieldUnion::getVersion, module.getVersion())
                    .list();
            relateFieldUnionMap = fieldUnions.stream().collect(Collectors.toMap(ModuleFieldUnion::getRelateFieldId, Function.identity()));
            List<Long> targetModuleIds = fieldUnions.stream().map(ModuleFieldUnion::getTargetModuleId).distinct().collect(Collectors.toList());
            // 目标模块
            List<ModuleEntity> targetModules = ModuleCacheUtil.getActiveByIds(targetModuleIds);
            moduleIdMap = targetModules.stream().collect(Collectors.toMap(ModuleEntity::getModuleId, Function.identity()));
        }
        for (ModuleFieldData moduleFieldData : fieldDataList) {
            if (CollUtil.isNotEmpty(tableId2FieldMap)) {
                ModuleField field = tableId2FieldMap.get(moduleFieldData.getFieldId());
                if (ObjectUtil.isNotNull(field)) {
                    // 表格内字段
                    Map<String, ModuleField> name2FieldMap = moduleFields.stream()
                            .filter(f -> ObjectUtil.equal(f.getGroupId(), field.getGroupId()))
                            .collect(Collectors.toMap(ModuleField::getFieldName, Function.identity()));
                    // 表格内数据关联字段
                    Map<String, Long> unionFieldNameIdMap = moduleFields.stream()
                            .filter(f -> ObjectUtil.equal(f.getGroupId(), field.getGroupId()))
                            .filter(f -> Arrays.asList(ModuleFieldEnum.DATA_UNION.getType(), ModuleFieldEnum.DATA_UNION_MULTI.getType()).contains(f.getType()))
                            .collect(Collectors.toMap(ModuleField::getFieldName, ModuleField::getFieldId));
                    // 明细表格子字段值
                    String value = moduleFieldData.getValue();
                    List<JSONObject> valueArr = JSON.parseArray(value, JSONObject.class);
                    // 明细表格字段关联数据值
                    List<Long> unionFieldValues = new ArrayList<>();
                    List<ModuleFieldData> mainFileValueList = new ArrayList<>();
                    for (Map.Entry<String, Long> entry : unionFieldNameIdMap.entrySet()) {
                        unionFieldValues.addAll(valueArr.stream().map(val -> val.getString(entry.getKey())).filter(d -> StrUtil.isNotEmpty(d)).flatMap(val -> Arrays.stream(val.split(Const.SEPARATOR))).map(TypeUtils::castToLong).distinct().collect(Collectors.toList()));
                        if (CollUtil.isNotEmpty(unionFieldValues)) {
                            ModuleFieldUnion fieldUnion = relateFieldUnionMap.get(entry.getValue());
                            if (ObjectUtil.isNotNull(fieldUnion)) {
                                ModuleEntity targetModule = moduleIdMap.get(fieldUnion.getTargetModuleId());
                                if (ObjectUtil.isNotNull(targetModule)) {
                                    List<ModuleFieldData> mainFileValues = fieldDataService.lambdaQuery()
                                            .eq(ModuleFieldData::getModuleId, targetModule.getModuleId())
                                            .eq(ModuleFieldData::getFieldId, targetModule.getMainFieldId())
                                            .in(ModuleFieldData::getDataId, unionFieldValues)
                                            .list();
                                    mainFileValueList.addAll(mainFileValues);
                                }
                            }
                        }
                    }
                    for (JSONObject val : valueArr) {
                        for (Map.Entry<String, Object> valEntry : val.entrySet()) {
                            if (ObjectUtil.isNotNull(valEntry.getValue()) && StrUtil.isNotEmpty(valEntry.getValue().toString())) {
                                ModuleField fieldInTable = name2FieldMap.get(valEntry.getKey());
                                if (ObjectUtil.isNotNull(fieldInTable)) {
                                    if (Arrays.asList(ModuleFieldEnum.DATA_UNION.getType(), ModuleFieldEnum.DATA_UNION_MULTI.getType()).contains(fieldInTable.getType())) {
                                        ModuleFieldUnion fieldUnion = relateFieldUnionMap.get(fieldInTable.getFieldId());
                                        if (ObjectUtil.isNotNull(fieldUnion)) {
                                            ModuleEntity targetModule = moduleIdMap.get(fieldUnion.getTargetModuleId());
                                            JSONObject jsonObject = new JSONObject();
                                            List<Long> values = Arrays.stream(valEntry.getValue().toString().split(Const.SEPARATOR)).mapToLong(Long::valueOf).boxed().collect(Collectors.toList());
                                            List<ModuleFieldData> mainFieldData = mainFileValueList.stream()
                                                    .filter(r -> values.contains(r.getDataId()))
                                                    .collect(Collectors.toList());
                                            jsonObject.put("module", targetModule);
                                            jsonObject.put("fieldData", mainFieldData);
                                            jsonObject.put("values", values);
                                            valEntry.setValue(jsonObject);
                                        }

                                    } else if (ObjectUtil.equal(ModuleFieldEnum.USER.getType(), fieldInTable.getType())) {
                                        JSONArray array = new JSONArray();
                                        for (String userId : valEntry.getValue().toString().split(Const.SEPARATOR)) {
                                            SimpleUser user = UserCacheUtil.getSimpleUser(Long.valueOf(userId));
                                            array.add(user);
                                        }
                                        valEntry.setValue(array);
                                    } else if (ObjectUtil.equal(ModuleFieldEnum.STRUCTURE.getType(), fieldInTable.getType())) {
                                        JSONArray array = new JSONArray();
                                        for (String deptId : valEntry.getValue().toString().split(Const.SEPARATOR)) {
                                            JSONObject dept = new JSONObject();
                                            dept.fluentPut("deptId", deptId).fluentPut("deptName", UserCacheUtil.getDeptName(Long.valueOf(deptId)));
                                            array.add(dept);
                                        }
                                        valEntry.setValue(array);
                                    }
                                }
                            }

                        }
                    }
                    moduleFieldData.setValue(JSON.toJSONString(valueArr, SerializerFeature.DisableCircularReferenceDetect));
                }
            }

            if (CollUtil.contains(userFieldIds, moduleFieldData.getFieldId())) {
                JSONArray array = new JSONArray();
                if (StrUtil.isNotEmpty(moduleFieldData.getValue())) {
                    for (String userId : moduleFieldData.getValue().split(Const.SEPARATOR)) {
                        SimpleUser user = UserCacheUtil.getSimpleUser(Long.valueOf(userId));
                        array.add(user);
                    }
                    moduleFieldData.setValue(JSON.toJSONString(array));
                }
            }
            if (CollUtil.contains(structureFieldIds, moduleFieldData.getFieldId())) {
                JSONArray array = new JSONArray();
                if (StrUtil.isNotEmpty(moduleFieldData.getValue())) {
                    for (String deptId : moduleFieldData.getValue().split(Const.SEPARATOR)) {
                        JSONObject dept = new JSONObject();
                        dept.fluentPut("deptId", deptId).fluentPut("deptName", UserCacheUtil.getDeptName(Long.valueOf(deptId)));
                        array.add(dept);
                    }
                    moduleFieldData.setValue(JSON.toJSONString(array));
                }
            }
            if (CollUtil.contains(unionFieldIds, moduleFieldData.getFieldId())) {
                if (StrUtil.isNotEmpty(moduleFieldData.getValue())) {
                    JSONObject jsonObject = new JSONObject();
                    List<Long> values = Arrays.stream(moduleFieldData.getValue().split(Const.SEPARATOR)).mapToLong(Long::valueOf).boxed().collect(Collectors.toList());
                    ModuleFieldUnion fieldUnion = relateFieldUnionMap.get(moduleFieldData.getFieldId());
                    if (ObjectUtil.isNotNull(fieldUnion)) {
                        ModuleEntity targetModule = moduleIdMap.get(fieldUnion.getTargetModuleId());
                        if (ObjectUtil.isNotNull(targetModule)) {
                            List<ModuleFieldData> mainFieldData = fieldDataService.lambdaQuery()
                                    .eq(ModuleFieldData::getModuleId, targetModule.getModuleId())
                                    .eq(ModuleFieldData::getFieldId, targetModule.getMainFieldId())
                                    .in(ModuleFieldData::getDataId, values).list();
                            jsonObject.put("module", targetModule);
                            jsonObject.put("fieldData", mainFieldData);
                            jsonObject.put("values", values);
                            moduleFieldData.setValue(jsonObject.toJSONString());
                        }
                    }
                }
            }
        }
        if (replaceMask) {
            ApplicationContextHolder.getBean(IModuleRoleFieldService.class).replaceMaskFieldValue2(fieldDataList, module.getModuleId());
        }
        FlowExamineRecord examineRecord = examineRecordService.getRecordByModuleIdAndDataId(module.getModuleId(), dataId, 0L);
        ModuleFieldDataResponseBO responseBO = new ModuleFieldDataResponseBO();
        responseBO.setFieldDataList(fieldDataList);
        ModuleFieldDataCommonBO fieldDataCommonBO = BeanUtil.copyProperties(fieldDataCommon , ModuleFieldDataCommonBO.class, "createUserName", "ownerUserName");
        fieldDataCommonBO.setCreateUserName(UserCacheUtil.getUserName(fieldDataCommonBO.getCreateUserId()));
        fieldDataCommonBO.setOwnerUserName(UserCacheUtil.getUserName(fieldDataCommonBO.getOwnerUserId()));
        responseBO.setFieldDataCommon(fieldDataCommonBO);
        responseBO.setModuleId(fieldDataCommon.getModuleId());
        responseBO.setCategoryId(fieldDataCommon.getCategoryId());
        responseBO.setExamineRecord(examineRecord);
        responseBO.setDataId(dataId);
        responseBO.setVersion(fieldDataCommon.getVersion());
        return responseBO;
    }

    /**
     * 构建字段值保存对象
     *
     * @param targetModuleId        目标模块ID
     * @param flowConditionDataList 目标模块添加/更新字段值规则
     * @param currentModule         当前模块
     * @param dataMap               当前模块字段值
     * @return
     */
    @Override
    public ModuleFieldDataSaveBO buildFieldSaveBO(Long targetModuleId, List<FlowConditionData> flowConditionDataList,
                                                  ModuleEntity currentModule, Map<String, Object> dataMap) {
        ModuleEntity module = ModuleCacheUtil.getActiveById(targetModuleId);
        if (ObjectUtil.isNull(module)) {
            return null;
        }
        ModuleFieldDataSaveBO fieldDataSaveBO = new ModuleFieldDataSaveBO();
        fieldDataSaveBO.setModuleId(targetModuleId);
        fieldDataSaveBO.setVersion(module.getVersion());
        // 目标模块的自定义字段
        List<ModuleField> fieldList = fieldService.getByModuleId(targetModuleId, 1);
        Map<String, ModuleField> fieldNameMap = fieldList.stream().collect(Collectors.toMap(ModuleField::getFieldName, Function.identity()));
        for (FlowConditionData conditionData : flowConditionDataList) {
            CommonConditionBO conditionDataBO = JSON.parseObject(conditionData.getSearch(), CommonConditionBO.class);
            String fieldName = conditionDataBO.getFieldName();
            ModuleField field = fieldNameMap.get(fieldName);
            if (ObjectUtil.isNotNull(field)) {
                ModuleFieldData fieldData = new ModuleFieldData();
                fieldData.setFieldId(field.getFieldId());
                fieldData.setFieldName(field.getFieldName());
                fieldData.setCreateTime(DateUtil.date());
                fieldData.setModuleId(targetModuleId);
                // 自定义
                if (ObjectUtil.equal(0, conditionData.getType())) {
                    ModuleFieldEnum fieldEnum = ModuleFieldEnum.parse(field.getType());
                    fieldData.setValue( parJSONString(fieldEnum, conditionDataBO.getValues()));
                } else {
                    // 匹配字段
                    ModuleField currentField = ModuleFieldCacheUtil.getByIdAndVersion(currentModule.getModuleId(), conditionDataBO.getCurrentFieldId(), currentModule.getVersion());

                    if (ObjectUtil.equal(ModuleFieldEnum.DATA_UNION.getType(), field.getType())) {
                        if (ObjectUtil.isNotNull(currentField)) {
                            if (ObjectUtil.equal(ModuleFieldEnum.DATA_UNION.getType(), currentField.getType())) {
                                fieldData.setValue(MapUtil.getStr(dataMap, currentField.getFieldName()));
                            } else {
                                fieldData.setValue(MapUtil.getStr(dataMap, "dataId"));
                            }
                        }
                    } else {
                        if (ObjectUtil.isNotNull(currentField)) {
                            if (ObjectUtil.equal(ModuleFieldEnum.DATA_UNION.getType(), currentField.getType())) {
                                // 获取模块主字段值
                                String mainFieldValue = fieldDataService.queryMainFieldValue(MapUtil.getLong(dataMap, currentField.getFieldName()));
                                fieldData.setValue(mainFieldValue);
                            } else {
                                fieldData.setValue(MapUtil.getStr(dataMap, currentField.getFieldName()));
                            }
                        }
                    }
                }
                fieldDataSaveBO.getFieldDataList().add(fieldData);
            }
        }
        return fieldDataSaveBO;
    }
}
