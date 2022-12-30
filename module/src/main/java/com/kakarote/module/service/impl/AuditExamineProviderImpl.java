package com.kakarote.module.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.kakarote.common.entity.UserInfo;
import com.kakarote.common.exception.BusinessException;
import com.kakarote.common.utils.UserUtil;
import com.kakarote.module.common.FlowCacheUtil;
import com.kakarote.module.common.ModuleCacheUtil;
import com.kakarote.module.common.mq.ProducerUtil;
import com.kakarote.module.constant.ActionTypeEnum;
import com.kakarote.module.constant.FlowStatusEnum;
import com.kakarote.module.constant.MessageTagEnum;
import com.kakarote.module.constant.ModuleCodeEnum;
import com.kakarote.module.entity.BO.ExamineBO;
import com.kakarote.module.entity.BO.MsgBodyBO;
import com.kakarote.module.entity.PO.*;
import com.kakarote.module.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : zjj
 * @since : 2022/12/28
 */
@Service
public class AuditExamineProviderImpl implements IFlowCommonService, IAuditExamineProvider {


    @Autowired
    private IModuleFieldDataCommonService dataCommonService;

    @Autowired
    private IModuleDataOperationRecordService operationRecordService;


    @Autowired
    private IFlowDataDealRecordService dealRecordService;

    @Autowired
    private IFlowExamineRecordService examineRecordService;

    @Autowired
    private IModuleFieldDataProvider fieldDataProvider;

    @Autowired
    private IFlowProvider flowProvider;

    @Autowired
    private ProducerUtil producerUtil;

    /**
     * 修改审批状态
     *
     * @param module         模块
     * @param examineFlow    审批节点
     * @param record         审核记录
     * @param mainFieldValue 主字段值
     * @param dealRecord     审核日志
     * @param status         审批状态
     */
    private void updateExamineStatus(ModuleEntity module, Flow examineFlow,
                                     FlowExamineRecord record, String mainFieldValue,
                                     FlowDataDealRecord dealRecord, Integer status) {
        // 废弃其他进行中的审核
        dealRecordService.lambdaUpdate()
                .set(FlowDataDealRecord::getFlowStatus, FlowStatusEnum.INVALID.getStatus())
                .eq(FlowDataDealRecord::getBatchId, dealRecord.getBatchId())
                .ne(FlowDataDealRecord::getId, dealRecord.getId())
                .in(FlowDataDealRecord::getFlowStatus, FlowStatusEnum.WAIT.getStatus(),
                        FlowStatusEnum.DEALING.getStatus())
                .update();
        // 修改审核记录状态
        record.setExamineStatus(status);
        examineRecordService.updateById(record);
        // 修改审核日志状态
        dealRecord.setFlowStatus(status);
        dealRecord.setUpdateTime(DateUtil.date());
        dealRecordService.updateById(dealRecord);
        dealRecordService.sendMessageForRecord(module, examineFlow,
                record.getDataId(), mainFieldValue,
                record.getCreateUserId(), status,
                record.getCreateUserId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditExamine(ExamineBO examineBO) {
        UserInfo userInfo = UserUtil.getUser();
        Integer status = examineBO.getStatus();
        FlowDataDealRecord dealRecord;
        // 撤回
        if (ObjectUtil.equal(FlowStatusEnum.RECHECK.getStatus(), status)) {
            // 当前用户为创建人
            dealRecord = dealRecordService.lambdaQuery()
                    .eq(FlowDataDealRecord::getIsMain, false)
                    .eq(FlowDataDealRecord::getRecordId, examineBO.getRecordId())
                    .eq(FlowDataDealRecord::getFlowStatus, FlowStatusEnum.DEALING.getStatus())
                    .eq(FlowDataDealRecord::getCreateUserId, userInfo.getUserId())
                    .orderByAsc(FlowDataDealRecord::getSort).one();
        } else {
            // 当前用户为审批人
            dealRecord = dealRecordService.lambdaQuery()
                    .eq(FlowDataDealRecord::getIsMain, false)
                    .eq(FlowDataDealRecord::getRecordId, examineBO.getRecordId())
                    .eq(FlowDataDealRecord::getFlowStatus, FlowStatusEnum.DEALING.getStatus())
                    .eq(FlowDataDealRecord::getUserId, userInfo.getUserId())
                    .orderByAsc(FlowDataDealRecord::getSort).one();
        }
        if (ObjectUtil.isNull(dealRecord)) {
            return;
        }
        if (FlowStatusEnum.stopFlow(dealRecord.getFlowStatus())) {
            throw new BusinessException(ModuleCodeEnum.HAS_EXAMINED_ERROR);
        }
        dealRecord.setRemark(examineBO.getRemarks());
        // 获取当前数据的审批记录
        FlowExamineRecord examineRecord = examineRecordService.getById(dealRecord.getRecordId());
        Flow currentFlow = FlowCacheUtil.getByIdAndVersion(examineBO.getFlowId(), dealRecord.getVersion());
        if (ObjectUtil.equal(FlowStatusEnum.RECHECK.getStatus(), status)) {
            if (ObjectUtil.equal(FlowStatusEnum.PASS.getStatus(), examineRecord.getExamineStatus())) {
                throw new BusinessException(ModuleCodeEnum.EXAMINE_RECHECK_PASS_ERROR);
            }
        }
        // 获取节点的主记录
        FlowDataDealRecord mainDataDealRecord = dealRecordService.getMainByRecordIdAndFlowId(examineRecord.getRecordId(), examineBO.getFlowId());
        ModuleEntity module = ModuleCacheUtil.getByIdAndVersion(mainDataDealRecord.getModuleId(), mainDataDealRecord.getVersion());
        // 获取模块主字段值
        String mainFieldValue = fieldDataProvider.queryValue(mainDataDealRecord.getDataId(), module.getMainFieldId());
        // 操作类型
        ActionTypeEnum actionTypeEnum = ActionTypeEnum.NULL;
        if (ObjectUtil.equal(FlowStatusEnum.PASS.getStatus(), status)) {
            actionTypeEnum = ActionTypeEnum.PASS;
        } else if (ObjectUtil.equal(FlowStatusEnum.REJECT.getStatus(), status)) {
            actionTypeEnum = ActionTypeEnum.REJECT;

        } else if (ObjectUtil.equal(FlowStatusEnum.RECHECK.getStatus(), status)) {
            actionTypeEnum = ActionTypeEnum.RECHECK;
        }
        // 保存操作记录
        ModuleDataOperationRecord operationRecord = operationRecordService.initEntity(module.getModuleId(), module.getVersion(), mainDataDealRecord.getDataId(), mainFieldValue, null, null, actionTypeEnum);
        operationRecord.setExamineRecordId(mainDataDealRecord.getRecordId());
        operationRecord.setExtData(JSON.toJSONString(mainDataDealRecord));
        operationRecordService.save(operationRecord);
        // 审批通过
        if (ObjectUtil.equal(FlowStatusEnum.PASS.getStatus(), status)) {
            // 转交的审批
            if (ObjectUtil.isNotNull(dealRecord.getParentId())) {
                // 1 依次审批 2 会签 3 或签
                if (ObjectUtil.equal(1, dealRecord.getType())) {
                    dealRecord.setFlowStatus(status);
                    dealRecord.setUpdateTime(DateUtil.date());
                    dealRecordService.updateById(dealRecord);
                    FlowDataDealRecord nextRecord = dealRecordService.getNext(dealRecord);
                    if (ObjectUtil.isNull(nextRecord)) {
                        mainDataDealRecord.setFlowStatus(FlowStatusEnum.PASS.getStatus());
                        dealRecordService.updateById(mainDataDealRecord);
                    } else {
                        // 转交的审批
                        if (ObjectUtil.isNotNull(nextRecord.getParentId())) {
                            nextRecord.setFlowStatus(FlowStatusEnum.DEALING.getStatus());
                            dealRecordService.updateById(nextRecord);
                            // 通知下个待审批的用户
                            dealRecordService.sendMessageForRecord(module, currentFlow, mainDataDealRecord.getDataId(), mainFieldValue,
                                    nextRecord.getUserId(), FlowStatusEnum.DEALING.getStatus(), examineRecord.getCreateUserId());
                            return;
                        } else {
                            // 1 依次审批 2 会签 3 或签
                            if (ObjectUtil.equal(1, nextRecord.getType())) {
                                nextRecord.setFlowStatus(FlowStatusEnum.DEALING.getStatus());
                                dealRecordService.updateById(nextRecord);
                                // 通知下个待审批的用户
                                dealRecordService.sendMessageForRecord(module, currentFlow, mainDataDealRecord.getDataId(), mainFieldValue,
                                        nextRecord.getUserId(), FlowStatusEnum.DEALING.getStatus(), examineRecord.getCreateUserId());
                                return;
                            } else if (ObjectUtil.equal(2, nextRecord.getType())) {
                                return;
                            } else {
                                mainDataDealRecord.setFlowStatus(FlowStatusEnum.PASS.getStatus());
                                dealRecordService.updateById(mainDataDealRecord);
                            }
                        }
                    }
                } else if (ObjectUtil.equal(2, dealRecord.getType())) {
                    dealRecord.setFlowStatus(status);
                    dealRecord.setUpdateTime(DateUtil.date());
                    dealRecordService.updateById(dealRecord);
                    FlowDataDealRecord nextRecord = dealRecordService.getNext(dealRecord);
                    if (ObjectUtil.isNull(nextRecord)) {
                        mainDataDealRecord.setFlowStatus(FlowStatusEnum.PASS.getStatus());
                        dealRecordService.updateById(mainDataDealRecord);
                    } else {
                        // 转交的审批
                        if (ObjectUtil.isNotNull(nextRecord.getParentId())) {
                            return;
                        } else {
                            // 1 依次审批 2 会签 3 或签
                            if (ObjectUtil.equal(1, nextRecord.getType())) {
                                nextRecord.setFlowStatus(FlowStatusEnum.DEALING.getStatus());
                                dealRecordService.updateById(nextRecord);
                                // 通知下个待审批的用户
                                dealRecordService.sendMessageForRecord(module, currentFlow, mainDataDealRecord.getDataId(), mainFieldValue,
                                        nextRecord.getUserId(), FlowStatusEnum.DEALING.getStatus(), examineRecord.getCreateUserId());
                                return;
                            } else if (ObjectUtil.equal(2, nextRecord.getType())) {
                                return;
                            } else {
                                mainDataDealRecord.setFlowStatus(FlowStatusEnum.PASS.getStatus());
                                dealRecordService.updateById(mainDataDealRecord);
                            }
                        }
                    }
                } else {
                    // 当前用户审批通过
                    dealRecord.setFlowStatus(status);
                    dealRecord.setUpdateTime(DateUtil.date());
                    dealRecordService.updateById(dealRecord);
                    // 其它或签废弃
                    dealRecordService.lambdaUpdate()
                            .set(FlowDataDealRecord::getFlowStatus, FlowStatusEnum.INVALID.getStatus())
                            .set(FlowDataDealRecord::getUpdateTime, DateUtil.date())
                            .eq(FlowDataDealRecord::getParentId, dealRecord.getParentId())
                            .eq(FlowDataDealRecord::getBatchId, dealRecord.getBatchId())
                            .ne(FlowDataDealRecord::getId, dealRecord.getId())
                            .update();
                    FlowDataDealRecord nextRecord = dealRecordService.getNext(dealRecord);
                    if (ObjectUtil.isNull(nextRecord)) {
                        mainDataDealRecord.setFlowStatus(FlowStatusEnum.PASS.getStatus());
                        dealRecordService.updateById(mainDataDealRecord);
                    } else {
                        // 1 依次审批 2 会签 3 或签
                        if (ObjectUtil.equal(1, nextRecord.getType())) {
                            nextRecord.setFlowStatus(FlowStatusEnum.DEALING.getStatus());
                            dealRecordService.updateById(nextRecord);
                            // 通知下个待审批的用户
                            dealRecordService.sendMessageForRecord(module, currentFlow, mainDataDealRecord.getDataId(), mainFieldValue,
                                    nextRecord.getUserId(), FlowStatusEnum.DEALING.getStatus(), examineRecord.getCreateUserId());
                            return;
                        } else if (ObjectUtil.equal(2, nextRecord.getType())) {
                            return;
                        } else {
                            dealRecordService.lambdaUpdate()
                                    .set(FlowDataDealRecord::getFlowStatus, FlowStatusEnum.PASS.getStatus())
                                    .set(FlowDataDealRecord::getUpdateTime, DateUtil.date())
                                    .eq(FlowDataDealRecord::getBatchId, dealRecord.getBatchId())
                                    .update();
                            mainDataDealRecord.setFlowStatus(FlowStatusEnum.PASS.getStatus());
                            dealRecordService.updateById(mainDataDealRecord);
                        }
                    }
                }
            } else {
                // 1 依次审批 2 会签 3 或签
                if (ObjectUtil.equal(1, dealRecord.getType())) {
                    dealRecord.setFlowStatus(status);
                    dealRecord.setUpdateTime(DateUtil.date());
                    dealRecordService.updateById(dealRecord);
                    FlowDataDealRecord nextRecord = dealRecordService.getNext(dealRecord);
                    if (ObjectUtil.isNull(nextRecord)) {
                        mainDataDealRecord.setFlowStatus(FlowStatusEnum.PASS.getStatus());
                        dealRecordService.updateById(mainDataDealRecord);
                    } else {
                        nextRecord.setFlowStatus(FlowStatusEnum.DEALING.getStatus());
                        dealRecordService.updateById(nextRecord);
                        // 通知下个待审批的用户
                        dealRecordService.sendMessageForRecord(module, currentFlow, mainDataDealRecord.getDataId(), mainFieldValue,
                                nextRecord.getUserId(), FlowStatusEnum.DEALING.getStatus(), examineRecord.getCreateUserId());
                        return;
                    }
                } else if (ObjectUtil.equal(2, dealRecord.getType())) {
                    dealRecord.setFlowStatus(status);
                    dealRecord.setUpdateTime(DateUtil.date());
                    dealRecordService.updateById(dealRecord);
                    FlowDataDealRecord nextRecord = dealRecordService.getNext(dealRecord);
                    if (ObjectUtil.isNull(nextRecord)) {
                        mainDataDealRecord.setFlowStatus(FlowStatusEnum.PASS.getStatus());
                        dealRecordService.updateById(mainDataDealRecord);
                    } else {
                        // 转交的审批
                        if (ObjectUtil.isNotNull(nextRecord.getParentId())) {
                            // 1 依次审批 2 会签 3 或签
                            if (ObjectUtil.equal(1, nextRecord.getType())) {
                                nextRecord.setFlowStatus(FlowStatusEnum.DEALING.getStatus());
                                dealRecordService.updateById(nextRecord);
                                // 通知下个待审批的用户
                                dealRecordService.sendMessageForRecord(module, currentFlow, mainDataDealRecord.getDataId(), mainFieldValue,
                                        nextRecord.getUserId(), FlowStatusEnum.DEALING.getStatus(), examineRecord.getCreateUserId());
                                return;
                            }
                        } else {
                            return;
                        }
                    }
                } else {
                    // 当前用户审批通过
                    dealRecord.setFlowStatus(status);
                    dealRecord.setUpdateTime(DateUtil.date());
                    dealRecordService.updateById(dealRecord);
                    // 其它或签废弃
                    dealRecordService.lambdaUpdate()
                            .set(FlowDataDealRecord::getFlowStatus, FlowStatusEnum.INVALID.getStatus())
                            .set(FlowDataDealRecord::getUpdateTime, DateUtil.date())
                            .eq(FlowDataDealRecord::getBatchId, dealRecord.getBatchId())
                            .ne(FlowDataDealRecord::getId, dealRecord.getId())
                            .update();
                    mainDataDealRecord.setFlowStatus(FlowStatusEnum.PASS.getStatus());
                    dealRecordService.updateById(mainDataDealRecord);
                }
            }
        } else {
            this.updateExamineStatus(module, currentFlow, examineRecord, mainFieldValue, dealRecord, status);
            mainDataDealRecord.setFlowStatus(status);
            dealRecordService.updateById(mainDataDealRecord);
        }
        // 更新数据的流程状态
        ModuleFieldDataCommon dataCommon = dataCommonService.getByDataId(examineBO.getDataId());
        dataCommon.setFlowStatus(status);
        dataCommon.setType(0);
        dataCommon.setCurrentFlowId(examineBO.getFlowId());
        dataCommon.setUpdateTime(DateUtil.date());
        dataCommonService.updateById(dataCommon);
        savePage(mainDataDealRecord.getDataId(), mainDataDealRecord.getModuleId(), mainDataDealRecord.getVersion());
        if (ObjectUtil.equal(FlowStatusEnum.PASS.getStatus(), mainDataDealRecord.getFlowStatus())) {
            // 获取当前节点的下一个节点，继续处理数据
            Flow nextFlow = flowProvider.getNextOrUpperNextFlow(currentFlow);
            if (ObjectUtil.isNull(nextFlow)) {
                examineRecord.setUpdateTime(DateUtil.date());
                examineRecord.setUpdateUserId(UserUtil.getUserId());
                examineRecord.setExamineStatus(FlowStatusEnum.PASS.getStatus());
                examineRecordService.updateById(examineRecord);
                return;
            }
            // 发送MQ消息
            MsgBodyBO msgBody = new MsgBodyBO();
            msgBody.setMsgKey(IdUtil.simpleUUID());
            msgBody.setModuleId(examineRecord.getModuleId());
            msgBody.setVersion(examineRecord.getVersion());
            msgBody.setRecordId(examineRecord.getRecordId());
            msgBody.setUserId(examineRecord.getCreateUserId());
            msgBody.setFlowId(nextFlow.getFlowId());
            msgBody.setDataId(examineBO.getDataId());
            msgBody.setUserId(examineRecord.getCreateUserId());
            msgBody.setMsgTag(MessageTagEnum.DEAL_FLOW);
            producerUtil.sendMsgToTopicOne(msgBody);
        }
    }
}
