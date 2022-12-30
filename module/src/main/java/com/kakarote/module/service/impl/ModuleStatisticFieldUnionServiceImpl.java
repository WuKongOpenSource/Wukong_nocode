package com.kakarote.module.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kakarote.common.entity.UserInfo;
import com.kakarote.common.exception.BusinessException;
import com.kakarote.common.result.BasePage;
import com.kakarote.common.result.SystemCodeEnum;
import com.kakarote.common.servlet.ApplicationContextHolder;
import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.common.utils.UserUtil;
import com.kakarote.module.common.ModuleCacheUtil;
import com.kakarote.module.common.ModuleConditionHolder;
import com.kakarote.module.common.ModuleFieldCacheUtil;
import com.kakarote.module.constant.FieldSearchEnum;
import com.kakarote.module.constant.ModuleFieldEnum;
import com.kakarote.module.constant.StatisticTypeEnum;
import com.kakarote.module.entity.BO.CommonUnionConditionBO;
import com.kakarote.module.entity.BO.ConditionDataBO;
import com.kakarote.module.entity.PO.ModuleField;
import com.kakarote.module.entity.PO.ModuleFieldData;
import com.kakarote.module.entity.PO.ModuleFieldUnionCondition;
import com.kakarote.module.entity.PO.ModuleEntity;
import com.kakarote.module.constant.ModuleCodeEnum;
import com.kakarote.module.entity.BO.*;
import com.kakarote.module.entity.PO.*;
import com.kakarote.module.mapper.ModuleStatisticFieldUnionMapper;
import com.kakarote.module.service.*;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.filter.FilterAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.filter.ParsedFilter;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: zjj
 * @date: 2021-05-17 17:46
 */
@Slf4j
@Service
public class ModuleStatisticFieldUnionServiceImpl extends BaseServiceImpl<ModuleStatisticFieldUnionMapper,
		ModuleStatisticFieldUnion> implements IModuleStatisticFieldUnionService, ModulePageService {

	@Autowired
	private IModuleFieldUnionConditionService fieldUnionConditionService;

	@Autowired
	private IModuleFieldService fieldService;

	@Autowired
	private IModuleFieldDataService fieldDataService;

	@Autowired
	private IModuleFieldDataProvider fieldDataProvider;

	@Override
	public ModuleFieldValueBO getStatisticFileValue(Long moduleId, Integer version, Long fieldId, Long dataId) {
		// 统计字段
        ModuleField field = fieldService.getByFieldId(moduleId, fieldId, version);
		if (ObjectUtil.isNull(field)) {
			throw new BusinessException(ModuleCodeEnum.MODULE_FIELD_NOT_FOUND);
		}
		if (ObjectUtil.notEqual(ModuleFieldEnum.STATISTIC.getType(), field.getType())) {
			throw new BusinessException(ModuleCodeEnum.MODULE_ERROR_FIELD_TYPE);
		}

		ModuleFieldValueBO result = new ModuleFieldValueBO();
		result.setModuleId(moduleId);
		result.setFieldId(fieldId);
		result.setType(field.getType());
		result.setFieldName(field.getFieldName());
		result.setValue(null);

		ModuleStatisticFieldUnion statisticFieldUnion = lambdaQuery()
				.eq(ModuleStatisticFieldUnion::getModuleId, moduleId)
				.eq(ModuleStatisticFieldUnion::getVersion, version)
				.eq(ModuleStatisticFieldUnion::getRelateFieldId, fieldId).one();
		if (ObjectUtil.isNotNull(statisticFieldUnion)) {
			Long targetModuleId = statisticFieldUnion.getTargetModuleId();
			Long targetFieldId = statisticFieldUnion.getTargetFieldId();
			Integer statisticType = statisticFieldUnion.getStatisticType();
			ModuleField targetField = null;
			// 统计类型为计数时，目标字段为 dataId
			if (ObjectUtil.equal(StatisticTypeEnum.COUNT.getCode(), statisticType)) {
				targetField = new ModuleField();
				targetField.setFieldName("dataId");
			} else {
				ModuleEntity targetModule = ModuleCacheUtil.getActiveById(targetModuleId);
				targetField = fieldService.getByFieldId(targetModule.getModuleId(), targetFieldId, targetModule.getVersion());
				// 如果统计字段关联的字段不存在，则字段值为 null
				if (ObjectUtil.isNull(targetField)) {
					return result;
				}
			}
			try {
				List<ModuleFieldUnionCondition> fieldUnionConditions = fieldUnionConditionService.lambdaQuery()
						.eq(ModuleFieldUnionCondition::getModuleId, moduleId)
						.eq(ModuleFieldUnionCondition::getVersion, version)
						.eq(ModuleFieldUnionCondition::getRelateFieldId, fieldId)
						.eq(ModuleFieldUnionCondition::getTargetModuleId, targetModuleId).list();
				List<CommonUnionConditionBO> conditionBOS = JSON.parseArray(JSON.toJSONString(fieldUnionConditions), CommonUnionConditionBO.class);
				ModuleConditionHolder.set(conditionBOS);

				ConditionDataBO conditionDataBO = new ConditionDataBO();
				conditionDataBO.setRelatedModuleId(moduleId);
				conditionDataBO.setRelatedVersion(version);
				conditionDataBO.setModuleId(targetModuleId);
				conditionDataBO.setDataId(dataId);
				SearchBO search = new SearchBO();
				search.setPage(1);
				search.setLimit(10000);
				search.setAuthFilter(false);
				String value = null;
				BasePage<Map<String, Object>> dataPage = fieldUnionConditionService.queryModuleDataList(conditionBOS, conditionDataBO, search);

				String targetFieldName = targetField.getFieldName();
				if (CollUtil.isNotEmpty(dataPage.getList())) {
					// 明細表格字段
					if (ObjectUtil.isNotNull(targetField.getGroupId())) {
						ModuleField detailTableField = fieldService.getDetailTableFieldByGroupId(targetModuleId, targetField.getGroupId(), targetField.getVersion());
						if (ObjectUtil.equal(StatisticTypeEnum.COUNT.getCode(), statisticType)) {
							value = String.valueOf(dataPage.getTotal());
						} else {
							List<Map<String, Object>> list = this.filterData(dataPage.getList(), conditionBOS, conditionDataBO, targetField, detailTableField);
							List<JSONObject> tableData = new ArrayList<>();
							for (Map<String, Object> map : list) {
								List<JSONObject> jsonObjects = JSON.parseArray(JSON.toJSONString(map.get(detailTableField.getFieldName())), JSONObject.class);
								tableData.addAll(jsonObjects);
							}
							if (ObjectUtil.equal(StatisticTypeEnum.SUM.getCode(), statisticType)) {
								BigDecimal decimal = tableData.stream().map(m -> m.get(targetFieldName))
										.filter(v -> ObjectUtil.isNotEmpty(v)).map(String::valueOf).map(BigDecimal::new)
										.reduce(BigDecimal.ZERO, BigDecimal::add);
								value = decimal.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();
							} else if (ObjectUtil.equal(StatisticTypeEnum.MAX.getCode(), statisticType)) {
								BigDecimal decimal = tableData.stream().map(m -> m.get(targetFieldName))
										.filter(v -> ObjectUtil.isNotEmpty(v)).map(String::valueOf).map(BigDecimal::new)
										.max((x1,x2) -> x1.compareTo(x2)).get();
								value = decimal.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();
							} else if (ObjectUtil.equal(StatisticTypeEnum.MIN.getCode(), statisticType)) {
								BigDecimal decimal = tableData.stream().map(m -> m.get(targetFieldName))
										.filter(v -> ObjectUtil.isNotEmpty(v)).map(String::valueOf).map(BigDecimal::new)
										.min((x1,x2) -> x1.compareTo(x2)).get();
								value = decimal.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();
							} else if (ObjectUtil.equal(StatisticTypeEnum.AVERAGE.getCode(), statisticType)) {
								BigDecimal decimal = tableData.stream().map(m -> m.get(targetFieldName))
										.filter(v -> ObjectUtil.isNotEmpty(v)).map(String::valueOf).map(BigDecimal::new)
										.reduce(BigDecimal.ZERO, BigDecimal::add)
										.divide(BigDecimal.valueOf(dataPage.getTotal()));
								value = decimal.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();
							}
						}
					} else {
						if (StatisticTypeEnum.aggType(statisticType)) {
							if (ObjectUtil.equal(StatisticTypeEnum.COUNT.getCode(), statisticType)) {
								value = String.valueOf(dataPage.getTotal());
							} else if (ObjectUtil.equal(StatisticTypeEnum.SUM.getCode(), statisticType)) {
								BigDecimal decimal = dataPage.getList().stream().map(m -> m.get(targetFieldName))
										.filter(v -> ObjectUtil.isNotEmpty(v)).map(String::valueOf).map(BigDecimal::new)
										.reduce(BigDecimal.ZERO, BigDecimal::add);
								value = decimal.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();
							} else if (ObjectUtil.equal(StatisticTypeEnum.MAX.getCode(), statisticType)) {
								BigDecimal decimal = dataPage.getList().stream().map(m -> m.get(targetFieldName))
										.filter(v -> ObjectUtil.isNotEmpty(v)).map(String::valueOf).map(BigDecimal::new)
										.max((x1,x2) -> x1.compareTo(x2)).get();
								value = decimal.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();
							} else if (ObjectUtil.equal(StatisticTypeEnum.MIN.getCode(), statisticType)) {
								BigDecimal decimal = dataPage.getList().stream().map(m -> m.get(targetFieldName))
										.filter(v -> ObjectUtil.isNotEmpty(v)).map(String::valueOf).map(BigDecimal::new)
										.min((x1,x2) -> x1.compareTo(x2)).get();
								value = decimal.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();
							} else if (ObjectUtil.equal(StatisticTypeEnum.AVERAGE.getCode(), statisticType)) {
								BigDecimal decimal = dataPage.getList().stream().map(m -> m.get(targetFieldName))
										.filter(v -> ObjectUtil.isNotEmpty(v)).map(String::valueOf).map(BigDecimal::new)
										.reduce(BigDecimal.ZERO, BigDecimal::add)
										.divide(BigDecimal.valueOf(dataPage.getTotal()));
								value = decimal.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();
							}
						} else {
							if (ObjectUtil.equal(StatisticTypeEnum.OLDEST.getCode(), statisticType)) {
								value = dataPage.getList().stream()
										.sorted(Comparator.comparing(m -> MapUtil.getDate(m, "updateTime")))
										.map(m -> MapUtil.getStr(m, targetFieldName)).findFirst().get();
							} else if (ObjectUtil.equal(StatisticTypeEnum.LATEST.getCode(), statisticType)) {
								value = dataPage.getList().stream()
										.sorted((x1, x2) -> MapUtil.getDate(x2, "updateTime").compareTo(MapUtil.getDate(x1, "updateTime")))
										.map(m -> MapUtil.getStr(m, targetFieldName)).findFirst().get();
							} else {
								return null;
							}
						}

					}
				}
				result.setValue(value);
			} catch (Exception e) {
				throw new BusinessException(SystemCodeEnum.SYSTEM_ERROR);
			} finally {
				ModuleConditionHolder.remove();
			}
		}
		return result;
	}

	/**
	 * 用筛选条件筛选已有数据（明细表格内）
	 *
	 * @param dataList
	 * @param conditionBOS
	 * @param conditionDataBO
	 * @return
	 */
	private List<Map<String, Object>> filterData(List<Map<String, Object>> dataList,
												 List<CommonUnionConditionBO> conditionBOS,
												 ConditionDataBO conditionDataBO,
												 ModuleField field,
												 ModuleField detailTableField) {
		Long targetModuleId = conditionDataBO.getModuleId();
		Long relatedModuleId = conditionDataBO.getRelatedModuleId();
		ModuleEntity targetModule = ModuleCacheUtil.getActiveById(targetModuleId);

		Long dataId = conditionDataBO.getDataId();
		List<ModuleField> detailTableFields = fieldService.getFieldByGroupId(field.getModuleId(), field.getGroupId(), field.getVersion());
		List<String> fieldNames = detailTableFields.stream().map(ModuleField::getFieldName).collect(Collectors.toList());
		conditionBOS = conditionBOS.stream().filter(c -> {
			SearchEntityBO searchEntityBO = JSON.parseObject(c.getSearch(), SearchEntityBO.class);
			return fieldNames.contains(searchEntityBO.getFieldName());
		}).collect(Collectors.toList());
		Map<Integer, List<CommonUnionConditionBO>> unionConditionMap = conditionBOS.stream().collect(Collectors.groupingBy(CommonUnionConditionBO::getGroupId));
		for (Map<String, Object> data : dataList) {
			List<JSONObject> jsonList = JSON.parseArray(JSON.toJSONString(data.get(detailTableField.getFieldName())), JSONObject.class);
			List<JSONObject> filterJson = new ArrayList<>();
			if (MapUtil.isNotEmpty(unionConditionMap)) {
				// 遍历表格内数据
				for (JSONObject o : jsonList) {
					for (Map.Entry<Integer, List<CommonUnionConditionBO>> entry : unionConditionMap.entrySet()) {
						Boolean isPass = true;
						for (CommonUnionConditionBO conditionBO : entry.getValue()) {
							Integer type = conditionBO.getType();
							SearchEntityBO searchEntityBO = JSON.parseObject(conditionBO.getSearch(), SearchEntityBO.class);
							String targetFieldName = searchEntityBO.getFieldName();
							// 匹配字段
							if (ObjectUtil.equal(1, type)) {
								ModuleField targetField = fieldService.getByFieldName(targetModuleId, targetFieldName);
								ModuleEntity currentModule = ModuleCacheUtil.getByIdAndVersion(conditionBO.getModuleId(), conditionBO.getVersion());
								Long currentFieldId = searchEntityBO.getCurrentFieldId();
								ModuleField currentField = ModuleFieldCacheUtil.getByIdAndVersion(conditionBO.getModuleId(), currentFieldId, conditionBO.getVersion());
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
								String preValue = fieldDataProvider.queryValue(dataId, currentFieldId);
								if (dataUnionMatchMainField) {
									preValue = fieldDataService.queryMainFieldValue(Long.valueOf(preValue));
								}
								if (mainFieldMatchDataUnion) {
									preValue = String.valueOf(dataId);
								}
								FieldSearchEnum searchEnum = searchEntityBO.getSearchEnum();
								String currentValue = o.getString(targetFieldName);
								switch (searchEnum) {
									case IS:
									case CONTAINS:
									{
										if (!StrUtil.equals(preValue, currentValue)) {
											isPass = false;
										}
										break;
									}
									case IS_NOT:
									case NOT_CONTAINS:
									{
										if (StrUtil.equals(preValue, currentValue)) {
											isPass = false;
										}
										break;
									}
									case IS_NULL:
									{
										if (StrUtil.isNotEmpty(currentValue)) {
											isPass = false;
										}
										break;
									}
									case IS_NOT_NULL:
									{
										if (StrUtil.isEmpty(currentValue)) {
											isPass = false;
										}
										break;
									}
								}
							} else {
								List<String> preValues = searchEntityBO.getValues();
								FieldSearchEnum searchEnum = searchEntityBO.getSearchEnum();
								String currentValue = o.getString(targetFieldName);
								switch (searchEnum) {
									case IS:
									case CONTAINS:
									{
										if (!CollUtil.contains(preValues, currentValue)) {
											isPass = false;
										}
										break;
									}
									case IS_NOT:
									case NOT_CONTAINS:
									{
										if (CollUtil.contains(preValues, currentValue)) {
											isPass = false;
										}
										break;
									}
									case IS_NULL:
									{
										if (StrUtil.isNotEmpty(currentValue)) {
											isPass = false;
										}
										break;
									}
									case IS_NOT_NULL:
									{
										if (StrUtil.isEmpty(currentValue)) {
											isPass = false;
										}
										break;
									}
								}

							}
						}
						if (isPass) {
							filterJson.add(o);
						}
					}
				}
				data.put(detailTableField.getFieldName(), filterJson);
			}
		}
		return dataList;
	}

	private BoolQueryBuilder createQueryBuilder() {
		List<CommonUnionConditionBO> unionConditions = ModuleConditionHolder.get();
		BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
		if (CollUtil.isEmpty(unionConditions)) {
			return queryBuilder;
		}
		Integer model = unionConditions.get(0).getModel();
		// 简单模式
		if (ObjectUtil.equal(0, model)) {
			Map<Integer, List<CommonUnionConditionBO>> unionConditionMap = unionConditions.stream()
					.collect(Collectors.groupingBy(CommonUnionConditionBO::getGroupId));
			unionConditionMap.forEach((k, v) -> {
				List<SearchEntityBO> entityBOS = v.stream()
						.map(d -> fieldUnionConditionService.transConditionJsonToEntity(d))
						.filter(d -> ObjectUtil.isNotNull(d)).collect(Collectors.toList());
				BoolQueryBuilder query = QueryBuilders.boolQuery();
				entityBOS.forEach(e -> {
					search(e, query);
				});
				queryBuilder.should(query);
			});
		}
		// 高级模式
		else if (ObjectUtil.equal(1, model)) {

		}
		return queryBuilder;
	}

	private static final ExecutorService EXECUTOR = new ThreadPoolExecutor(10, 20, 5L, TimeUnit.SECONDS,
			new LinkedBlockingDeque<>(2048), new ThreadPoolExecutor.AbortPolicy());

	@Override
	public void updateStatisticFieldValue(Long targetModuleId, Integer version) {
		// 查看当前模块的统计字段关联关系
		List<ModuleStatisticFieldUnion> fieldUnionList = lambdaQuery()
				.eq(ModuleStatisticFieldUnion::getTargetModuleId, targetModuleId)
				.or(i ->
                        i.eq(ModuleStatisticFieldUnion::getModuleId, targetModuleId)
                                .eq(ModuleStatisticFieldUnion::getVersion, version)
                ).list();
		for (ModuleStatisticFieldUnion fieldUnion : fieldUnionList) {
			Long relateFieldId = fieldUnion.getRelateFieldId();
			Long moduleId = fieldUnion.getModuleId();
			Integer v = fieldUnion.getVersion();
			ModuleEntity module = ModuleCacheUtil.getActiveById(moduleId);
			if (ObjectUtil.isNull(module) || ObjectUtil.notEqual(module.getVersion(), v)) {
				continue;
			}
			Set<Long> dataIds = fieldDataService.lambdaQuery()
					.select(ModuleFieldData::getDataId)
					.eq(ModuleFieldData::getModuleId, moduleId)
					.list()
					.stream()
					.map(ModuleFieldData::getDataId).collect(Collectors.toSet());
			UserInfo userInfo = UserUtil.getUser();
			for (Long dataId : dataIds) {
				EXECUTOR.execute(new UpdateStatisticTask(moduleId, v, relateFieldId, dataId, userInfo));
			}
		}
	}

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatisticFieldValue(Long moduleId, Integer version, Long fieldId, Long dataId) {
        try {
            ModuleFieldValueBO fieldValue = getStatisticFileValue(moduleId, version, fieldId, dataId);
			ModuleField field = ModuleFieldCacheUtil.getByIdAndVersion(moduleId, fieldId, version);
            // 更新数据库中字段的值
            fieldDataService.saveOrUpdate(field, fieldValue.getValue(), dataId, version, moduleId);
            // 更新ES
            Map<String, Object> fieldValueMap = new HashMap<>();
            fieldValueMap.put(fieldValue.getFieldName(), fieldValue.getValue());
            updateField(fieldValueMap, dataId, moduleId);
        } catch (Exception e) {
            log.error("更新统计字段值错误,dataId:{}", dataId, e);
        }
    }

	public static class UpdateStatisticTask implements Runnable {
		private Long moduleId;
		private Integer version;
		private Long fieldId;
		private Long dataId;
		private UserInfo userInfo;

		public UpdateStatisticTask(Long moduleId, Integer version, Long fieldId, Long dataId, UserInfo userInfo) {
			this.moduleId = moduleId;
            this.version = version;
            this.fieldId = fieldId;
			this.dataId = dataId;
			this.userInfo = userInfo;
		}

		@Override
		public void run() {
			try {
				UserUtil.setUser(userInfo);
				ApplicationContextHolder.getBean(IModuleStatisticFieldUnionService.class)
						.updateStatisticFieldValue(moduleId, version, fieldId, dataId);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				UserUtil.removeUser();
			}
		}
	}

	@Override
	public List<ModuleStatisticFieldUnion> getByModuleIdAndVersion(Long moduleId, Integer version) {
		return lambdaQuery()
				.eq(ModuleStatisticFieldUnion::getModuleId, moduleId)
				.eq(ModuleStatisticFieldUnion::getVersion, version).list();
	}
}
