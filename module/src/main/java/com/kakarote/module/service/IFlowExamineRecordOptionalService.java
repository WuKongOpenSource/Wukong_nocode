package com.kakarote.module.service;

import com.kakarote.common.servlet.BaseService;
import com.kakarote.module.entity.BO.ExamineGeneralBO;
import com.kakarote.module.entity.PO.FlowExamineRecord;
import com.kakarote.module.entity.PO.FlowExamineRecordOptional;

import java.util.List;

/**
 * <p>
 * 审核自选成员选择成员表 服务类
 * </p>
 *
 * @author zhangzhiwei
 * @since 2021-04-25
 */
public interface IFlowExamineRecordOptionalService extends BaseService<FlowExamineRecordOptional> {

	/**
	 * 保存当前审批记录的自选成员信息
	 *
	 * @param generalBO
	 * @param record
	 */
	void saveOptionalInfo(ExamineGeneralBO generalBO, FlowExamineRecord record);

	List<FlowExamineRecordOptional> getOptional(Long recordId, Long flowId);
}
