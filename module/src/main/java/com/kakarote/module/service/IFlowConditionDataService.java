package com.kakarote.module.service;

import com.kakarote.common.servlet.BaseService;
import com.kakarote.module.constant.FlowRuleTypeEnum;
import com.kakarote.module.entity.BO.FlowConditionBO;
import com.kakarote.module.entity.BO.ModuleFieldDataSaveBO;
import com.kakarote.module.entity.PO.FlowConditionData;
import com.kakarote.module.entity.PO.FlowMetadata;
import com.kakarote.module.entity.PO.ModuleEntity;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 审批条件扩展字段表 服务类
 * </p>
 *
 * @author zhangzhiwei
 * @since 2021-04-25
 */
public interface IFlowConditionDataService extends BaseService<FlowConditionData> {

	void saveConditionData(List<FlowConditionBO> conditionBOS, Long targetModuleId,
						   FlowRuleTypeEnum ruleType, Long typeId,
						   Long moduleId, Integer version, FlowMetadata flowMetadata);

	/**
	 *  删除节点条件数据
	 *
	 * @param moduleId
	 * @param version
	 * @param metadata
	 */
	void remove(Long moduleId, Integer version, FlowMetadata metadata);

	List<FlowConditionData> getByTargetModuleIdAndFlowId(Long targetModuleId, Long flowId);

	List<FlowConditionData> getByModuleIdAndVersion(Long moduleId, Integer version, Long flowMetaDataId);
}
