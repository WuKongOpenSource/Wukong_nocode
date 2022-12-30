package com.kakarote.module.service.impl;

import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.module.entity.PO.FlowTimeLimit;
import com.kakarote.module.mapper.FlowTimeLimitMapper;
import com.kakarote.module.service.IFlowTimeLimitService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 流程限时处理设置 服务实现类
 * </p>
 *
 * @author zhangzhiwei
 * @since 2021-04-28
 */
@Service
public class FlowTimeLimitServiceImpl extends BaseServiceImpl<FlowTimeLimitMapper, FlowTimeLimit> implements IFlowTimeLimitService {

	@Override
	public FlowTimeLimit getByModuleIdAndFlowId(Long moduleId, Long flowId) {
		return lambdaQuery()
				.eq(FlowTimeLimit::getModuleId, moduleId)
				.eq(FlowTimeLimit::getFlowId, flowId).one();
	}

    @Override
    public List<FlowTimeLimit> getByModuleIdAndVersion(Long moduleId, Integer version, Long flowMetaDataId) {
        return lambdaQuery()
				.eq(FlowTimeLimit::getModuleId, moduleId)
				.eq(FlowTimeLimit::getVersion, version)
				.eq(FlowTimeLimit::getFlowMetadataId, flowMetaDataId)
                .list();
    }
}
