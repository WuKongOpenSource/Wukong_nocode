package com.kakarote.module.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.kakarote.common.result.BasePage;
import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.module.common.ModuleFieldCacheUtil;
import com.kakarote.module.constant.ModuleFieldEnum;
import com.kakarote.module.entity.BO.*;
import com.kakarote.module.entity.PO.*;
import com.kakarote.module.entity.VO.ModuleDefaultValueVO;
import com.kakarote.module.entity.VO.ModuleFieldValueVO;
import com.kakarote.module.mapper.ModuleFieldDefaultMapper;
import com.kakarote.module.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 字段默认值配置表 服务实现类
 * </p>
 *
 * @author zhangzhiwei
 * @since 2021-03-06
 */
@Service
public class ModuleFieldDefaultServiceImpl extends BaseServiceImpl<ModuleFieldDefaultMapper, ModuleFieldDefault> implements IModuleFieldDefaultService, IModuleFormulaService {

	@Autowired
	private IModuleFieldUnionConditionService unionConditionService;

	@Autowired
	private IModuleFieldService fieldService;

    @Autowired
    private IModuleService moduleService;

	@Autowired
	private IModuleFieldUnionService fieldUnionService;

	/**
	 * 查询字段默认值配置
	 *
	 * @param moduleId 模块ID
	 * @return data
	 */
	@Override
	public List<ModuleDefaultValueBO> queryDefaultValueList(Long moduleId, Integer version) {
		List<ModuleFieldDefault> fieldDefaults = this.getByModuleIdAndVersion(moduleId, version);
		// 表格类型默认值填充配置
		List<ModuleFieldUnion> fieldUnions = fieldUnionService.getByModuleIdAndVersion(moduleId, version, 2);
		Map<Long, List<ModuleFieldUnionBO>> fieldUnionGroupByRelateFieldId = fieldUnions.stream()
				.collect(Collectors.groupingBy(ModuleFieldUnion::getRelateFieldId,
						Collectors.mapping(f -> BeanUtil.copyProperties(f, ModuleFieldUnionBO.class), Collectors.toList())));
		return fieldDefaults.stream()
				.map(fieldDefault -> {
					ModuleDefaultValueBO defaultValueBO = JSON.parseObject(JSON.toJSONString(fieldDefault), ModuleDefaultValueBO.class);
					defaultValueBO.setValue(getValueByFieldType(fieldDefault));
					if (ObjectUtil.isNotNull(fieldDefault.getSearch())) {
						defaultValueBO.setFieldUnionConditionList(JSON.parseArray(fieldDefault.getSearch(), ModuleFieldUnionConditionBO.class));
					}
					List<ModuleFieldUnionBO> fieldUnionBOS = fieldUnionGroupByRelateFieldId.get(defaultValueBO.getFieldId());
					defaultValueBO.setFieldUnionList(fieldUnionBOS);
					return defaultValueBO;
				})
				.collect(Collectors.toList());
	}

	/**
	 * 根据字段的类型，转换一下各自value
	 */
	private Object getValueByFieldType(ModuleFieldDefault fieldDefault) {
		String value = fieldDefault.getValue();
		if (ObjectUtil.isNotNull(value)) {
			ModuleField field = fieldService.getByFieldId(fieldDefault.getModuleId(), fieldDefault.getFieldId(), fieldDefault.getVersion());
			if (ObjectUtil.equal(ModuleFieldEnum.ATTENTION, ModuleFieldEnum.parse(field.getType()))) {
				return Integer.parseInt(value);
			} else if (ObjectUtil.equal(ModuleFieldEnum.NUMBER, ModuleFieldEnum.parse(field.getType()))
					|| ObjectUtil.equal(ModuleFieldEnum.FLOATNUMBER, ModuleFieldEnum.parse(field.getType()))
					|| ObjectUtil.equal(ModuleFieldEnum.PERCENT, ModuleFieldEnum.parse(field.getType()))) {
				if (NumberUtil.isInteger(value)) {
					return NumberUtil.parseInt(value);
				} else if (NumberUtil.isDouble(value)) {
					return NumberUtil.parseDouble(value);
				}
			}
			else {
				return fieldDefault.getValue();
			}
		}
		return "";
	}

	@Override
	public List<ModuleDefaultValueVO> values(Long moduleId, Integer version) {
		List<ModuleFieldDefault> fieldDefaults = lambdaQuery()
				.eq(ModuleFieldDefault::getType, 1)
				.eq(ModuleFieldDefault::getModuleId, moduleId)
				.eq(ModuleFieldDefault::getVersion, version)
				.list();
		List<ModuleDefaultValueVO> defaultValueVOS = new ArrayList<>();
		for (ModuleFieldDefault fieldDefault : fieldDefaults) {
			ModuleDefaultValueVO valueVO = new ModuleDefaultValueVO();
			valueVO.setFieldId(fieldDefault.getFieldId());
			// 根据字段的 FieldEnum 类型来赋值
			valueVO.setValue(getValueByFieldType(fieldDefault));
			valueVO.setKey(fieldDefault.getKey());
			defaultValueVOS.add(valueVO);
		}
		return defaultValueVOS;
	}

	@Override
	public List<ModuleFieldValueVO> values(ConditionSearchRequest searchBO) {
		List<ModuleFieldValueVO> fieldValueVOS = new ArrayList<>();
		Long moduleId = searchBO.getModuleId();
		Integer version = searchBO.getVersion();
		Long targetModuleId = searchBO.getTargetModuleId();
		ModuleEntity targetModule= moduleService.getNormal(targetModuleId);
		// 本模块中默认值list
		List<ModuleFieldDefault> fieldDefaults = lambdaQuery()
				.eq(ModuleFieldDefault::getType, 2)
				.eq(ModuleFieldDefault::getModuleId, moduleId)
				.eq(ModuleFieldDefault::getVersion, version)
				.list();
		for (ModuleFieldDefault fieldDefault : fieldDefaults) {
			ModuleDefaultValueVO valueVO = new ModuleDefaultValueVO();
			valueVO.setFieldId(fieldDefault.getFieldId());
			Long targetFieldId = fieldDefault.getTargetFieldId();
			// 目标模块的目标字段
			ModuleField targetField = fieldService.getByFieldId(targetModule.getModuleId(), targetFieldId, targetModule.getVersion());
			if (ObjectUtil.isNull(targetField)) {
				valueVO.setValue(null);
				continue;
			}
			// 组装通用关联条件
			List<CommonUnionConditionBO> conditionBOS = JSON.parseArray(fieldDefault.getSearch(), CommonUnionConditionBO.class);
			conditionBOS.forEach(c -> {
				c.setTargetModuleId(targetModuleId);
				c.setModuleId(moduleId);
				c.setVersion(version);
			});
			ConditionDataBO conditionDataBO = new ConditionDataBO();
			conditionDataBO.setFieldValues(searchBO.getFieldValues());
			conditionDataBO.setModuleId(targetModuleId);
			conditionDataBO.setTargetFieldNames(Collections.singletonList(targetField.getFieldName()));
			SearchBO search = new SearchBO();
			search.setAuthFilter(true);
			search.setPage(searchBO.getPage());
			search.setLimit(searchBO.getLimit());
			// 查询目标模块相关数据
			BasePage<Map<String, Object>> dataList = unionConditionService.queryModuleDataList(conditionBOS, conditionDataBO, search);
			// 只返回目标字段值
			List<Object> values = dataList.getList().stream().map(d -> d.get(targetField.getFieldName())).collect(Collectors.toList());
			valueVO.setValue(values);
			fieldValueVOS.add(valueVO);
		}
		return fieldValueVOS;
	}

	@Override
	public List<ModuleFieldValueVO> values(ModuleFieldDataSaveBO saveBO) {
		List<ModuleFieldValueVO> fieldValueVOS = new ArrayList<>();
		Long moduleId = saveBO.getModuleId();
		Integer version = saveBO.getVersion();
		List<ModuleFieldData> fieldDataList = saveBO.getFieldDataList();
		if (CollUtil.isEmpty(fieldDataList)) {
			return fieldValueVOS;
		}
		Map<Long, Object> fieldIdValue = fieldDataList.stream().collect(Collectors.toMap(ModuleFieldData::getFieldId, ModuleFieldData::getValue));
		List<ModuleFieldDefault> fieldDefaults = lambdaQuery()
				.eq(ModuleFieldDefault::getType, 3)
				.eq(ModuleFieldDefault::getModuleId, moduleId)
				.eq(ModuleFieldDefault::getVersion, version)
				.list();
		for (ModuleFieldDefault fieldDefault : fieldDefaults) {
			try {
				ModuleField field = ModuleFieldCacheUtil.getByIdAndVersion(moduleId, fieldDefault.getFieldId(), version);
				ModuleDefaultValueVO valueVO = new ModuleDefaultValueVO();
				valueVO.setFieldId(fieldDefault.getFieldId());
				valueVO.setFieldName(field.getFieldName());
				Object value = calculateFormula(moduleId, version, fieldIdValue, fieldDefault.getFormula());
				if (ModuleFieldEnum.isDigit(field.getType())) {
					if (value instanceof Number) {
						if (value instanceof BigDecimal) {
							BigDecimal bigDecimal = (BigDecimal) value;
							bigDecimal = bigDecimal.setScale(Optional.ofNullable(field.getPrecisions()).orElse(2), BigDecimal.ROUND_HALF_UP);
							valueVO.setValue(bigDecimal.toPlainString());
						} else if (value instanceof Long) {
							BigDecimal bigDecimal = BigDecimal.valueOf(((Long) value).longValue());
							bigDecimal = bigDecimal.setScale(Optional.ofNullable(field.getPrecisions()).orElse(0), BigDecimal.ROUND_HALF_UP);
							valueVO.setValue(bigDecimal.toPlainString());
						} else if (value instanceof Double) {
							BigDecimal bigDecimal = BigDecimal.valueOf(((Double) value).doubleValue());
							bigDecimal = bigDecimal.setScale(Optional.ofNullable(field.getPrecisions()).orElse(0), BigDecimal.ROUND_HALF_UP);
							valueVO.setValue(bigDecimal.toPlainString());
						}
					} else {
						valueVO.setValue(BigDecimal.ZERO.toString());
					}
				} else {
					valueVO.setValue(value.toString());
				}
				fieldValueVOS.add(valueVO);
			} catch (Exception e) {
				e.printStackTrace();
				log.error(e.getMessage());
			}
		}
		return fieldValueVOS;
	}

	@Override
	public List<ModuleFieldDefault> getByModuleIdAndVersion(Long moduleId, Integer version) {
		return lambdaQuery()
				.eq(ModuleFieldDefault::getModuleId, moduleId)
				.eq(ModuleFieldDefault::getVersion, version)
				.list();
	}
}
