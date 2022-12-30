package com.kakarote.module.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.module.entity.BO.ExamineGeneralBO;
import com.kakarote.module.entity.PO.FlowExamineRecord;
import com.kakarote.module.entity.PO.FlowExamineRecordOptional;
import com.kakarote.module.mapper.FlowExamineRecordOptionalMapper;
import com.kakarote.module.service.IFlowExamineRecordOptionalService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * <p>
 * 审核自选成员选择成员表 服务实现类
 * </p>
 *
 * @author zhangzhiwei
 * @since 2021-04-25
 */
@Service
public class FlowExamineRecordOptionalServiceImpl extends BaseServiceImpl<FlowExamineRecordOptionalMapper, FlowExamineRecordOptional> implements IFlowExamineRecordOptionalService {


	@Override
	public void saveOptionalInfo(ExamineGeneralBO generalBO, FlowExamineRecord record) {
		AtomicInteger atomicInteger = new AtomicInteger(0);
		if (CollUtil.isEmpty(generalBO.getUserList())) {
			return;
		}
		List<FlowExamineRecordOptional> optionalList = generalBO.getUserList().stream().map(userId -> {
			FlowExamineRecordOptional recordOptional = new FlowExamineRecordOptional();
			recordOptional.setModuleId(record.getModuleId());
			recordOptional.setVersion(record.getVersion());
			recordOptional.setRecordId(record.getRecordId());
			recordOptional.setFlowId(generalBO.getFlowId());
			recordOptional.setSort(atomicInteger.getAndIncrement());
			recordOptional.setUserId(userId);
			recordOptional.setFlowMetadataId(record.getFlowMetadataId());
			recordOptional.setBatchId(record.getBatchId());
			return recordOptional;
		}).collect(Collectors.toList());
		saveBatch(optionalList);
	}

	@Override
	public List<FlowExamineRecordOptional> getOptional(Long recordId, Long flowId) {
		return lambdaQuery().eq(FlowExamineRecordOptional::getRecordId, recordId).eq(FlowExamineRecordOptional::getFlowId, flowId).list();
	}
}
