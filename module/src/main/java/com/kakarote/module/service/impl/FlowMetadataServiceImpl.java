package com.kakarote.module.service.impl;

import com.kakarote.common.servlet.ApplicationContextHolder;
import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.module.entity.PO.FlowMetadata;
import com.kakarote.module.mapper.FlowMetadataMapper;
import com.kakarote.module.service.IFlowMetadataService;
import com.kakarote.module.service.IModuleService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 模块自定义流程元数据表 服务实现类
 * </p>
 *
 * @author zhangzhiwei
 * @since 2021-04-25
 */
@Service
public class FlowMetadataServiceImpl extends BaseServiceImpl<FlowMetadataMapper, FlowMetadata> implements IFlowMetadataService {

    @Override
    public FlowMetadata getByModuleId(Long moduleId, Integer version, Long typeId, Integer type) {
        return lambdaQuery()
                .eq(FlowMetadata::getModuleId, moduleId)
                .eq(FlowMetadata::getVersion, version)
                .eq(FlowMetadata::getTypeId, typeId)
                .eq(FlowMetadata::getType, type).one();
    }

    @Override
    public List<FlowMetadata> getByModuleIdAndVersion(Long moduleId, Integer version) {
        return lambdaQuery()
                .eq(FlowMetadata::getModuleId, moduleId)
                .eq(FlowMetadata::getVersion, version).list();
    }

	@Override
	public FlowMetadata getByMetadataId(Long metaDataId) {
		return getById(metaDataId);
	}

	@Override
	public List<Long> getManagerUserIds(Long metaDataId) {
		FlowMetadata metadata = getById(metaDataId);
        IModuleService moduleService = ApplicationContextHolder.getBean(IModuleService.class);
		return moduleService.getManagerUserIds(metadata.getModuleId(), metadata.getVersion());
	}
}
