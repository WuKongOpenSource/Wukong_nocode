package com.kakarote.module.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kakarote.common.constant.Const;
import com.kakarote.common.entity.SimpleUser;
import com.kakarote.common.exception.BusinessException;
import com.kakarote.common.servlet.ApplicationContextHolder;
import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.common.utils.UserUtil;
import com.kakarote.ids.provider.utils.UserCacheUtil;
import com.kakarote.module.common.FlowCacheUtil;
import com.kakarote.module.common.ModuleCacheUtil;
import com.kakarote.module.common.mq.ProducerUtil;
import com.kakarote.module.constant.ActionTypeEnum;
import com.kakarote.module.constant.MessageTagEnum;
import com.kakarote.module.constant.ModuleCodeEnum;
import com.kakarote.module.constant.ModuleFieldEnum;
import com.kakarote.module.entity.BO.*;
import com.kakarote.module.entity.PO.*;
import com.kakarote.module.mapper.ModuleFieldDataMapper;
import com.kakarote.module.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 模块字段值表 服务实现类
 * </p>
 *
 * @author zhangzhiwei
 * @since 2020-12-10
 */
@Service
public class ModuleFieldDataServiceImpl extends BaseServiceImpl<ModuleFieldDataMapper, ModuleFieldData> implements IModuleFieldDataService, IFlowCommonService, IModuleFormulaService {

	@Autowired
	private IModuleFieldDataCommonService fieldDataCommonService;

	@Autowired
	private IModuleTreeDataService treeDataService;

	@Autowired
	private IModuleDataOperationRecordService operationRecordService;

	@Autowired
	private IModuleFieldService fieldService;

	@Autowired
	private ProducerUtil producerUtil;

    @Autowired
    private IModuleFieldFormulaService fieldFormulaService;

	@Autowired
	private ICustomCategoryService categoryService;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delete(ModuleFieldDataDeleteBO dataDeleteBO) {
		List<Long> dataIds = dataDeleteBO.getDataIds();
		ModuleEntity module = ModuleCacheUtil.getByIdAndVersion(dataDeleteBO.getModuleId(), dataDeleteBO.getVersion());
		Long mainFieldId = module.getMainFieldId();
		// 要删除数据的主字段的值
		List<ModuleFieldData> fieldDataList = lambdaQuery()
				.eq(ModuleFieldData::getModuleId, dataDeleteBO.getModuleId())
				.eq(ModuleFieldData::getFieldId, mainFieldId)
				.in(ModuleFieldData::getDataId, dataIds).list();
		Map<Long, ModuleFieldData> dataIdValue = fieldDataList.stream().collect(Collectors.toMap(ModuleFieldData::getDataId, Function.identity()));

		List<ModuleDataOperationRecord> operationRecords = new ArrayList<>();
		dataIdValue.forEach((k, v) -> operationRecords.add(operationRecordService.initEntity(module.getModuleId(), module.getVersion(), k, Optional.ofNullable(v.getValue()).orElse(""), null, null, ActionTypeEnum.DELETE)));
		lambdaUpdate()
                .in(ModuleFieldData::getDataId, dataIds)
                .remove();
		fieldDataCommonService.lambdaUpdate()
                .in(ModuleFieldDataCommon::getDataId, dataIds)
                .remove();
		// 删除树数据
		treeDataService.delete(module.getModuleId(), dataIds);
		operationRecordService.saveBatch(operationRecords);
		MsgBodyBO msgBody = new MsgBodyBO();
		msgBody.setMsgTag(MessageTagEnum.DELETE_DATA);
		msgBody.setMsgKey(IdUtil.simpleUUID());
		msgBody.setModuleId(module.getModuleId());
		msgBody.setVersion(module.getVersion());
		msgBody.setDataIds(dataIds);
		msgBody.setUserId(UserUtil.getUserId());
		msgBody.setDelayTime(2000L);
		producerUtil.sendMsgToTopicOne(msgBody);
		// 删除ES
		deletePage(dataIds, module.getModuleId());
	}

    @Override
    public Map<Long, Object> queryAllFieldIdDataMap(Long dataId) {
        List<ModuleFieldData> fieldDataList = getBaseMapper().getByDataId(dataId);
        ModuleFieldDataCommon fieldDataCommon = fieldDataCommonService.getByDataId(dataId);
        ModuleEntity module = ModuleCacheUtil.getActiveById(fieldDataCommon.getModuleId());
        List<ModuleField> moduleFields = fieldService.getByModuleIdAndVersion(module.getModuleId(), module.getVersion(), null);
        Map<Long, ModuleField> fieldIdMap = moduleFields.stream().collect(Collectors.toMap(ModuleField::getFieldId, Function.identity()));
        Map<String, ModuleField> fieldNameMap = moduleFields.stream().collect(Collectors.toMap(ModuleField::getFieldName, Function.identity()));
        Map<String, Object> commonDataMap = BeanUtil.beanToMap(fieldDataCommon);
		Map<Long, Object> result = new HashMap<>();
		for (ModuleFieldData fieldData : fieldDataList) {
			Long fieldId = fieldData.getFieldId();
			ModuleField field = fieldIdMap.get(fieldId);
			if (ObjectUtil.isNull(field)) {
				continue;
			}
			ModuleFieldEnum fieldEnum = ModuleFieldEnum.parse(field.getType());
			String value = fieldData.getValue();
			if (StrUtil.isNotEmpty(value)) {
				// 下拉选
				if (ObjectUtil.equal(ModuleFieldEnum.SELECT, fieldEnum)) {
					ModuleOptionsBO optionsBO = JSON.parseObject(value, ModuleOptionsBO.class);
					result.put(fieldId, optionsBO.getValue());
				}
				// 数字类型的字段
				if (Arrays.asList(ModuleFieldEnum.NUMBER, ModuleFieldEnum.FLOATNUMBER, ModuleFieldEnum.PERCENT).contains(fieldEnum)) {
					result.put(fieldId,new BigDecimal(value));
				}
			} else {
				result.put(fieldId, value);
			}
		}
        for (Map.Entry<String, ModuleField> entry : fieldNameMap.entrySet()) {
            String fieldName = entry.getKey();
            ModuleField field = entry.getValue();
            if (commonDataMap.containsKey(fieldName)) {
				if (StrUtil.equals("createUserName", fieldName)) {
					result.put(field.getFieldId(), commonDataMap.get("createUserId"));
				} else if (StrUtil.equals("ownerUserName", fieldName)) {
					result.put(field.getFieldId(), commonDataMap.get("ownerUserId"));
				} else {
					result.put(field.getFieldId(), commonDataMap.get(fieldName));
				}
            }
        }
        return result;
    }

	@Override
	public Map<Long, Object> queryFieldIdDataMap(Long dataId) {
		List<ModuleFieldData> fieldDataList = getBaseMapper().getByDataId(dataId);
		Map<Long, Object> result = new HashMap<>();
		for (ModuleFieldData fieldData : fieldDataList) {
			Long fieldId = fieldData.getFieldId();
			String value = fieldData.getValue();
			result.put(fieldId, value);
		}
		return result;
	}

	@Override
	public List<ModuleFieldData> queryFieldData(Long dataId) {
		return getBaseMapper().getByDataId(dataId);
	}

	@Override
	public List<ModuleFieldData> queryFieldData(List<Long> dataIds, List<Long> fieldIds) {
		return lambdaQuery()
				.in(ModuleFieldData::getDataId, dataIds)
				.in(ModuleFieldData::getFieldId, fieldIds)
				.list();
	}

	@Override
	public List<ModuleFieldValueBO> transFieldValue(Long moduleId, Integer version, List<ModuleFieldValueBO> fieldValueBOS) {
		List<ModuleFieldValueBO> result = new ArrayList<>();
		for (ModuleFieldValueBO fieldValueBO : fieldValueBOS) {
			ModuleFieldValueBO fieldData = BeanUtil.copyProperties(fieldValueBO, ModuleFieldValueBO.class);
			result.add(fieldData);
			Integer type = fieldData.getType();
			String value = fieldData.getValue();
			if (StrUtil.isEmpty(value)) {
				continue;
			}
			// 人员
			if (ObjectUtil.equal(ModuleFieldEnum.USER.getType(), type)) {
				JSONArray array = new JSONArray();
				for (String userId : value.split(Const.SEPARATOR)) {
					SimpleUser user = UserCacheUtil.getSimpleUser(Long.valueOf(userId));
					array.add(user);
				}
				value = JSON.toJSONString(array);
			}
			// 部门
			if (ObjectUtil.equal(ModuleFieldEnum.STRUCTURE.getType(), type)) {
				JSONArray array = new JSONArray();
				for (String deptId : value.split(Const.SEPARATOR)) {
					JSONObject dept = new JSONObject();
					dept.fluentPut("deptId", deptId).fluentPut("deptName", UserCacheUtil.getDeptName(Long.valueOf(deptId)));
					array.add(dept);
				}
				value = JSON.toJSONString(array);
			}
			// 数据关联
			if (ObjectUtil.equal(ModuleFieldEnum.DATA_UNION.getType(), type)) {
				String mainFieldValue = this.queryMainFieldValue(Long.valueOf(value));
				value = new JSONObject().fluentPut("dataId", value).fluentPut("value", mainFieldValue).toJSONString();
			}
			// 节点
			if (StrUtil.equals("currentFlowId", fieldData.getFieldName())) {
				Flow flow = FlowCacheUtil.getByIdAndVersion(Long.valueOf(value), version);
				value = JSON.toJSONString(flow);
			}
			fieldData.setValue(value);
		}
		return result;
	}

    @Override
    public String queryMainFieldValue(Long dataId) {
        return getBaseMapper().getMainFieldValue(dataId);
    }

	@Override
	public void saveOrUpdate(ModuleField field, String value, Long dataId, Integer version, Long moduleId) {
		ModuleFieldData fieldData = getBaseMapper().getByDataIdAndFieldId(dataId, field.getFieldId());
		if (ObjectUtil.isNull(fieldData)) {
			fieldData = new ModuleFieldData();
			fieldData.setDataId(dataId);
			fieldData.setFieldId(field.getFieldId());
			fieldData.setFieldName(field.getFieldName());
			fieldData.setModuleId(moduleId);
			fieldData.setVersion(version);
		}
		fieldData.setValue(value);
		fieldData.setCreateTime(DateUtil.date());
		saveOrUpdate(fieldData);
	}

	@Override
	public List<Long> getDataIdsByFieldId(Long fieldId, Long moduleId) {
		return lambdaQuery()
				.select(ModuleFieldData::getDataId)
				.eq(ModuleFieldData::getFieldId, fieldId)
				.eq(ModuleFieldData::getModuleId, moduleId)
				.list().stream()
				.map(ModuleFieldData::getDataId)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void transferOwner(TransferOwnerBO transferOwnerBO) {
		if (CollUtil.isNotEmpty(transferOwnerBO.getDataIds())) {
			List<ModuleFieldDataCommon> dataCommons = fieldDataCommonService.lambdaQuery()
					.eq(ModuleFieldDataCommon::getModuleId,transferOwnerBO.getModuleId())
					.in(ModuleFieldDataCommon::getDataId, transferOwnerBO.getDataIds()).list();

			ModuleEntity module = ModuleCacheUtil.getActiveById(transferOwnerBO.getModuleId());
			// 获取模块主字段值
			List<ModuleFieldData> fieldDataList = lambdaQuery()
					.eq(ModuleFieldData::getModuleId, transferOwnerBO.getModuleId())
					.eq(ModuleFieldData::getFieldId, module.getMainFieldId())
					.in(ModuleFieldData::getDataId, transferOwnerBO.getDataIds()).list();
			Map<Long, String> dataIdValue = fieldDataList.stream().collect(Collectors.toMap(ModuleFieldData::getDataId, ModuleFieldData::getValue));
			Map<Long, Long> dataUserIdMap = dataCommons.stream().collect(Collectors.toMap(ModuleFieldDataCommon::getDataId, ModuleFieldDataCommon:: getOwnerUserId));
			// 保存操作
			List<ModuleDataOperationRecord> operationRecords = new ArrayList<>();
			dataIdValue.forEach((k, v) -> {
				operationRecords.add(operationRecordService.initTransferEntity(module.getModuleId(), module.getVersion(),
                        k, v, dataUserIdMap.get(k), transferOwnerBO.getOwnerUserId(), ActionTypeEnum.TRANSFER));
			});
			operationRecordService.saveBatch(operationRecords);

            fieldDataCommonService.lambdaUpdate()
					.set(ModuleFieldDataCommon::getUpdateTime, DateUtil.now())
					.set(ModuleFieldDataCommon::getOwnerUserId, transferOwnerBO.getOwnerUserId())
                    .eq(ModuleFieldDataCommon::getModuleId, transferOwnerBO.getModuleId())
                    .in(ModuleFieldDataCommon::getDataId, transferOwnerBO.getDataIds()).update();
			Map<String, Object> map = new HashMap<>();
			map.put("ownerUserId", transferOwnerBO.getOwnerUserId());
			map.put("ownerUserName", transferOwnerBO.getOwnerUserId());
			updateField(map, transferOwnerBO.getDataIds(), transferOwnerBO.getModuleId());
		}
	}

	@Override
	public Boolean doubleCheck(DoubleCheckBO checkBO) {
		if (ObjectUtil.isNull(checkBO) || ObjectUtil.isNull(checkBO.getModuleId())
				|| ObjectUtil.isNull(checkBO.getFieldId()) || ObjectUtil.isNull(checkBO.getValue())) {
			return false;
		}
		// 数据为空不进行校验
		if (StrUtil.isEmpty(checkBO.getValue())) {
			return true;
		}
		QueryWrapper<ModuleFieldData> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("module_id", checkBO.getModuleId());
		queryWrapper.eq("field_id", checkBO.getFieldId());
		queryWrapper.eq("value", checkBO.getValue());
		if (ObjectUtil.isNotNull(checkBO.getDataId())) {
			queryWrapper.ne("data_id", checkBO.getDataId());
		}
		Object fieldData = getOne(queryWrapper);
		return ObjectUtil.isNull(fieldData);
	}

    @Override
    public List<ModuleFieldData> calculateFieldFormula(ModuleFieldDataSaveBO fieldDataSaveBO) {
        Long moduleId = fieldDataSaveBO.getModuleId();
        Integer version = fieldDataSaveBO.getVersion();
        List<ModuleField> fieldList = fieldService.getByModuleIdAndVersion(moduleId, version, null);
        // 公式字段列表
        List<ModuleField> fieldFormulaList = fieldList.stream().filter(f -> ObjectUtil.equal(ModuleFieldEnum.FORMULA, ModuleFieldEnum.parse(f.getType()))).collect(Collectors.toList());
        // 字段公式
        List<ModuleFieldFormula> fieldFormulas = fieldFormulaService.getByModuleIdAndVersion(moduleId, version);
        Map<Long, ModuleFieldFormula> fieldIdFormulaMap = fieldFormulas.stream().collect(Collectors.toMap(ModuleFieldFormula::getFieldId, Function.identity()));
        List<ModuleFieldData> fieldDataList = fieldDataSaveBO.getFieldDataList();
        Map<Long, Object> fieldIdValue = fieldDataList.stream().collect(Collectors.toMap(ModuleFieldData::getFieldId, ModuleFieldData::getValue));

        List<ModuleFieldData> result = new ArrayList<>();
        for (ModuleField field : fieldFormulaList) {
            ModuleFieldData fieldData = new ModuleFieldData();
            fieldData.setFieldId(field.getFieldId());
			fieldData.setFieldName(field.getFieldName());
            ModuleFieldFormula fieldFormula = fieldIdFormulaMap.get(field.getFieldId());
            try {
                Object value = calculateFormula(moduleId, version, fieldIdValue, fieldFormula.getFormula());

				if (Arrays.asList(1, 2 ,3).contains(fieldFormula.getType())) {
					if (value instanceof Number) {
						if (value instanceof BigDecimal) {
							BigDecimal bigDecimal = (BigDecimal) value;
							bigDecimal = bigDecimal.setScale(Optional.ofNullable(field.getPrecisions()).orElse(0), BigDecimal.ROUND_HALF_UP);
							fieldData.setValue(bigDecimal.toPlainString());
						} else if (value instanceof Long) {
							BigDecimal bigDecimal = BigDecimal.valueOf(((Long) value).longValue());
							bigDecimal = bigDecimal.setScale(Optional.ofNullable(field.getPrecisions()).orElse(0), BigDecimal.ROUND_HALF_UP);
							fieldData.setValue(bigDecimal.toPlainString());
						} else if (value instanceof Double) {
							BigDecimal bigDecimal = BigDecimal.valueOf(((Double) value).doubleValue());
							bigDecimal = bigDecimal.setScale(Optional.ofNullable(field.getPrecisions()).orElse(0), BigDecimal.ROUND_HALF_UP);
							fieldData.setValue(bigDecimal.toPlainString());
						}
					} else {
						throw new BusinessException(ModuleCodeEnum.FORMULA_VALUE_NOT_MATCH_TYPE);
					}
				} else {
					fieldData.setValue(value.toString());
				}
            } catch (Exception e) {
                e.printStackTrace();
                log.error(e.getMessage());
            }
            result.add(fieldData);
        }
        return result;
    }

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void setDataCategory(SetDataCategoryBO bo) {
		if (CollUtil.isEmpty(bo.getDataIds())) {
			return;
		}
		for (Long dataId : bo.getDataIds()) {
			ModuleFieldDataCommon dataCommon = fieldDataCommonService.getByDataId(dataId);
			CustomCategory category = categoryService.getByCategoryId(bo.getCategoryId(), bo.getVersion());
			if (ObjectUtil.notEqual(bo.getModuleId(), dataCommon.getModuleId())) {
				throw new BusinessException(ModuleCodeEnum.CATEGORY_AND_DATA_NOT_OF_SAME_MODULE);
			}
			Long categoryId = bo.getCategoryId();
			if (ObjectUtil.equal(0, category.getType())) {
				categoryId = null;
			}
			dataCommon.setCategoryId(categoryId);
			dataCommon.setUpdateTime(DateUtil.date());
			fieldDataCommonService.updateById(dataCommon);
			// 更新ES
			Map<String, Object> fieldValueMap = new HashMap<>(1);
			fieldValueMap.put("categoryId", categoryId);
			fieldValueMap.put("updateTime", DateUtil.formatDateTime(dataCommon.getUpdateTime()));
			updateField(fieldValueMap, dataId, category.getModuleId());
		}

	}

	@Override
	public List<ModuleFieldData> getTargetFieldValuesByUnionFieldId(Long fieldId, FieldQueryBO queryBO) {
		// 查到关联模块
		ModuleFieldUnion fieldUnion = ApplicationContextHolder
				.getBean(IModuleFieldUnionService.class)
				.lambdaQuery()
				.eq(ModuleFieldUnion::getModuleId, queryBO.getModuleId())
				.eq(ModuleFieldUnion::getVersion, queryBO.getVersion())
				.eq(ModuleFieldUnion::getRelateFieldId, fieldId)
				.eq(ModuleFieldUnion::getType, 1)
				.one();
		ModuleEntity targetNormalModule = ApplicationContextHolder.getBean(IModuleService.class).getNormal(fieldUnion.getTargetModuleId());
		Long targetMainFieldId = targetNormalModule.getMainFieldId();
		List<ModuleFieldData> mainValueList = this.lambdaQuery()
				.select(ModuleFieldData::getValue, ModuleFieldData::getDataId)
				.eq(ModuleFieldData::getModuleId, targetNormalModule.getModuleId())
				.eq(ModuleFieldData::getFieldId, targetMainFieldId)
				.list();
		return mainValueList;
	}

	@Override
	public String queryMultipleMainFieldValue(String dataIds) {
		if (ObjectUtil.isNull(dataIds) || StrUtil.isEmpty(dataIds)) {
			return "";
		}
		List<String> strings = Arrays.asList(dataIds.split(Const.SEPARATOR));
		List<String> list = getBaseMapper().queryMultipleMainFieldValue(strings);
		String join = String.join(Const.SEPARATOR, list);
		return join;
	}
}
