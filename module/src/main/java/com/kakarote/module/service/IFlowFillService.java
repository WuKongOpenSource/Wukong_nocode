package com.kakarote.module.service;

import com.kakarote.module.entity.BO.FlowFillFieldDataSaveBO;
import com.kakarote.module.entity.BO.MsgBodyBO;

/**
 * @description:
 * @author: zjj
 * @date: 2021-05-25 15:04
 */
public interface IFlowFillService {

	/**
	 * 节点限时处理
	 *
	 * @param bodyBO mq消息对象
	 */
	void dealTimeFlow(MsgBodyBO bodyBO);


	/**
	 * 填写节点保存字段数据值
	 * 
	 * @param dataSaveBO
	 */
	void saveFieldValue(FlowFillFieldDataSaveBO dataSaveBO);
}