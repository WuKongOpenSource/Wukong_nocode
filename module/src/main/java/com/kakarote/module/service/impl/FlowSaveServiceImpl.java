package com.kakarote.module.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kakarote.common.entity.UserInfo;
import com.kakarote.common.servlet.ApplicationContextHolder;
import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.module.common.ModuleCacheUtil;
import com.kakarote.module.entity.PO.FlowConditionData;
import com.kakarote.module.entity.PO.ModuleField;
import com.kakarote.module.entity.PO.ModuleFieldDataCommon;
import com.kakarote.module.entity.PO.ModuleEntity;
import com.kakarote.module.constant.*;
import com.kakarote.module.entity.BO.*;
import com.kakarote.module.entity.PO.*;
import com.kakarote.module.entity.VO.FlowVO;
import com.kakarote.module.mapper.FlowSaveMapper;
import com.kakarote.module.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @description: 添加数据节点 服务实现类
 * @author: zjj
 * @date: 2021-05-25 15:33
 */
@Service("saveService")
public class FlowSaveServiceImpl extends BaseServiceImpl<FlowSaveMapper, FlowSave> implements IFlowSaveService, IFlowTypeService {

	@Autowired
	private IFlowConditionDataService conditionDataService;

	@Autowired
	private IFlowService flowService;

	@Autowired
	private IFlowProvider flowProvider;

	@Autowired
	private IModuleService moduleService;

	@Autowired
	private IFlowDataDealRecordService dealRecordService;

	@Autowired
	private IModuleFieldService fieldService;

	@Autowired
	private IModuleFieldDataProvider fieldDataProvider;

	@Autowired
	private IModuleFieldDataCommonService dataCommonService;

	@Override
	public FlowSave getByFlowId(Long flowId) {
		return lambdaQuery().eq(FlowSave::getFlowId, flowId).one();
	}

	@Override
	public List<FlowSave> getByModuleIdAndVersion(Long moduleId, Integer version, Long flowMetaDataId) {
		return lambdaQuery().eq(FlowSave::getModuleId, moduleId)
                .eq(FlowSave::getVersion, version)
                .eq(FlowSave::getFlowMetadataId, flowMetaDataId)
                .list();
	}

	@Override
	public void queryExamineData(Map<String, Object> map, String batchId) {
		List<FlowSave> flowSaves = lambdaQuery().eq(FlowSave::getBatchId, batchId).list();
		if (CollUtil.isEmpty(flowSaves)) {
			return;
		}
		Map<String, FlowSave> flowSaveMap = flowSaves.stream()
				.collect(Collectors.toMap(flowSave -> flowSave.getFlowId().toString(), flowSave -> flowSave));
		map.put(FlowTypeEnum.SAVE.name(), flowSaveMap);
		List<FlowConditionData> conditionDataList = conditionDataService.lambdaQuery()
				.eq(FlowConditionData::getRuleType, FlowRuleTypeEnum.SAVE_INSERT.getType())
				.eq(FlowConditionData::getBatchId, batchId).list();
		Map<String, List<Object>> flowConditionMap = conditionDataList.stream()
				.collect(Collectors.groupingBy(d -> d.getTypeId().toString(),
						Collectors.mapping(d -> {
							FlowConditionBO conditionBO = new FlowConditionBO();
                            BeanUtils.copyProperties(d, conditionBO);
							conditionBO.setSearch(JSON.parseObject(d.getSearch(), CommonConditionBO.class));
							return JSON.toJSON(conditionBO);
						}, Collectors.toList())
				));
		map.put(FlowRuleTypeEnum.SAVE_INSERT.name(), flowConditionMap);
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
		Map<String, FlowSave> flowSaveMap = MapUtil.get(map, FlowTypeEnum.SAVE.name(), Map.class);
		FlowSave flowSave = flowSaveMap.get(flow.getFlowId().toString());
		FlowVO.FlowSaveData flowSaveData = new FlowVO.FlowSaveData();
		flowSaveData.setTargetModuleId(flowSave.getTargetModuleId());
		flowSaveData.setOwnerUser(searchUser(userInfos, flowSave.getOwnerUserId()));
		Map<String, List<FlowConditionBO>> flowConditionMap = MapUtil.get(map, FlowRuleTypeEnum.SAVE_INSERT.name(), Map.class);
		List<FlowConditionBO> flowConditionBOS = flowConditionMap.get(flow.getFlowId().toString());
		flowSaveData.setInsertRules(flowConditionBOS);
		flowVO.setData(JSON.parseObject(JSON.toJSONString(flowSaveData)));
		return flowVO;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void dealData(FlowExamineRecord record, Long flowId) {
		Flow currentFlow = flowService.getByFlowId(flowId, record.getVersion());
		FlowDataDealRecord dataDealRecord = dealRecordService.getMainByRecordIdAndFlowId(record.getRecordId(), flowId);
		// 当前节点未处理
		if (ObjectUtil.isNull(dataDealRecord)) {
			FlowSave flowSave = getByFlowId(currentFlow.getFlowId());
			Long targetModuleId = flowSave.getTargetModuleId();
			ModuleEntity targetModule = moduleService.getNormal(targetModuleId);
			DoubleCheckResultBO doubleCheckResultBO = null;
			Integer flowStatus = FlowStatusEnum.PASS.getStatus();
			if (ObjectUtil.isNotNull(targetModule)) {
				// 当前模块
				ModuleEntity currentModule = ModuleCacheUtil.getByIdAndVersion(record.getModuleId(), record.getVersion());
				// 当前模块字段数据值
				Map<String, Object> currentFieldData = fieldDataProvider.queryFieldNameDataMap(record.getDataId());
				List<FlowConditionData> flowConditionDataList = conditionDataService.getByTargetModuleIdAndFlowId(targetModuleId, flowId);
				// 明细表格内字段
				List<ModuleField> fieldsInTable = fieldService.lambdaQuery()
						.eq(ModuleField::getModuleId, currentModule.getModuleId())
						.eq(ModuleField::getVersion, currentModule.getVersion())
						.isNotNull(ModuleField::getGroupId).list();
				List<ModuleField> toSearchFields = new ArrayList<>();
				Boolean tableFieldToNormalField = false;
				for (FlowConditionData conditionData : flowConditionDataList) {
					SearchEntityBO entityBO = JSON.parseObject(conditionData.getSearch(), SearchEntityBO.class);
					List<ModuleField> fields = fieldsInTable.stream()
							.filter(f -> ObjectUtil.equal(entityBO.getCurrentFieldId(), f.getFieldId()))
							.collect(Collectors.toList());
					if (CollUtil.isNotEmpty(fields)) {
						toSearchFields.addAll(fields);
					}
				}
				ModuleField detailTableField = null;
				if (CollUtil.isNotEmpty(toSearchFields)) {
					tableFieldToNormalField = true;
					detailTableField = fieldsInTable.stream()
							.filter(f -> ObjectUtil.equal(ModuleFieldEnum.DETAIL_TABLE.getType(), f.getType())
									&& ObjectUtil.equal(f.getGroupId(), CollUtil.getFirst(toSearchFields).getGroupId()))
							.findFirst().orElse(null);
				}
				if (ObjectUtil.isNotNull(detailTableField)) {
					Object tableData = currentFieldData.get(detailTableField.getFieldName());
					List<JSONObject> jsonObjects = JSON.parseArray(tableData.toString(), JSONObject.class);
					for (JSONObject jsonObject : jsonObjects) {
						currentFieldData.putAll(jsonObject);
						// 根据创建规则构建字段数据保存对象并保存目标模块数据
						ModuleFieldDataSaveBO dataSaveBO = fieldDataProvider.buildFieldSaveBO(targetModuleId,
								flowConditionDataList,
								currentModule,
								currentFieldData);
						doubleCheckResultBO = ApplicationContextHolder.getBean(IFlowUpdateService.class).saveAndReturnDoubleCheckResult(dataSaveBO);
						if (!doubleCheckResultBO.getIsPass()) {
							break;
						}
					}
				} else {
					// 根据创建规则构建字段数据保存对象并保存目标模块数据
					ModuleFieldDataSaveBO dataSaveBO = fieldDataProvider.buildFieldSaveBO(targetModuleId,
							flowConditionDataList,
							currentModule,
							currentFieldData);
					doubleCheckResultBO = ApplicationContextHolder.getBean(IFlowUpdateService.class).saveAndReturnDoubleCheckResult(dataSaveBO);
				}
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
            dataDealRecord.setFlowType(FlowTypeEnum.SAVE.getType());
            dataDealRecord.setBatchId(IdUtil.simpleUUID());
            dataDealRecord.setExtData(JSON.toJSONString(doubleCheckResultBO));
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

			if (ObjectUtil.equal(FlowStatusEnum.PASS.getStatus(), flowStatus)) {
				// 更新数据的流程状态
				if (ObjectUtil.equal(FlowMetadataTypeEnum.SYSTEM.getType(), record.getFlowMetadataType())) {
					ModuleFieldDataCommon dataCommon = dataCommonService.getByDataId(record.getDataId());
					dataCommon.setFlowStatus(flowStatus);
					dataCommon.setFlowType(FlowTypeEnum.SAVE.getType());
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

		// 处理
		Flow nextFlow = flowProvider.getNextOrUpperNextFlow(currentFlow);
		if (ObjectUtil.isNull(nextFlow)) {
			updateExamineStatus(record, currentFlow, FlowStatusEnum.PASS);
			return;
		}
		FlowTypeEnum flowTypeEnum = FlowTypeEnum.parse(nextFlow.getFlowType());
		IFlowTypeService flowTypeService = ApplicationContextHolder.getBean(flowTypeEnum.getServiceName());
		flowTypeService.dealData(record, nextFlow.getFlowId());
	}
}
