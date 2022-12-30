package com.kakarote.module.service;

import com.kakarote.module.entity.PO.FlowExamineRecord;

/**
 * @description:
 * @author: zjj
 * @date: 2021-07-12 09:18
 */
public interface IFlowDealDataService {

	/**
	 * 处理当前节点之后的所有数据节点数据节点
	 *
	 * @param record 审核记录
	 * @param flowId 当前节点
	 */
	void dealDataFlow(FlowExamineRecord record, Long flowId);
}
