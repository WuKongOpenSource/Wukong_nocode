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
import com.kakarote.module.entity.BO.ModuleFieldValueBO;
import com.kakarote.module.entity.PO.FlowConditionData;
import com.kakarote.module.constant.FlowRuleTypeEnum;
import com.kakarote.module.constant.FlowStatusEnum;
import com.kakarote.module.constant.FlowTypeEnum;
import com.kakarote.module.entity.BO.CommonConditionBO;
import com.kakarote.module.entity.BO.FlowConditionBO;

import com.kakarote.module.entity.PO.*;
import com.kakarote.module.entity.VO.FlowVO;
import com.kakarote.module.mapper.FlowConditionMapper;
import com.kakarote.module.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <p>
 * 流程条件表 服务实现类
 * </p>
 *
 * @author zhangzhiwei
 * @since 2021-04-25
 */
@Service("conditionService")
public class FlowConditionServiceImpl extends BaseServiceImpl<FlowConditionMapper, FlowCondition> implements IFlowConditionService, IFlowTypeService {

	@Autowired
	private IFlowConditionDataService conditionDataService;

	@Autowired
	private IFlowService flowService;

	@Autowired
	private IFlowProvider flowProvider;

	@Autowired
	private IFlowDataDealRecordService dealRecordService;

	@Override
	public void queryExamineData(Map<String, Object> map, String batchId) {
		List<FlowCondition> flowConditions = lambdaQuery().eq(FlowCondition::getBatchId, batchId).list();
		if (CollUtil.isEmpty(flowConditions)) {
			return;
		}
		Map<String, List<FlowCondition>> flowConditionMap = flowConditions.stream()
				.collect(Collectors.groupingBy(d -> d.getFlowId().toString()));
		map.put(FlowTypeEnum.CONDITION.name(), flowConditionMap);
		List<FlowConditionData> conditionDataList = conditionDataService.lambdaQuery()
				.eq(FlowConditionData::getRuleType, FlowRuleTypeEnum.CONDITION_CONDITION.getType())
				.eq(FlowConditionData::getBatchId, batchId).list();
		map.put(FlowRuleTypeEnum.CONDITION_CONDITION.name(), conditionDataList);
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
		Map<String, List<FlowCondition>> flowConditionMap = MapUtil.get(map, FlowTypeEnum.CONDITION.name(), Map.class);
		List<FlowCondition> flowConditions = flowConditionMap.get(flow.getFlowId().toString());
		List<FlowVO.FlowCondition> conditionList = new ArrayList<>();
		Map<Long, List<Flow>> flowMap = MapUtil.get(map, "FLOW", Map.class);
		for (FlowCondition flowCondition : flowConditions) {
			FlowVO.FlowCondition condition = new FlowVO.FlowCondition();
			condition.setConditionId(flowCondition.getConditionId());
			condition.setConditionName(flowCondition.getConditionName());
			condition.setSort(flowCondition.getPriority());
			List<FlowConditionData> conditions = MapUtil.get(map, FlowRuleTypeEnum.CONDITION_CONDITION.name(), List.class);
			condition.setConditionDataList(filterAndTransBO(conditions, flowCondition.getConditionId()));
			if (flowMap.containsKey(flowCondition.getConditionId())) {
				List<FlowVO> flowDataList = new ArrayList<>();
				List<Flow> conditionFlows = flowMap.get(flowCondition.getConditionId());
				conditionFlows.sort((f1, f2) -> f1.getPriority() > f2.getPriority() ? 1 : -1);
				for (Flow f : conditionFlows) {
					FlowTypeEnum flowTypeEnum = FlowTypeEnum.parse(f.getFlowType());
					IFlowTypeService flowTypeService = ApplicationContextHolder.getBean(flowTypeEnum.getServiceName());
					flowDataList.add(flowTypeService.createFlowInfo(map, f, userInfos, ownerUserId));
				}
				condition.setFlowDataList(flowDataList);
			}
			conditionList.add(condition);
		}
		flowVO.setData(new JSONObject().fluentPut("data", conditionList));
		return flowVO;
	}

	private List<FlowConditionBO> filterAndTransBO(List<FlowConditionData> updateConditions, Long conditionId) {
		return updateConditions.stream().filter(d -> ObjectUtil.equal(conditionId, d.getTypeId()))
				.map(d -> {
					FlowConditionBO conditionBO = new FlowConditionBO();
                    BeanUtils.copyProperties(d, conditionBO);
					conditionBO.setSearch(JSON.parseObject(d.getSearch(), CommonConditionBO.class));
					return conditionBO;
				}).collect(Collectors.toList());
	}

	/**
	 * 获取条件节点的所有条件分支
	 *
	 * @param flowId
	 * @param version
	 * @return
	 */
	@Override
	public List<FlowCondition> getByFlowId(Long flowId, Integer version) {
		return lambdaQuery()
				.eq(FlowCondition::getFlowId, flowId)
				.eq(FlowCondition::getVersion, version)
				.list();
	}

	/**
	 * 获取条件节点的下一个节点
	 *
	 * @param flowId
	 * @param fieldValueBOS
	 * @return
	 */
	@Override
	public Flow getNextFlow(Long flowId, Integer version, List<ModuleFieldValueBO> fieldValueBOS) {
		Flow flow = flowService.getByFlowId(flowId, version);
		List<FlowCondition> conditions = getByFlowId(flowId, version);
		List<FlowConditionData> conditionDataList = conditionDataService.lambdaQuery()
				.eq(FlowConditionData::getRuleType, FlowRuleTypeEnum.CONDITION_CONDITION.getType())
				.eq(FlowConditionData::getBatchId, flow.getBatchId()).list();
		Map<Long, List<FlowConditionData>> flowConditionDataMap = conditionDataList.stream()
				.collect(Collectors.groupingBy(d -> d.getTypeId()));
		Long conditionId = null;
		for (FlowCondition condition : conditions) {
			List<FlowConditionData> flowConditionDataList = flowConditionDataMap.get(condition.getConditionId());
			boolean isPass;
			if (CollUtil.isEmpty(flowConditionDataList)) {
				isPass = true;
			} else {
				isPass = conditionPass(filterAndTransBO(flowConditionDataList, condition.getConditionId()),
						fieldValueBOS, flow.getModuleId(), version);
			}
			if (isPass) {
				conditionId = condition.getConditionId();
				break;
			}
		}
		// 条件均不满足
		if (ObjectUtil.isNull(conditionId)) {
			return null;
		}
		Flow nextFlow = flowService.getNextConditionFlow(conditionId, flow.getFlowMetadataId());
		if (ObjectUtil.isNull(nextFlow)) {
			nextFlow = flowProvider.getNextOrUpperNextFlow(flow);
		}
		return nextFlow;
	}

	@Override
	public void dealData(FlowExamineRecord record, Long flowId) {
		Flow currentFlow = flowService.getByFlowId(flowId, record.getVersion());
		// 条件节点的条件数据
		List<ModuleFieldValueBO> fieldValueBOS = flowProvider.getConditionDataMap(record);
		Flow nextFlow = this.getNextFlow(flowId, record.getVersion(), fieldValueBOS);
		// 保存节点处理记录
		FlowDataDealRecord dataDealRecord = new FlowDataDealRecord();
		dataDealRecord.setIsMain(true);
		dataDealRecord.setRecordId(record.getRecordId());
		dataDealRecord.setFlowId(flowId);
		dataDealRecord.setConditionId(Optional.ofNullable(nextFlow).map(f -> f.getConditionId()).orElse(null));
		dataDealRecord.setDataId(record.getDataId());
		dataDealRecord.setUserId(record.getCreateUserId());
		dataDealRecord.setCreateUserId(record.getCreateUserId());
		dataDealRecord.setCreateTime(DateUtil.date());
		dataDealRecord.setUpdateTime(DateUtil.date());
		dataDealRecord.setModuleId(record.getModuleId());
		dataDealRecord.setVersion(record.getVersion());
		dataDealRecord.setFlowType(FlowTypeEnum.CONDITION.getType());
		dataDealRecord.setFlowStatus(FlowStatusEnum.DEFAULT.getStatus());
		dataDealRecord.setBatchId(IdUtil.simpleUUID());
		dealRecordService.save(dataDealRecord);
		if (ObjectUtil.isNull(nextFlow)) {
			updateExamineStatus(record, currentFlow, FlowStatusEnum.PASS);
			return;
		}
		FlowTypeEnum flowTypeEnum = FlowTypeEnum.parse(nextFlow.getFlowType());
		IFlowTypeService flowTypeService = ApplicationContextHolder.getBean(flowTypeEnum.getServiceName());
		flowTypeService.dealData(record, nextFlow.getFlowId());
	}

	@Override
	public List<FlowCondition> getByModuleIdAndVersion(Long moduleId, Integer version, Long flowMetaDataId) {
		return lambdaQuery().eq(FlowCondition::getModuleId, moduleId)
                .eq(FlowCondition::getVersion, version)
                .eq(FlowCondition::getFlowMetadataId, flowMetaDataId)
                .list();
	}
}
