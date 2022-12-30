package com.kakarote.module.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.kakarote.common.entity.UserInfo;
import com.kakarote.common.servlet.ApplicationContextHolder;
import com.kakarote.module.constant.FlowStatusEnum;
import com.kakarote.module.constant.FlowTypeEnum;
import com.kakarote.module.entity.PO.Flow;
import com.kakarote.module.entity.PO.FlowDataDealRecord;
import com.kakarote.module.entity.PO.FlowExamineRecord;
import com.kakarote.module.entity.VO.FlowVO;
import com.kakarote.module.service.IFlowDataDealRecordService;
import com.kakarote.module.service.IFlowProvider;
import com.kakarote.module.service.IFlowService;
import com.kakarote.module.service.IFlowTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("startService")
public class FlowStartServiceImpl implements IFlowTypeService {

    @Autowired
    private IFlowService flowService;

    @Autowired
    private IFlowProvider flowProvider;

    @Autowired
    private IFlowDataDealRecordService dealRecordService;

    @Override
    public FlowVO createFlowInfo(Map<String, Object> map, Flow flow, List<UserInfo> userInfos, Long ownerUserId) {
        FlowVO flowVO = new FlowVO();
        flowVO.setFlowId(flow.getFlowId());
        flowVO.setFlowName(flow.getFlowName());
        flowVO.setContent(flow.getContent());
        flowVO.setFlowType(flow.getFlowType());
        flowVO.setType(flow.getType());
        flowVO.setSort(flow.getPriority());
        return flowVO;
    }

    @Override
    public void dealData(FlowExamineRecord record, Long flowId) {
        Flow currentFlow = flowService.getByFlowId(flowId, record.getVersion());

        // 保存节点处理记录
        FlowDataDealRecord dataDealRecord = new FlowDataDealRecord();
        dataDealRecord.setIsMain(true);
        dataDealRecord.setRecordId(record.getRecordId());
        dataDealRecord.setFlowId(currentFlow.getFlowId());
        dataDealRecord.setDataId(record.getDataId());
        dataDealRecord.setUserId(record.getCreateUserId());
        dataDealRecord.setCreateUserId(record.getCreateUserId());
        dataDealRecord.setCreateTime(DateUtil.date());
        dataDealRecord.setUpdateTime(DateUtil.date());
        dataDealRecord.setModuleId(record.getModuleId());
        dataDealRecord.setVersion(record.getVersion());
        dataDealRecord.setFlowType(FlowTypeEnum.START.getType());
        dataDealRecord.setFlowStatus(FlowStatusEnum.PASS.getStatus());
        dataDealRecord.setBatchId(IdUtil.simpleUUID());
        dealRecordService.save(dataDealRecord);
        // 获取当前节点的下一个节点，继续处理数据
        Flow nextFlow = flowProvider.getNextOrUpperNextFlow(currentFlow);
        if (ObjectUtil.isNull(nextFlow)) {
            updateExamineStatus(record, currentFlow, FlowStatusEnum.PASS);
            return;
        }
        FlowTypeEnum flowTypeEnum = FlowTypeEnum.parse(nextFlow.getFlowType());
        IFlowTypeService flowTypeService = ApplicationContextHolder.getBean(flowTypeEnum.getServiceName());
        flowTypeService.dealData(record, nextFlow.getFlowId());
    }
}
