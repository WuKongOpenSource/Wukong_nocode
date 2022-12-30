package com.kakarote.module.service;

import com.kakarote.common.entity.UserInfo;
import com.kakarote.module.entity.BO.ExamineUserBO;
import com.kakarote.module.entity.BO.ExamineUserQueryBO;
import com.kakarote.module.entity.PO.Flow;
import com.kakarote.module.entity.VO.FlowVO;

import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: zjj
 * @date: 2021-05-25 11:19
 */
public interface IFlowExamineTypeService extends IFlowCommonService{

	void queryExamineData(Map<String, Object> map, String batchId);

	FlowVO createFlowInfo(Map<String, Object> map, Flow flow, List<UserInfo> userInfos, Long ownerUserId);

	/**
	 * 查询审批用户
	 *
	 * @return
	 */
	ExamineUserBO queryFlowUser(ExamineUserQueryBO queryBO);
}
