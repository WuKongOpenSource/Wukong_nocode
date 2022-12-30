package com.kakarote.module.service.impl;

import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.module.entity.PO.FlowFieldAuth;
import com.kakarote.module.mapper.FlowFieldAuthMapper;
import com.kakarote.module.service.IFlowFieldAuthService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FlowFieldAuthServiceImpl extends BaseServiceImpl<FlowFieldAuthMapper, FlowFieldAuth> implements IFlowFieldAuthService {

    @Override
    public FlowFieldAuth getByModuleIdAndFlowId(Long moduleId, Integer version, Long flowId) {
        return lambdaQuery()
                .eq(FlowFieldAuth::getModuleId, moduleId)
                .eq(FlowFieldAuth::getVersion, version)
                .eq(FlowFieldAuth::getFlowId, flowId)
                .one();
    }

    @Override
    public List<FlowFieldAuth> getByModuleIdAndVersion(Long moduleId, Integer version, Long flowMetaDataId) {
        return lambdaQuery().eq(FlowFieldAuth::getModuleId, moduleId)
                .eq(FlowFieldAuth::getVersion, version)
                .eq(FlowFieldAuth::getFlowMetadataId, flowMetaDataId)
                .list();
    }
}
