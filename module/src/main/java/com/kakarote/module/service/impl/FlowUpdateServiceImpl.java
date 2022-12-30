package com.kakarote.module.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.kakarote.common.entity.UserInfo;
import com.kakarote.common.result.BasePage;
import com.kakarote.common.servlet.ApplicationContextHolder;
import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.module.common.ModuleCacheUtil;
import com.kakarote.module.entity.BO.CommonUnionConditionBO;
import com.kakarote.module.entity.BO.ConditionDataBO;
import com.kakarote.module.constant.*;
import com.kakarote.module.entity.BO.*;
import com.kakarote.module.entity.PO.*;
import com.kakarote.module.entity.VO.FlowVO;
import com.kakarote.module.entity.proto.FieldDataSnapshotBuf;
import com.kakarote.module.mapper.FlowUpdateMapper;
import com.kakarote.module.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @description: 更新数据节点 服务实现类
 * @author: zjj
 * @date: 2021-05-25 15:56
 */
@Service("updateService")
public class FlowUpdateServiceImpl extends BaseServiceImpl<FlowUpdateMapper, FlowUpdate> implements IFlowUpdateService, IFlowTypeService, IFlowCommonService {

	@Autowired
	private IFlowConditionDataService conditionDataService;

	@Autowired
	private IFlowService flowService;

	@Autowired
	private IFlowProvider flowProvider;

	@Autowired
	private IFlowDataDealRecordService dealRecordService;

	@Autowired
	private IModuleService moduleService;

	@Autowired
	private IModuleFieldDataService fieldDataService;

	@Autowired
	private IModuleFieldDataProvider fieldDataProvider;

	@Autowired
	private IModuleFieldUnionConditionService unionConditionService;

	@Autowired
	private IModuleFieldService fieldService;

	@Autowired
	private IModuleFieldDataCommonService dataCommonService;

	@Override
	public FlowUpdate getByFlowId(Long flowId) {
		return lambdaQuery().eq(FlowUpdate::getFlowId, flowId).one();
	}

	@Override
	public List<FlowUpdate> getByModuleIdAndVersion(Long moduleId, Integer version, Long flowMetaDataId) {
		return lambdaQuery().eq(FlowUpdate::getModuleId, moduleId)
                .eq(FlowUpdate::getVersion, version)
                .eq(FlowUpdate::getFlowMetadataId, flowMetaDataId)
                .list();
	}

	@Override
	public void queryExamineData(Map<String, Object> map, String batchId) {
		List<FlowUpdate> flowUpdates = lambdaQuery().eq(FlowUpdate::getBatchId, batchId).list();
		if (CollUtil.isEmpty(flowUpdates)) {
			return;
		}
		Map<String, FlowUpdate> flowUpdateMap = flowUpdates.stream()
				.collect(Collectors.toMap(flowUpdate -> flowUpdate.getFlowId().toString(), flowUpdate -> flowUpdate));
		map.put(FlowTypeEnum.UPDATE.name(), flowUpdateMap);
		List<FlowConditionData> conditionDataList = conditionDataService.lambdaQuery()
				.in(FlowConditionData::getRuleType, FlowRuleTypeEnum.UPDATE_CONDITION.getType(),
						FlowRuleTypeEnum.UPDATE_UPDATE.getType(), FlowRuleTypeEnum.UPDATE_INSERT.getType())
				.eq(FlowConditionData::getBatchId, batchId).list();
		Map<String, List<FlowConditionData>> flowConditionMap = conditionDataList.stream()
				.collect(Collectors.groupingBy(d -> FlowRuleTypeEnum.parse(d.getRuleType()).name()));
		map.putAll(flowConditionMap);
	}

	@Override
	public FlowVO createFlowInfo(Map<String, Object> map, Flow flow, List<UserInfo> userInfos, Long ownerUserId) {
		FlowVO flowVO = new FlowVO();
		flowVO.setFlowId(flow.getFlowId());
		flowVO.setFlowName(flow.getFlowName());
		flowVO.setContent(flow.getContent());
		flowVO.setFlowType(flow.getFlowType());
		flowVO.setType(flow.getType());
		flowVO.setSort(flow.getPriority());
		Map<String, FlowUpdate> flowUpdateMap = MapUtil.get(map, FlowTypeEnum.UPDATE.name(), Map.class);
		FlowUpdate flowUpdate = flowUpdateMap.get(flow.getFlowId().toString());
		FlowVO.FlowUpdateData flowUpdateData = new FlowVO.FlowUpdateData();
		flowUpdateData.setIsInsert(flowUpdate.getIsInsert());
		flowUpdateData.setTargetModuleId(flowUpdate.getTargetModuleId());
		//  search condition
		List<FlowConditionData> conditions = MapUtil.get(map, FlowRuleTypeEnum.UPDATE_CONDITION.name(), List.class);
		flowUpdateData.setSearchRules(filterAndTransBO(conditions, flow.getFlowId()));
		// update condition
		List<FlowConditionData> updateConditions = MapUtil.get(map, FlowRuleTypeEnum.UPDATE_UPDATE.name(), List.class);
		flowUpdateData.setUpdateRules(filterAndTransBO(updateConditions, flow.getFlowId()));
		// insert condition
		List<FlowConditionData> insertConditions = MapUtil.get(map, FlowRuleTypeEnum.UPDATE_INSERT.name(), List.class);
		flowUpdateData.setInsertRules(filterAndTransBO(insertConditions, flow.getFlowId()));
		flowVO.setData(JSON.parseObject(JSON.toJSONString(flowUpdateData)));
		return flowVO;
	}

	private List<FlowConditionBO> filterAndTransBO(List<FlowConditionData> conditionData, Long flowId) {
		if(CollUtil.isEmpty(conditionData)) {
			return null;
		}
		return conditionData.stream().filter(d -> ObjectUtil.equal(flowId, d.getTypeId()))
				.map(d -> {
                    FlowConditionBO conditionBO = new FlowConditionBO();
					BeanUtils.copyProperties(d, conditionBO);
					conditionBO.setSearch(JSON.parseObject(d.getSearch(), CommonConditionBO.class));
					return conditionBO;
				}).collect(Collectors.toList());
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void dealData(FlowExamineRecord record, Long flowId) {
		Flow currentFlow = flowService.getByFlowId(flowId, record.getVersion());
		FlowDataDealRecord dataDealRecord = dealRecordService.getMainByRecordIdAndFlowId(record.getRecordId(), flowId);
		// 当前节点未处理，直接结束不执行下一个节点
		if (ObjectUtil.isNull(dataDealRecord)) {
			FlowUpdate flowUpdate = getByFlowId(currentFlow.getFlowId());
			Long targetModuleId = flowUpdate.getTargetModuleId();
            ModuleEntity targetModule = moduleService.getNormal(targetModuleId);
			DoubleCheckResultBO doubleCheckResultBO = null;
			FieldDataSnapshotBuf.FieldDataSnapshot sourceSnapshot = null;
			Integer flowStatus = FlowStatusEnum.PASS.getStatus();
			if (ObjectUtil.isNotNull(targetModule)) {
				// 当前模块
				ModuleEntity currentModule = ModuleCacheUtil.getByIdAndVersion(record.getModuleId(), record.getVersion());
				// 当前模块字段数据值
				Map<String, Object> currentFieldData = fieldDataProvider.queryFieldNameDataMap(record.getDataId());
				// 更新节点筛选条件
				List<FlowConditionData> conditionDataList = conditionDataService.lambdaQuery()
						.eq(FlowConditionData::getRuleType, FlowRuleTypeEnum.UPDATE_CONDITION.getType())
						.eq(FlowConditionData::getTypeId, flowId)
						.eq(FlowConditionData::getBatchId, flowUpdate.getBatchId()).list();
				List<CommonUnionConditionBO> conditionBOS = JSON.parseArray(JSON.toJSONString(conditionDataList), CommonUnionConditionBO.class);
				ConditionDataBO conditionDataBO = new ConditionDataBO();
				conditionDataBO.setRelatedModuleId(record.getModuleId());
				conditionDataBO.setRelatedVersion(record.getVersion());
				conditionDataBO.setModuleId(targetModuleId);
				conditionDataBO.setDataId(record.getDataId());
				SearchBO search = new SearchBO();
				search.setPage(1);
				search.setLimit(10000);
				// 根据筛选条件获取数据
				BasePage<Map<String, Object>> dataPage = unionConditionService.queryModuleDataList(conditionBOS, conditionDataBO, search);

				// 如果有数据则更新，没有数据就添加
				if (CollUtil.isEmpty(dataPage.getList())) {
					// 更新节点插入条件
					List<FlowConditionData> insertCondition = conditionDataService.lambdaQuery()
							.eq(FlowConditionData::getRuleType, FlowRuleTypeEnum.UPDATE_INSERT.getType())
							.eq(FlowConditionData::getTypeId, flowId)
							.eq(FlowConditionData::getBatchId, flowUpdate.getBatchId()).list();
					if (CollUtil.isNotEmpty(insertCondition)) {
						// 根据创建规则构建字段数据保存对象并保存目标模块数据
						ModuleFieldDataSaveBO dataSaveBO = fieldDataProvider.buildFieldSaveBO(targetModuleId, insertCondition, currentModule, currentFieldData);
						if (ObjectUtil.isNotNull(dataSaveBO)) {
							doubleCheckResultBO = this.saveAndReturnDoubleCheckResult(dataSaveBO);
						}
					}
				} else {
					// 更新节点更新条件
					List<FlowConditionData> updateCondition = conditionDataService.lambdaQuery()
							.eq(FlowConditionData::getRuleType, FlowRuleTypeEnum.UPDATE_UPDATE.getType())
							.eq(FlowConditionData::getTypeId, flowId)
							.eq(FlowConditionData::getBatchId, flowUpdate.getBatchId()).list();
					// 所有符合条件的数据ID
					Set<Long> dataIds = dataPage.getList().stream().map(m -> MapUtil.getLong(m, "dataId")).collect(Collectors.toSet());
					doubleCheckResultBO = this.updateAndReturnDoubleCheckResult(updateCondition,dataIds,currentFieldData, targetModule);
				}
				sourceSnapshot = buildSnapshot(dataPage.getList());
			} else {
				flowStatus = FlowStatusEnum.FAILED.getStatus();
			}
			dataDealRecord = new FlowDataDealRecord();
			dataDealRecord.setIsMain(true);
			dataDealRecord.setRecordId(record.getRecordId());
			dataDealRecord.setFlowId(currentFlow.getFlowId());
			dataDealRecord.setDataId(record.getDataId());
			dataDealRecord.setUserId(record.getCreateUserId());
			dataDealRecord.setCreateUserId(record.getCreateUserId());
			dataDealRecord.setCreateTime(DateUtil.date());
			dataDealRecord.setUpdateTime(DateUtil.date());
			dataDealRecord.setModuleId(record.getModuleId());
			dataDealRecord.setVersion(record.getVersion());
			dataDealRecord.setFlowType(FlowTypeEnum.UPDATE.getType());
			dataDealRecord.setBatchId(IdUtil.simpleUUID());
            dataDealRecord.setExtData(JSON.toJSONString(doubleCheckResultBO));
			if (ObjectUtil.isNotNull(sourceSnapshot)) {
				dataDealRecord.setSourceData(sourceSnapshot.toByteArray());
			}
			if (ObjectUtil.isNotNull(doubleCheckResultBO)) {
				if (ObjectUtil.isNotNull(doubleCheckResultBO.getCurrentSnapshot())) {
					dataDealRecord.setCurrentData(doubleCheckResultBO.getCurrentSnapshot().toByteArray());
				}
				if (!doubleCheckResultBO.getIsPass()) {
					flowStatus = FlowStatusEnum.FAILED.getStatus();
				}
			}
			dataDealRecord.setFlowStatus(flowStatus);
			dealRecordService.save(dataDealRecord);
			// 没有匹配的数据
			if (ObjectUtil.equal(FlowStatusEnum.PASS.getStatus(), flowStatus)) {
				// 更新数据的流程状态
				if (ObjectUtil.equal(FlowMetadataTypeEnum.SYSTEM.getType(), record.getFlowMetadataType())) {
					ModuleFieldDataCommon dataCommon = dataCommonService.getByDataId(record.getDataId());
					dataCommon.setFlowStatus(flowStatus);
					dataCommon.setFlowType(FlowTypeEnum.UPDATE.getType());
					dataCommon.setType(1);
					dataCommon.setCurrentFlowId(flowId);
					dataCommon.setUpdateTime(DateUtil.date());
					dataCommonService.updateById(dataCommon);
					savePage(record.getDataId(), record.getModuleId(), record.getVersion());
				}
			} else {
				// 验重失败，更新审核状态并结束
				updateExamineStatus(record, currentFlow, FlowStatusEnum.FAILED);
				return;
			}
		}
		// 获取当前节点的下一个节点，继续处理数据
		Flow nextFlow = flowProvider.getNextOrUpperNextFlow(currentFlow);
		if (ObjectUtil.isNull(nextFlow)) {
			updateExamineStatus(record, currentFlow, FlowStatusEnum.PASS);
			return;
		}
		FlowTypeEnum flowTypeEnum = FlowTypeEnum.parse(nextFlow.getFlowType());
		IFlowTypeService flowTypeService = ApplicationContextHolder.getBean(flowTypeEnum.getServiceName());
		flowTypeService.dealData(record, nextFlow.getFlowId());
	}

    /**
     * 插入操作字段值
     *
     * @param fieldDataSaveBO
     * @return 返回插入结果和重复的字段信息
     */
    @Override
    public DoubleCheckResultBO saveAndReturnDoubleCheckResult(ModuleFieldDataSaveBO fieldDataSaveBO) {
        DoubleCheckResultBO result = new DoubleCheckResultBO();
        Long moduleId = fieldDataSaveBO.getModuleId();
        result.setModuleId(moduleId);
        if (ObjectUtil.isNotNull(fieldDataSaveBO)) {
            List<ModuleField> fieldList = fieldService.getByModuleId(moduleId, null);
            Map<Long, ModuleField> fieldMap = fieldList.stream().collect(Collectors.toMap(ModuleField::getFieldId, Function.identity()));
            List<ModuleFieldValueBO> repeatFieldValueList = new ArrayList<>();
            for (ModuleFieldData fieldData : fieldDataSaveBO.getFieldDataList()) {
                ModuleField field = fieldMap.get(fieldData.getFieldId());
                // 字段值验重
                if (ObjectUtil.equal(1, field.getIsUnique())) {
                    DoubleCheckBO doubleCheckBO = new DoubleCheckBO();
                    doubleCheckBO.setModuleId(moduleId);
                    doubleCheckBO.setFieldId(fieldData.getFieldId());
                    doubleCheckBO.setValue(fieldData.getValue());
                    Boolean pass = fieldDataService.doubleCheck(doubleCheckBO);
                    if (!pass) {
                        ModuleFieldValueBO fieldValueBO = new ModuleFieldValueBO();
                        fieldValueBO.setFieldId(field.getFieldId());
                        fieldValueBO.setType(field.getType());
                        fieldValueBO.setFieldName(field.getFieldName());
                        fieldValueBO.setName(field.getName());
                        fieldValueBO.setValue(fieldData.getValue());
                        repeatFieldValueList.add(fieldValueBO);
                    }
                }
            }
            if (CollUtil.isEmpty(repeatFieldValueList)) {
                Long dataId = fieldDataProvider.save(fieldDataSaveBO);
                Map<String, Object> dataMap = fieldDataProvider.queryFieldNameDataMap(dataId);
                FieldDataSnapshotBuf.FieldDataSnapshot currentSnapshot = buildSnapshot(Arrays.asList(dataMap));
                result.setCurrentSnapshot(currentSnapshot);
                result.setIsPass(true);
            } else {
                result.setIsPass(false);
            }
            result.setRepeatFieldValueList(repeatFieldValueList);
        }
        return result;
    }

    /**
     * 更新操作字段值
     *
     * @param updateCondition
     * @param dataIds
     * @param currentFieldData
     * @param module
     * @return 返回插更新结果和重复的字段信息
     */
    @Override
    public DoubleCheckResultBO updateAndReturnDoubleCheckResult(List<FlowConditionData> updateCondition,
                                                                 Set<Long> dataIds,
                                                                 Map<String, Object> currentFieldData,
                                                                 ModuleEntity module) {
        DoubleCheckResultBO result = new DoubleCheckResultBO();
        result.setModuleId(module.getModuleId());
        if (CollUtil.isEmpty(updateCondition)) {
            result.setIsPass(true);
            return result;
        }
        // 更新MYSQL数据
        List<ModuleField> targetFields = fieldService.getByModuleId(module.getModuleId(), null);
        Map<String, ModuleField> fieldNameFieldMap = targetFields.stream().collect(Collectors.toMap(ModuleField::getFieldName, Function.identity()));
        List<ModuleFieldValueBO> repeatFieldValueList = new ArrayList<>();
        // 待更新的字段值
        List<ModuleFieldData> fieldDataList = new ArrayList<>();
        List<Long> toUpdateFieldIdList = new ArrayList<>();
        boolean isPass = true;
        for (FlowConditionData conditionData : updateCondition) {
            // 0 自定义,1 匹配字段
            Integer type = conditionData.getType();
            SearchEntityBO entityBO = JSON.parseObject(conditionData.getSearch(), SearchEntityBO.class);
			ModuleField field = fieldNameFieldMap.get(entityBO.getFieldName());
			Long fieldId = field.getFieldId();
			toUpdateFieldIdList.add(fieldId);
			Boolean conditionPass = true;
            String value = "";
            if (ObjectUtil.equal(0, type)) {
				ModuleFieldEnum fieldEnum = ModuleFieldEnum.parse(field.getType());
				value = parJSONString(fieldEnum, entityBO.getValues());
            } else {
                ModuleField currentField = fieldService.getByFieldId(conditionData.getModuleId(), entityBO.getCurrentFieldId(), conditionData.getVersion());
				if (ObjectUtil.equal(currentField.getType(), ModuleFieldEnum.DATETIME.getType())) {
					Object objectValue = currentFieldData.get(currentField.getFieldName());
					if (objectValue instanceof Date) {
						value = DateUtil.formatDateTime((Date) currentFieldData.get(currentField.getFieldName()));
					} else {
						value = MapUtil.getStr(currentFieldData, currentField.getFieldName());
					}
				} else {
					value = MapUtil.getStr(currentFieldData, currentField.getFieldName());
				}
            }

            // 如果是更新多条数据且值都一样，则验重失败
            if (ObjectUtil.equal(1, field.getIsUnique())) {
                if (dataIds.size() > 1) {
                    conditionPass = false;
                } else {
                    DoubleCheckBO doubleCheckBO = new DoubleCheckBO();
                    doubleCheckBO.setModuleId(module.getModuleId());
                    doubleCheckBO.setFieldId(fieldId);
                    doubleCheckBO.setValue(value);
                    conditionPass = fieldDataService.doubleCheck(doubleCheckBO);
                }
                if (!conditionPass) {
                    isPass = false;
                    ModuleFieldValueBO fieldValueBO = new ModuleFieldValueBO();
                    fieldValueBO.setFieldId(fieldId);
					fieldValueBO.setType(field.getType());
                    fieldValueBO.setFieldName(field.getFieldName());
                    fieldValueBO.setName(field.getName());
                    fieldValueBO.setValue(value);
                    repeatFieldValueList.add(fieldValueBO);
                }
            }
            for (Long dataId : dataIds) {
                ModuleFieldData fieldData = new ModuleFieldData();
                fieldData.setDataId(dataId);
                fieldData.setFieldId(fieldId);
				fieldData.setFieldName(field.getFieldName());
                fieldData.setModuleId(module.getModuleId());
                fieldData.setVersion(module.getVersion());
                fieldData.setValue(value);
                fieldData.setCreateTime(DateUtil.date());
                fieldDataList.add(fieldData);
            }
        }
        result.setIsPass(isPass);
        result.setRepeatFieldValueList(repeatFieldValueList);
        if (isPass) {
            // 先删除历史的字段值，再插入新的字段值
            fieldDataService.lambdaUpdate()
                    .eq(ModuleFieldData::getModuleId, module.getModuleId())
                    .in(ModuleFieldData::getFieldId, toUpdateFieldIdList)
                    .in(ModuleFieldData::getDataId, dataIds)
                    .remove();
            fieldDataService.saveBatch(fieldDataList);
            // 更新ES数据
            dataIds.forEach(dataId -> savePage(dataId, module.getModuleId(), module.getVersion()));
            BasePage<Map<String, Object>> currentDataList = queryByDataIds(dataIds, module.getModuleId());
            FieldDataSnapshotBuf.FieldDataSnapshot currentSnapshot = buildSnapshot(currentDataList.getList());
            result.setCurrentSnapshot(currentSnapshot);
        }
        return result;
    }
}
