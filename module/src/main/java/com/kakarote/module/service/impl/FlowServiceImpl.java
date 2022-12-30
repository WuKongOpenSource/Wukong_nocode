package com.kakarote.module.service.impl;

import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.module.constant.FlowTypeEnum;
import com.kakarote.module.entity.PO.Flow;
import com.kakarote.module.entity.PO.FlowCondition;
import com.kakarote.module.mapper.FlowConditionMapper;
import com.kakarote.module.mapper.FlowMapper;
import com.kakarote.module.service.IFlowCommonService;
import com.kakarote.module.service.IFlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 模块流程表 服务实现类
 * </p>
 *
 * @author zhangzhiwei
 * @since 2021-04-25
 */
@Service
public class FlowServiceImpl extends BaseServiceImpl<FlowMapper, Flow> implements IFlowService, IFlowCommonService {

	@Autowired
	private FlowConditionMapper conditionMapper;

	@Override
	public List<Flow> getByModuleIdAndVersion(Long moduleId, Integer version) {
		return lambdaQuery()
				.eq(Flow::getModuleId, moduleId)
				.eq(Flow::getVersion, version).list();
	}

    @Override
    public List<Flow> getByModuleIdAndVersion(Long moduleId, Integer version, Long metaDataId) {
        return lambdaQuery()
                .eq(Flow::getModuleId, moduleId)
                .eq(Flow::getVersion, version)
                .eq(Flow::getFlowMetadataId, metaDataId)
                .list();
    }

    /**
     * 获取当当前层级节点的下一个节点
     *
     * @param flow 当前节点
     * @return
     */
	@Override
	public Flow getNextFlow(Flow flow) {
		// 当前层级节点的下一个节点
		return lambdaQuery().eq(Flow::getBatchId, flow.getBatchId())
				.eq(Flow::getConditionId, flow.getConditionId())
				.gt(Flow::getPriority, flow.getPriority())
				.orderByAsc(Flow::getPriority).one();
	}


	@Override
	public Flow getNextConditionFlow(Long conditionId, Long flowMetadataId) {
		return lambdaQuery()
				.eq(Flow::getConditionId, conditionId)
				.eq(Flow::getFlowMetadataId, flowMetadataId)
				.ge(Flow::getPriority, 0)
				.orderByAsc(Flow::getPriority).one();
	}


	@Override
	public Flow findConditionFlow(Flow flow) {
		FlowCondition condition = conditionMapper.getByConditionIdAndVersion(flow.getConditionId(), flow.getVersion());
		// 当前条件节点
		return getByFlowId(condition.getFlowId(), flow.getVersion());
	}

	@Override
	public Flow getByFlowId(Long flowId, Integer version) {
		return lambdaQuery().eq(Flow::getFlowId, flowId).eq(Flow::getVersion, version).one();
	}

    @Override
    public Flow getByFlowId(Long flowId) {
        return lambdaQuery().eq(Flow::getFlowId, flowId).one();
    }

	@Override
	public Flow getStartFlow(Long moduleId, Integer version, Long metaDataId) {
		return lambdaQuery()
				.eq(Flow::getFlowMetadataId, metaDataId)
				.eq(Flow::getModuleId, moduleId)
				.eq(Flow::getVersion, version)
				.eq(Flow::getFlowType, FlowTypeEnum.START.getType())
				.one();
	}
}
