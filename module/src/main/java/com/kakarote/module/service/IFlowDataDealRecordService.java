package com.kakarote.module.service;

import com.alibaba.fastjson.JSONArray;
import com.kakarote.common.servlet.BaseService;
import com.kakarote.module.entity.BO.ExamineUserBO;
import com.kakarote.module.entity.BO.FlowDealDetailBO;
import com.kakarote.module.entity.BO.FlowDealDetailQueryBO;
import com.kakarote.module.entity.BO.TransferFlowBO;
import com.kakarote.module.entity.PO.Flow;
import com.kakarote.module.entity.PO.FlowDataDealRecord;
import com.kakarote.module.entity.PO.ModuleEntity;

import java.util.List;

/**
 * @description:节点数据处理记录
 * @author: zjj
 * @date: 2021-07-12 10:35
 */
public interface IFlowDataDealRecordService extends BaseService<FlowDataDealRecord> {

	/**
	 * 根据审核记录ID和节点ID 查询节点数据处理记录
	 *
	 * @param recordId
	 * @param flowId
	 * @param isMain
	 * @return
	 */
	FlowDataDealRecord getMainByRecordIdAndFlowId(Long recordId, Long flowId);

	/**
	 * 根据审核记录ID 查询节点数据处理记录
	 *
	 * @param recordId
	 * @return
	 */
	List<FlowDataDealRecord> getMainByRecordId(Long recordId);

	/**
	 * 保存节点处理记录
	 *
	 * @param currentFlow
	 * @param examineUserBO
	 * @param recordId
	 * @param currentUserId
	 * @param dataId
	 * @param batchId
	 * @return
	 */
	List<FlowDataDealRecord> saveRecords(Flow currentFlow, ExamineUserBO examineUserBO,
										 Long recordId, Long currentUserId, Long dataId, String batchId);

	/**
	 * 获取下个审核记录
	 *
	 * @param record
	 * @return
	 */
	FlowDataDealRecord getNext(FlowDataDealRecord record);

	/**
	 * 发送通知
	 *
	 * @param module
	 * @param flow
	 * @param dataId
	 * @param mainFieldValue
	 * @param receiver
	 * @param status
	 * @param createUserId
	 */
	void sendMessageForRecord(ModuleEntity module, Flow flow, Long dataId,
                              String mainFieldValue, Long receiver,
                              Integer status, Long createUserId);

	/**
	 * 获取数据的节点处理详情
	 *
	 * @param queryBO
	 * @return
	 */
	JSONArray getFlowDealDetail(FlowDealDetailQueryBO queryBO);

	/**
	 * 节点转交
	 *
	 * @param transferFlowBO
	 */
	void transferFlow(TransferFlowBO transferFlowBO);

	/**
	 * 获取指定数据的所有节点处理记录
	 *
	 * @param moduleId
	 * @param dataId
	 * @return
	 */
	List<FlowDataDealRecord> getMainByModuleIdAndDataId(Long moduleId, Long dataId);

	/**
	 * 获取指定数据的按钮节点处理详情
	 *
	 * @param moduleId
	 * @param dataId
	 * @return
	 */
	List<FlowDealDetailBO> getCustomButtonDealRecord(Long moduleId, Long dataId);

}
