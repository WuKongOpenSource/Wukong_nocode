package com.kakarote.module.service;

import com.kakarote.common.servlet.BaseService;
import com.kakarote.module.entity.BO.ExamineBO;
import com.kakarote.module.entity.BO.ExamineRecordSaveBO;
import com.kakarote.module.entity.PO.FlowExamineRecord;
import com.kakarote.module.entity.VO.ExamineRecordReturnVO;

import java.util.List;

/**
 * <p>
 * 审核记录表 服务类
 * </p>
 *
 * @author zhangzhiwei
 * @since 2021-04-25
 */
public interface IFlowExamineRecordService extends BaseService<FlowExamineRecord> {

	/**
	 * 获取当前数据的审批记录
	 *
	 * @param moduleId 模块ID
	 * @param dataId   数据ID
	 * @param typeId   类型ID
	 * @return
	 */
	FlowExamineRecord getRecordByModuleIdAndDataId(Long moduleId, Long dataId, Long typeId);

	/**
	 * 获取自定义按钮得审批记录列表
	 *
	 * @param moduleId
	 * @param dataId
	 * @return
	 */
	List<FlowExamineRecord> getCustomButtonRecordByModuleIdAndDataId(Long moduleId, Long dataId);

}
