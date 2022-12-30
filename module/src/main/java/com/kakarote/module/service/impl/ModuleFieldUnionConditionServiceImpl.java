package com.kakarote.module.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kakarote.common.exception.BusinessException;
import com.kakarote.common.result.BasePage;
import com.kakarote.common.result.SystemCodeEnum;
import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.common.utils.UserUtil;
import com.kakarote.module.common.ModuleCacheUtil;
import com.kakarote.module.common.ModuleFieldCacheUtil;
import com.kakarote.module.constant.ModuleCodeEnum;
import com.kakarote.module.constant.ModuleFieldEnum;
import com.kakarote.module.entity.BO.CommonUnionConditionBO;
import com.kakarote.module.entity.BO.ConditionDataBO;
import com.kakarote.module.common.ModuleConditionHolder;
import com.kakarote.module.entity.BO.*;
import com.kakarote.module.entity.PO.ModuleEntity;
import com.kakarote.module.entity.PO.ModuleField;
import com.kakarote.module.entity.PO.ModuleFieldUnionCondition;
import com.kakarote.module.mapper.ModuleFieldDataMapper;
import com.kakarote.module.mapper.ModuleFieldUnionConditionMapper;
import com.kakarote.module.service.*;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @description: 数据关联筛选条件
 * @author: zjj
 * @date: 2021-05-08 14:26
 */
@Service
public class ModuleFieldUnionConditionServiceImpl extends BaseServiceImpl<ModuleFieldUnionConditionMapper,
		ModuleFieldUnionCondition> implements IModuleFieldUnionConditionService, ModulePageService, IModuleFormulaService {

	@Autowired
	private ModuleFieldDataMapper fieldDataMapper;
	@Autowired
	private IModuleFieldDataProvider fieldDataProvider;

	@Autowired
	private IModuleFieldService fieldService;

	@Override
	public BasePage<Map<String, Object>> queryModuleDataList(List<CommonUnionConditionBO> conditions, ConditionDataBO dataBO, SearchBO search) {
		search.setModuleId(dataBO.getModuleId());
		search.setFetchFieldNameList(dataBO.getTargetFieldNames());
		ModuleConditionHolder.set(conditions);
        ModuleConditionHolder.setDataBO(dataBO);
        boolean advanceModel = false;
        ModuleFormulaBO formulaBO = new ModuleFormulaBO();
        if (CollUtil.isNotEmpty(conditions) && ObjectUtil.equal(1, conditions.get(0).getModel())) {
            advanceModel = true;
            formulaBO = JSON.parseObject(CollUtil.getFirst(conditions).getSearch(), ModuleFormulaBO.class);
            Map<String, Object> fieldNameValue;
            if (ObjectUtil.isNull(dataBO.getDataId())) {
                fieldNameValue = dataBO.getFieldValues().stream().collect(Collectors.toMap(ModuleFieldValueBO::getFieldName, ModuleFieldValueBO::getValue));
            } else {
                fieldNameValue = fieldDataProvider.queryFieldNameDataMap(dataBO.getDataId());
            }
            // 公式计算参数值
            Map<String, Object> env = buildFormulaEnv(dataBO.getRelatedModuleId(), dataBO.getRelatedVersion(), fieldNameValue);
            formulaBO.setSourceEnv(env);
            search.setPage(1);
            search.setLimit(10000);
        }
		BasePage<Map<String, Object>> data = queryPageList(search, dataBO.getModuleId());
		try {
            if (advanceModel) {
                return this.dealAdvanceModel(data, dataBO.getModuleId(),formulaBO, search.getPage(), search.getLimit());
            }
			return detailData(data, dataBO.getModuleId());
		} catch (Exception e) {
			throw new BusinessException(SystemCodeEnum.SYSTEM_ERROR);
		} finally {
			ModuleConditionHolder.remove();
		}
	}

	@Override
	public BoolQueryBuilder createQueryBuilder(SearchBO searchBO) {
        ConditionDataBO dataBO = ModuleConditionHolder.getDataBO();
        List<CommonUnionConditionBO> unionConditions = ModuleConditionHolder.get();
		BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
		queryBuilder.filter(getModuleQueryBuilders(searchBO.getModuleId()));
		// 在配置的筛选条件基础上，进行二级高级筛选
		dataBO.getSearchList().forEach(search -> search(search, queryBuilder));
		if (CollUtil.isEmpty(unionConditions)) {
			return queryBuilder;
		}
		Integer model = unionConditions.get(0).getModel();
		// 简单模式
		if (ObjectUtil.equal(0, model)) {
			Map<Integer, List<CommonUnionConditionBO>> unionConditionMap = unionConditions.stream()
					.collect(Collectors.groupingBy(CommonUnionConditionBO::getGroupId));
            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
            unionConditionMap.forEach((k, v) -> {
				List<SearchEntityBO> entityBOS = v.stream()
						.map(d -> transConditionJsonToEntity(d))
						.filter(d -> ObjectUtil.isNotNull(d)).collect(Collectors.toList());
				BoolQueryBuilder query = QueryBuilders.boolQuery();
				entityBOS.forEach(e -> {
					search(e, query);
				});
                boolQuery.should(query);
			});
            queryBuilder.must(boolQuery);
        }
		// 高级模式
		else if (ObjectUtil.equal(1, model)) {

		}
		if(searchBO.getAuthFilter()) {
			authQuery(searchBO, queryBuilder);
		}
        return queryBuilder;
	}

	@Override
	public BasePage<Map<String, Object>> searchDataUnionFieldData(ConditionSearchRequest searchBO) {
		Long fieldId = searchBO.getFieldId();
		ModuleField field = fieldService.getByFieldId(searchBO.getModuleId(), fieldId, searchBO.getVersion());
		ModuleFieldEnum fieldEnum = ModuleFieldEnum.parse(field.getType());
		// 当前字段是数据关联字段
		if (!Arrays.asList(ModuleFieldEnum.DATA_UNION, ModuleFieldEnum.DATA_UNION_MULTI).contains(fieldEnum)) {
			throw new BusinessException(ModuleCodeEnum.FIELD_NOT_DATA_UNION);
		}
		List<ModuleFieldUnionCondition> conditions = lambdaQuery()
				.eq(ModuleFieldUnionCondition::getRelateFieldId, fieldId)
				.eq(ModuleFieldUnionCondition::getVersion, searchBO.getVersion())
				.eq(ModuleFieldUnionCondition::getModuleId, searchBO.getModuleId()).list();
		List<CommonUnionConditionBO> conditionBOS = JSON.parseArray(JSON.toJSONString(conditions), CommonUnionConditionBO.class);
		ConditionDataBO conditionDataBO = new ConditionDataBO();
		conditionDataBO.setFieldValues(searchBO.getFieldValues());
		conditionDataBO.setModuleId(searchBO.getTargetModuleId());
		conditionDataBO.setRelatedModuleId(searchBO.getModuleId());
		conditionDataBO.setRelatedVersion(searchBO.getVersion());
		conditionDataBO.setSearchList(searchBO.getSearchList());
		SearchBO search = new SearchBO();
		search.setPage(searchBO.getPage());
		search.setLimit(searchBO.getLimit());
		search.setAuthFilter(true);
		BasePage<Map<String, Object>> dataList = this.queryModuleDataList(conditionBOS, conditionDataBO, search);
		return dataList;
	}

	@Override
	public SearchEntityBO transConditionJsonToEntity(CommonUnionConditionBO unionCondition) {
		Integer type = unionCondition.getType();
		JSONObject condition = JSON.parseObject(unionCondition.getSearch());
		SearchEntityBO entityBO = JSON.parseObject(unionCondition.getSearch(), SearchEntityBO.class);
		String leftContains="[";
		// 目标模块的信息
		Long targetModuleId = unionCondition.getTargetModuleId();
		String targetFieldName = entityBO.getFieldName();
		ModuleEntity targetModule = ModuleCacheUtil.getActiveById(targetModuleId);
		ModuleField targetField = fieldService.getByFieldName(targetModuleId, targetFieldName);
		// 明細表格字段
		if (ObjectUtil.isNotNull(targetField.getGroupId())) {
			ModuleField detailTableFiled = fieldService.getDetailTableFieldByGroupId(targetModuleId, targetField.getGroupId(), targetField.getVersion());
			ModuleSimpleFieldBO simpleDetailTableFiled = BeanUtil.copyProperties(detailTableFiled, ModuleSimpleFieldBO.class);
			entityBO.setDetailTableField(simpleDetailTableFiled);
		}
		// 匹配字段
		if (ObjectUtil.equal(1, type)) {
			// 当前模块信息
			Long moduleId = unionCondition.getModuleId();
			Integer version = unionCondition.getVersion();
			ModuleEntity currentModule = ModuleCacheUtil.getByIdAndVersion(moduleId, version);
			Long currentFieldId = condition.getLong("currentFieldId");
			ModuleField currentField = ModuleFieldCacheUtil.getByIdAndVersion(moduleId, currentFieldId, version);
			// 如果当前字段被删除，则此筛选条件不生效
			if (ObjectUtil.isNull(currentField)) {
				return null;
			}
			Boolean dataUnionMatchMainField = false;
			if (ObjectUtil.equal(ModuleFieldEnum.DATA_UNION.getType(), currentField.getType())
					&& ObjectUtil.equal(targetField.getFieldId(), targetModule.getMainFieldId())) {
				dataUnionMatchMainField = true;
			}
			Boolean mainFieldMatchDataUnion = false;
			if (ObjectUtil.equal(ModuleFieldEnum.DATA_UNION.getType(), targetField.getType())
					&& ObjectUtil.equal(currentFieldId, currentModule.getMainFieldId())) {
				mainFieldMatchDataUnion = true;
			}
			ConditionDataBO dataBO = ModuleConditionHolder.getDataBO();
			if (ObjectUtil.isNotNull(dataBO)) {
				Long dataId = dataBO.getDataId();
				// 如果数据ID不为空，则通过数据ID获取条件中字段的值
				if (ObjectUtil.isNotNull(dataId)) {
					String value = fieldDataProvider.queryValue(dataId, currentFieldId);
					if (ObjectUtil.isNull(value)) {
						entityBO.setValues(null);
					} else {
						if (dataUnionMatchMainField) {
							value = fieldDataMapper.getMainFieldValue(Long.valueOf(value));
						}
						if (mainFieldMatchDataUnion) {
							value = String.valueOf(dataId);
						}
					}
					if (StrUtil.isEmpty(value)) {
						entityBO.setValues(null);
					} else {
						if (value.contains(leftContains)) {
							List<String> values = JSON.parseArray(JSON.toJSONString(value)).toJavaList(String.class);
							entityBO.setValues(values);
						} else {
							entityBO.setValues(Arrays.asList(value));
						}
					}
				} else {
					List<ModuleFieldValueBO> fieldValueBOS = dataBO.getFieldValues();
					ModuleFieldValueBO fieldValueBO = fieldValueBOS.stream()
							.filter(f -> ObjectUtil.equal(currentFieldId, f.getFieldId()))
							.findFirst().orElse(null);
					if (ObjectUtil.isNotNull(fieldValueBO) && StrUtil.isNotEmpty(fieldValueBO.getValue())) {
						if (fieldValueBO.getValue().contains(leftContains)) {
							List<String> values = JSON.parseArray(fieldValueBO.getValue()).toJavaList(String.class);
							entityBO.setValues(values);
						} else {
							if (dataUnionMatchMainField) {
								String value = fieldDataMapper.getMainFieldValue(Long.valueOf(fieldValueBO.getValue()));
								if (StrUtil.isEmpty(value)) {
									entityBO.setValues(null);
								} else {
									entityBO.setValues(Arrays.asList(value));
								}
							} else {
								entityBO.setValues(Arrays.asList(fieldValueBO.getValue()));
							}
						}
					}
				}
			}
			Long dataId = ModuleConditionHolder.getDataId();
			// 如果数据ID不为空，则通过数据ID获取条件中字段的值
			if (ObjectUtil.isNotNull(dataId)) {
				String value = fieldDataProvider.queryValue(dataId, currentFieldId);
				if (ObjectUtil.isNull(value)) {
					entityBO.setValues(null);
				} else {
					if (dataUnionMatchMainField) {
						value = fieldDataMapper.getMainFieldValue(Long.valueOf(value));
					}
					if (mainFieldMatchDataUnion) {
						value = String.valueOf(dataId);
					}
				}
				if (StrUtil.isEmpty(value)) {
					entityBO.setValues(null);
				} else {
					if (value.contains(leftContains)) {
						List<String> values = JSON.parseArray(JSON.toJSONString(value)).toJavaList(String.class);
						entityBO.setValues(values);
					} else {
						entityBO.setValues(Arrays.asList(value));
					}
				}
			}
		}
		return entityBO;
	}

	@Override
	public List<ModuleFieldUnionCondition> getByModuleIdAndVersion(Long moduleId, Integer version) {
		return lambdaQuery()
				.eq(ModuleFieldUnionCondition::getModuleId, moduleId)
				.eq(ModuleFieldUnionCondition::getVersion, version).list();
	}
}
