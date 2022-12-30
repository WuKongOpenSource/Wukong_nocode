package com.kakarote.module.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.kakarote.common.servlet.ApplicationContextHolder;
import com.kakarote.module.constant.FlowStatusEnum;
import com.kakarote.module.constant.FlowTypeEnum;
import com.kakarote.module.entity.BO.FlowPreviewBO;

import com.kakarote.module.entity.BO.ModuleFieldValueBO;
import com.kakarote.module.entity.PO.Flow;
import com.kakarote.module.entity.PO.FlowExamineRecord;
import com.kakarote.module.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author zjj
 * @ClassName FlowDealDataServiceImpl.java
 * @Description 节点处理
 * @createTime 2021-09-15
 */
@Service
public class FlowDealDataServiceImpl implements IFlowDealDataService {

    @Autowired
    private IFlowService flowService;

    @Autowired
    private IFlowProvider flowProvider;

    @Autowired
    private IModuleFieldDataProvider fieldDataProvider;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void dealDataFlow(FlowExamineRecord record, Long flowId) {
        // 如果节点数据已处理，直接返回
        if (FlowStatusEnum.stopFlow(record.getExamineStatus())) {
            return;
        }
        if (ObjectUtil.isNull(flowId)) {
            List<ModuleFieldValueBO> fieldValueBOS =  fieldDataProvider.queryValueMap(record.getModuleId(), record.getVersion(), record.getDataId(), null);
            FlowPreviewBO previewBO = new FlowPreviewBO();
            previewBO.setModuleId(record.getModuleId());
            previewBO.setVersion(record.getVersion());
            previewBO.setFlowMetadataType(record.getFlowMetadataType());
            previewBO.setTypeId(record.getTypeId());
            previewBO.setFieldValues(fieldValueBOS);
            Flow flow = flowProvider.getFirstFlow(previewBO);
            if (ObjectUtil.isNull(flow)) {
                return;
            }
            flowId = flow.getFlowId();
        }
        Flow currentFlow = flowService.getByFlowId(flowId, record.getVersion());
        FlowTypeEnum flowTypeEnum = FlowTypeEnum.parse(currentFlow.getFlowType());
        IFlowTypeService flowTypeService = ApplicationContextHolder.getBean(flowTypeEnum.getServiceName());
        flowTypeService.dealData(record, currentFlow.getFlowId());
    }
}
