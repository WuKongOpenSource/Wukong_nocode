package com.kakarote.module.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.kakarote.common.utils.UserUtil;
import com.kakarote.module.common.ModuleCacheUtil;
import com.kakarote.module.common.mq.ProducerUtil;
import com.kakarote.module.constant.FlowMetadataTypeEnum;
import com.kakarote.module.constant.FlowStatusEnum;
import com.kakarote.module.constant.FlowTypeEnum;
import com.kakarote.module.constant.MessageTagEnum;
import com.kakarote.module.entity.BO.ExamineGeneralBO;
import com.kakarote.module.entity.BO.ExamineRecordSaveBO;
import com.kakarote.module.entity.BO.MsgBodyBO;
import com.kakarote.module.entity.BO.ToDoSaveBO;
import com.kakarote.module.entity.PO.*;
import com.kakarote.module.entity.VO.ExamineRecordReturnVO;
import com.kakarote.module.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author : zjj
 * @since : 2022/12/27
 */
@Service
public class FlowExamineProviderImpl implements IFlowExamineProvider {

    @Autowired
    private IFlowMetadataService metadataService;

    @Autowired
    private IFlowService flowService;

    @Autowired
    private IFlowFieldAuthService fieldAuthService;

    @Autowired
    private IFlowExamineRecordOptionalService recordOptionalService;

    @Autowired
    private ProducerUtil producerUtil;

    @Autowired
    private IModuleFieldDataService fieldDataService;


    @Autowired
    private IToDoService toDoService;

    @Autowired
    private IFlowExamineRecordService examineRecordService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExamineRecordReturnVO save(ExamineRecordSaveBO recordSaveBO) {
        FlowMetadata metadata = metadataService.getByModuleId(recordSaveBO.getModuleId(), recordSaveBO.getVersion(), 0L, FlowMetadataTypeEnum.SYSTEM.getType());
        // 无有效审批流程时直接审核通过
        if (ObjectUtil.isNull(metadata)) {
            return new ExamineRecordReturnVO(null, FlowStatusEnum.DEFAULT.getStatus());
        }
        // 审核记录
        FlowExamineRecord record = new FlowExamineRecord();
        record.setModuleId(recordSaveBO.getModuleId());
        record.setVersion(recordSaveBO.getVersion());
        record.setDataId(recordSaveBO.getDataId());
        record.setFlowMetadataId(metadata.getMetadataId());
        record.setCreateUserId(UserUtil.getUserId());
        record.setUpdateUserId(UserUtil.getUserId());
        record.setCreateTime(DateUtil.date());
        record.setUpdateTime(DateUtil.date());
        record.setExamineStatus(FlowStatusEnum.WAIT.getStatus());
        record.setBatchId(IdUtil.simpleUUID());
        record.setFlowId(0L);
        examineRecordService.save(record);
        // 保存自选成员列表
        List<ExamineGeneralBO> optionalList = recordSaveBO.getOptionalList();
        optionalList.forEach(examineGeneralBO -> {
            recordOptionalService.saveOptionalInfo(examineGeneralBO, record);
        });
        ModuleEntity module = ModuleCacheUtil.getByIdAndVersion(record.getModuleId(), record.getVersion());
        // 发起人待办信息
        ToDoSaveBO toDoSaveBO = new ToDoSaveBO();
        toDoSaveBO.setApplicationId(module.getApplicationId());
        toDoSaveBO.setModuleId(record.getModuleId());
        toDoSaveBO.setModuleName(module.getName());
        toDoSaveBO.setObjectType(FlowMetadataTypeEnum.SYSTEM.getType());
        toDoSaveBO.setRecordId(record.getRecordId());
        toDoSaveBO.setVersion(record.getVersion());
        toDoSaveBO.setDataId(record.getDataId());
        toDoSaveBO.setType(0);
        // 发起人节点
        Flow startFlow = flowService.getStartFlow(module.getModuleId(), module.getVersion(), metadata.getMetadataId());
        if (ObjectUtil.isNotNull(startFlow)) {
            toDoSaveBO.setTypeId(startFlow.getFlowId());
            toDoSaveBO.setTypeName(startFlow.getFlowName());
            toDoSaveBO.setFlowType(FlowTypeEnum.START.getType());
            FlowFieldAuth flowFieldAuth = fieldAuthService.getByModuleIdAndFlowId(module.getModuleId(), module.getVersion(), startFlow.getFlowId());
            if (ObjectUtil.isNotNull(flowFieldAuth)) {
                toDoSaveBO.setFieldAuth(flowFieldAuth.getAuth());
            }
        }
        List<ModuleFieldData> fieldDataList = fieldDataService.queryFieldData(record.getDataId());
        toDoSaveBO.setFieldValue(JSON.toJSONString(fieldDataList));
        toDoSaveBO.setCreateUserId(UserUtil.getUserId());
        toDoService.save(toDoSaveBO);
        // 发送MQ消息
        MsgBodyBO msgBody = new MsgBodyBO();
        msgBody.setMsgKey(IdUtil.simpleUUID());
        msgBody.setModuleId(recordSaveBO.getModuleId());
        msgBody.setVersion(recordSaveBO.getVersion());
        msgBody.setRecordId(record.getRecordId());
        msgBody.setFlowId(null);
        msgBody.setDataId(recordSaveBO.getDataId());
        msgBody.setUserId(UserUtil.getUserId());
        msgBody.setMsgTag(MessageTagEnum.DEAL_FLOW);
        msgBody.setDelayTime(2000L);
        producerUtil.sendMsgToTopicOne(msgBody);
        return new ExamineRecordReturnVO(record.getRecordId(), FlowStatusEnum.DEALING.getStatus());
    }
}
