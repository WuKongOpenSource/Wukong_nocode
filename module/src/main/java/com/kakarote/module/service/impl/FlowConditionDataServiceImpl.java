package com.kakarote.module.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.common.utils.UserUtil;
import com.kakarote.module.constant.FieldSearchEnum;
import com.kakarote.module.constant.FlowRuleTypeEnum;
import com.kakarote.module.entity.BO.CommonConditionBO;
import com.kakarote.module.entity.BO.FlowConditionBO;
import com.kakarote.module.entity.PO.FlowConditionData;
import com.kakarote.module.entity.PO.FlowMetadata;
import com.kakarote.module.mapper.FlowConditionDataMapper;
import com.kakarote.module.service.IFlowCommonService;
import com.kakarote.module.service.IFlowConditionDataService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 审批条件扩展字段表 服务实现类
 * </p>
 *
 * @author zhangzhiwei
 * @since 2021-04-25
 */
@Service
public class FlowConditionDataServiceImpl extends BaseServiceImpl<FlowConditionDataMapper, FlowConditionData> implements IFlowConditionDataService, IFlowCommonService {

	@Override
	public void saveConditionData(List<FlowConditionBO> conditionBOS, Long targetModuleId,
								  FlowRuleTypeEnum ruleType, Long typeId,
								  Long moduleId, Integer version, FlowMetadata flowMetadata) {
		if (CollUtil.isEmpty(conditionBOS)) {
			return;
		}
		List<FlowConditionData> conditionDataList = new ArrayList<>();
		for (FlowConditionBO dataBO : conditionBOS) {
			FlowConditionData entity = BeanUtil.copyProperties(dataBO, FlowConditionData.class);
			entity.setRuleType(ruleType.getType());
			entity.setBatchId(flowMetadata.getBatchId());
			entity.setFlowMetadataId(flowMetadata.getMetadataId());
			entity.setTypeId(typeId);
			entity.setModuleId(moduleId);
			entity.setVersion(version);
			entity.setTargetModuleId(targetModuleId);
			CommonConditionBO conditionDataBO = dataBO.getSearch();
			conditionDataBO.setConditionType(FieldSearchEnum.parse(conditionDataBO.getType()));
			entity.setSearch(JSON.toJSONString(conditionDataBO));
			entity.setCreateTime(DateUtil.date());
			entity.setCreateUserId(UserUtil.getUserId());
			conditionDataList.add(entity);
		}
		saveBatch(conditionDataList);
	}

	@Override
    public void remove(Long moduleId, Integer version, FlowMetadata metadata) {
        lambdaUpdate().eq(FlowConditionData::getModuleId, moduleId)
                .eq(FlowConditionData::getVersion, version)
                .eq(FlowConditionData::getBatchId, metadata.getBatchId())
                .remove();
	}

	@Override
	public List<FlowConditionData> getByTargetModuleIdAndFlowId(Long targetModuleId, Long flowId) {
		return lambdaQuery().eq(FlowConditionData::getTargetModuleId, targetModuleId)
				.eq(FlowConditionData::getTypeId, flowId).list();
	}

	@Override
	public List<FlowConditionData> getByModuleIdAndVersion(Long moduleId, Integer version, Long flowMetaDataId) {
		return lambdaQuery()
				.eq(FlowConditionData::getModuleId, moduleId)
				.eq(FlowConditionData::getVersion, version)
				.eq(FlowConditionData::getFlowMetadataId, flowMetaDataId)
                .list();
	}
}
