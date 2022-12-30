package com.kakarote.module.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kakarote.common.entity.UserInfo;
import com.kakarote.common.servlet.ApplicationContextHolder;
import com.kakarote.common.utils.UserUtil;
import com.kakarote.module.common.ModuleCacheUtil;
import com.kakarote.module.entity.PO.ModuleFieldDataCommon;
import com.kakarote.module.entity.PO.ModuleEntity;
import com.kakarote.module.constant.FlowMetadataTypeEnum;
import com.kakarote.module.constant.FlowStatusEnum;
import com.kakarote.module.entity.BO.FlowSaveBO;
import com.kakarote.module.entity.BO.MessageBO;
import com.kakarote.module.entity.PO.*;
import com.kakarote.module.entity.VO.FlowVO;
import com.kakarote.module.entity.proto.FieldDataSnapshotBuf;

import java.util.*;

public interface IFlowTypeService extends IFlowCommonService {

    default void queryExamineData(Map<String, Object> map, String batchId) {
    }

    /**
     * 构建流程数据
     *
     * @param map         当前模块的流程数据
     * @param flow        当前流程
     * @param userInfos   所有的用户信息
     * @param ownerUserId 审批发起人
     * @return
     */
	FlowVO createFlowInfo(Map<String, Object> map, Flow flow, List<UserInfo> userInfos, Long ownerUserId);

    /**
     * 处理数据
     *
     * @param record 当前审核记录
     * @param flowId 待处理的节点
     */
    default void dealData(FlowExamineRecord record, Long flowId) {
    }

    default FieldDataSnapshotBuf.FieldDataSnapshot buildSnapshot(List<Map<String, Object>> list) {
        FieldDataSnapshotBuf.FieldDataSnapshot.Builder snapshotBuilder = FieldDataSnapshotBuf.FieldDataSnapshot.newBuilder();
        for (Map<String, Object> data : list) {
            FieldDataSnapshotBuf.FieldDataSnapshot.Data.Builder dataSnapshot =
                    FieldDataSnapshotBuf.FieldDataSnapshot.Data.newBuilder();
            data.forEach((k, v) -> {
                // dataSnapshot.putData(k,Optional.ofNullable(v).orElse("").toString());
                // 转为json字符串
                Object o = Optional.ofNullable(v).orElse("");
                if (o instanceof String) {
                    dataSnapshot.putData(k, o.toString());
                } else {
                    if (o instanceof Date) {
                        dataSnapshot.putData(k, DateUtil.formatDateTime(DateUtil.parse(o.toString())));
                    } else {
                        dataSnapshot.putData(k, JSON.toJSONString(o));
                    }
                }
            });
            snapshotBuilder.addFieldDataList(dataSnapshot);
        }
        return snapshotBuilder.build();
    }

    /**
     * 更新审核状态
     *
     * @param record
     * @param currentFlow
     * @param statusEnum
     */
    default void updateExamineStatus(FlowExamineRecord record, Flow currentFlow, FlowStatusEnum statusEnum) {
        IFlowExamineRecordService recordService = ApplicationContextHolder.getBean(IFlowExamineRecordService.class);
        // 更新审核记录状态
        record.setUpdateTime(DateUtil.date());
        record.setUpdateUserId(UserUtil.getUserId());
        record.setExamineStatus(statusEnum.getStatus());
        recordService.updateById(record);
        if (ObjectUtil.equal(FlowMetadataTypeEnum.SYSTEM.getType(), record.getFlowMetadataType())) {
            // 更新数据状态
            IModuleFieldDataCommonService dataCommonService = ApplicationContextHolder.getBean(IModuleFieldDataCommonService.class);
            ModuleFieldDataCommon dataCommon = dataCommonService.getByDataId(record.getDataId());
            dataCommon.setCurrentFlowId(currentFlow.getFlowId());
            dataCommon.setFlowStatus(statusEnum.getStatus());
            dataCommon.setFlowType(currentFlow.getFlowType());
            dataCommon.setUpdateTime(DateUtil.date());
            dataCommonService.updateById(dataCommon);
            savePage(record.getDataId(), record.getModuleId(), record.getVersion());
        }
        // 自定义按钮发送消息
        if (ObjectUtil.equal(FlowMetadataTypeEnum.CUSTOM_BUTTON.getType(), record.getFlowMetadataType())) {
            ModuleEntity module = ModuleCacheUtil.getByIdAndVersion(currentFlow.getModuleId(), currentFlow.getVersion());
            CustomButton customButton = ApplicationContextHolder.getBean(ICustomButtonService.class).getByButtonId(record.getModuleId(), record.getTypeId(), record.getVersion());
            MessageBO messageBO = new MessageBO();
            messageBO.setDataId(record.getDataId());
            String value = ApplicationContextHolder.getBean(IModuleFieldDataService.class).queryMainFieldValue(record.getDataId());
            messageBO.setValue(value);
            messageBO.setModuleId(record.getModuleId());
            messageBO.setModuleName(module.getName());
            messageBO.setTypeId(record.getTypeId());
            messageBO.setTypeName(customButton.getButtonName());
            JSONObject extData = new JSONObject();
            extData.fluentPut("flow", currentFlow).fluentPut("entity", customButton);
            messageBO.setExtData(extData.toJSONString());
            messageBO.setType(3);
            messageBO.setStatus(statusEnum.getStatus());
            messageBO.setReceivers(Collections.singletonList(record.getCreateUserId()));
            messageBO.setCreateUserId(record.getCreateUserId());
            IMessageService messageService = ApplicationContextHolder.getBean(IMessageService.class);
            messageService.sendMessage(messageBO);
        }
    }

}
