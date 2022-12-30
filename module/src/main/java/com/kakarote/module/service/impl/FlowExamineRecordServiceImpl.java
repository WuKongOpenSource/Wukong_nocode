package com.kakarote.module.service.impl;

import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.module.constant.FlowMetadataTypeEnum;
import com.kakarote.module.entity.PO.FlowExamineRecord;
import com.kakarote.module.mapper.FlowExamineRecordMapper;
import com.kakarote.module.service.IFlowExamineRecordService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 审核记录表 服务实现类
 * </p>
 *
 * @author zhangzhiwei
 * @since 2021-04-25
 */
@Service
public class FlowExamineRecordServiceImpl extends BaseServiceImpl<FlowExamineRecordMapper, FlowExamineRecord> implements IFlowExamineRecordService {

	@Override
	public FlowExamineRecord getRecordByModuleIdAndDataId(Long moduleId, Long dataId, Long typeId) {
		return lambdaQuery()
				.eq(FlowExamineRecord::getModuleId, moduleId)
				.eq(FlowExamineRecord::getDataId, dataId)
				.eq(FlowExamineRecord::getTypeId, typeId)
				.orderByDesc(FlowExamineRecord::getCreateTime)
				.one();
	}

	@Override
	public List<FlowExamineRecord> getCustomButtonRecordByModuleIdAndDataId(Long moduleId, Long dataId) {
		return lambdaQuery()
				.eq(FlowExamineRecord::getModuleId, moduleId)
				.eq(FlowExamineRecord::getDataId, dataId)
				.eq(FlowExamineRecord::getFlowMetadataType, FlowMetadataTypeEnum.CUSTOM_BUTTON.getType())
				.orderByDesc(FlowExamineRecord::getCreateTime)
				.list();
	}
}
