package com.kakarote.module.service;

import com.kakarote.module.entity.BO.FlowConditionQueryBO;
import com.kakarote.module.entity.BO.FlowDetailQueryBO;
import com.kakarote.module.entity.BO.FlowPreviewBO;
import com.kakarote.module.entity.BO.ModuleFieldValueBO;
import com.kakarote.module.entity.PO.Flow;
import com.kakarote.module.entity.PO.FlowExamineRecord;
import com.kakarote.module.entity.PO.FlowMetadata;
import com.kakarote.module.entity.VO.FlowConditionDataVO;
import com.kakarote.module.entity.VO.FlowVO;

import java.util.List;

/**
 * @author : zjj
 * @since : 2022/12/27
 */
public interface IFlowProvider {

    /**
     * 获取节点的下一个节点
     *
     * @param flow 当前节点
     * @return
     */
    Flow getNextOrUpperNextFlow(Flow flow);

    /**
     * 获取流程信息
     *
     * @param flowMetadata    流程元数据
     * @param ownerUserId    审批发起人
     * @return
     */
    List<FlowVO> getFlowVOList(FlowMetadata flowMetadata, Long ownerUserId);

    /**
     * 获取流程条件
     *
     * @param conditionQueryBO
     * @return
     */
    List<FlowConditionDataVO> queryConditionData(FlowConditionQueryBO conditionQueryBO);

    /**
     * 预览流程
     *
     * @param previewBO
     * @return
     */
    List<FlowVO> previewFlow(FlowPreviewBO previewBO);

    /**
     * 获取当前数据的流程详情
     *
     * @param queryBO queryBO
     * @return
     */
    List<FlowVO> flowDetail(FlowDetailQueryBO queryBO);

    /**
     * 获取条件审核的条件数据
     *
     * @param record 审核记录
     * @return
     */
    List<ModuleFieldValueBO> getConditionDataMap(FlowExamineRecord record);

    /**
     * 获取当前数据符合条件得第一个节点
     *
     * @param previewBO
     * @return
     */
    Flow getFirstFlow(FlowPreviewBO previewBO);
}
