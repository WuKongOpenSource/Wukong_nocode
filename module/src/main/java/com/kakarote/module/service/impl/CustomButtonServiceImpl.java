package com.kakarote.module.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.kakarote.common.exception.BusinessException;
import com.kakarote.common.redis.Redis;
import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.common.utils.BaseUtil;
import com.kakarote.common.utils.UserUtil;
import com.kakarote.module.common.ModuleCacheUtil;
import com.kakarote.module.entity.PO.ModuleField;
import com.kakarote.module.entity.PO.ModuleFieldData;
import com.kakarote.module.entity.PO.ModuleEntity;
import com.kakarote.module.common.mq.ProducerUtil;
import com.kakarote.module.constant.*;
import com.kakarote.module.entity.BO.*;
import com.kakarote.module.entity.PO.*;
import com.kakarote.module.mapper.CustomButtonMapper;
import com.kakarote.module.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zjj
 * @title: CustomButtonServiceImpl
 * @description: 自定义按钮服务实现
 * @date 2022/3/16 14:49
 */
@Service
public class CustomButtonServiceImpl extends BaseServiceImpl<CustomButtonMapper, CustomButton> implements ICustomButtonService, ModulePageService {

    @Autowired
    private IFlowMetadataService flowMetadataService;

    @Autowired
    private IFlowExamineRecordService examineRecordService;

    @Autowired
    private ProducerUtil producerUtil;

    @Autowired
    private IModuleFieldService fieldService;

    @Autowired
    private IModuleFieldDataService fieldDataService;

    @Autowired
    private Redis redis;

    @Override
    public List<CustomButton> getByModuleIdAndVersion(Long moduleId, Integer version) {
        return lambdaQuery().eq(CustomButton::getModuleId, moduleId).eq(CustomButton::getVersion, version).list();
    }

    @Override
    public CustomButton getByButtonId(Long moduleId, Long buttonId, Integer version) {
        return lambdaQuery()
                .eq(CustomButton::getModuleId, moduleId)
                .eq(CustomButton::getButtonId, buttonId)
                .eq(CustomButton::getVersion, version)
                .one();
    }

    @Override
    public List<CustomButtonSaveBO> queryList(Long moduleId, Integer version) {
        List<CustomButton> buttons = this.getByModuleIdAndVersion(moduleId, version);
        if (CollUtil.isEmpty(buttons)) {
            return null;
        }
        List<CustomButtonSaveBO> result = buttons.stream().map(b -> BeanUtil.copyProperties(b, CustomButtonSaveBO.class)).collect(Collectors.toList());
        Map<Long, Long> buttonIdFlowMetaDataIdMap = flowMetadataService.getByModuleIdAndVersion(moduleId, version)
                .stream().filter(m -> ObjectUtil.equal(FlowMetadataTypeEnum.CUSTOM_BUTTON.getType(), m.getType()))
                .collect(Collectors.toMap(FlowMetadata::getTypeId, FlowMetadata::getMetadataId));
        result.forEach(b -> {
            Long flowMetaDataId = MapUtil.getLong(buttonIdFlowMetaDataIdMap, b.getButtonId());
            b.setFlowMetaDataId(flowMetaDataId);
        });
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FlowExamineRecord execute(ExecuteButtonRequestBO requestBO) {
        CustomButton button = this.getByButtonId(requestBO.getModuleId(), requestBO.getButtonId(), requestBO.getVersion());
        if (ObjectUtil.isNull(button)) {
            throw new BusinessException(ModuleCodeEnum.CUSTOM_BUTTON_NOT_FOUND);
        }
        FlowMetadata metadata = flowMetadataService.getByModuleId(requestBO.getModuleId(), requestBO.getVersion(),
                requestBO.getButtonId(), FlowMetadataTypeEnum.CUSTOM_BUTTON.getType());
        if (ObjectUtil.isNull(metadata)) {
            return null;
        }
        // 审核记录
        FlowExamineRecord record = examineRecordService.getRecordByModuleIdAndDataId(requestBO.getModuleId(), requestBO.getDataId(), button.getButtonId());
        if (ObjectUtil.isNotNull(record) && ObjectUtil.equal(FlowStatusEnum.WAIT.getStatus(),record.getExamineStatus())) {
            throw new BusinessException(ModuleCodeEnum.CUSTOM_BUTTON_DEALING);
        }
        record = new FlowExamineRecord();
        record.setRecordId(BaseUtil.getNextId());
        record.setModuleId(requestBO.getModuleId());
        record.setVersion(requestBO.getVersion());
        record.setDataId(requestBO.getDataId());
        record.setFlowMetadataId(metadata.getMetadataId());
        record.setCreateUserId(UserUtil.getUserId());
        record.setUpdateUserId(UserUtil.getUserId());
        record.setCreateTime(DateUtil.date());
        record.setUpdateTime(DateUtil.date());
        record.setExamineStatus(FlowStatusEnum.WAIT.getStatus());
        record.setTypeId(button.getButtonId());
        record.setFlowMetadataType(FlowMetadataTypeEnum.CUSTOM_BUTTON.getType());
        record.setBatchId(IdUtil.simpleUUID());
        record.setFlowId(0L);
        examineRecordService.save(record);
        // 发送MQ消息
        MsgBodyBO msgBody = new MsgBodyBO();
        msgBody.setMsgKey(IdUtil.simpleUUID());
        msgBody.setModuleId(requestBO.getModuleId());
        msgBody.setVersion(requestBO.getVersion());
        msgBody.setRecordId(record.getRecordId());
        msgBody.setFlowId(null);
        msgBody.setDataId(requestBO.getDataId());
        msgBody.setUserId(UserUtil.getUserId());
        msgBody.setMsgTag(MessageTagEnum.DEAL_FLOW);
        msgBody.setDelayTime(2000L);
        producerUtil.sendMsgToTopicOne(msgBody);
        return record;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveFieldData(CustomButtonFieldDataSaveBO saveBO) {
        List<ModuleFieldData> fieldDataList = saveBO.getFieldDataList();
        if (CollUtil.isEmpty(fieldDataList)) {
            return;
        }
        // 同时只有一个人可以填写
        String key = String.format("%s-%s-%s-%s", FlowTypeEnum.FILL.name(), saveBO.getModuleId(), saveBO.getButtonId(), saveBO.getDataId());
        if (!redis.setNx(key, 3L, 1)) {
            throw new BusinessException(ModuleCodeEnum.SOME_ONE_IS_FILLING_FIELD_DATA);
        }
        ModuleEntity module = ModuleCacheUtil.getByIdAndVersion(saveBO.getModuleId(), saveBO.getVersion());
        // 模块字段
        List<ModuleField> moduleFields = fieldService.getByModuleIdAndVersion(saveBO.getModuleId(), saveBO.getVersion(), null);
        Map<Long, String> fieldIdNameMap = moduleFields.stream().collect(Collectors.toMap(ModuleField::getFieldId,
                ModuleField::getFieldName));
        // 要填写的字段ID
        List<Long> fieldIds = new ArrayList<>();
        // 用户填写的数据
        Map<String, Object> filledData = new HashMap<>(16);
        // 保存字段值
        for (ModuleFieldData fieldData : fieldDataList) {
            fieldData.setDataId(saveBO.getDataId());
            fieldData.setModuleId(saveBO.getModuleId());
            fieldData.setCreateTime(DateUtil.date());
            fieldData.setVersion(module.getVersion());
            filledData.put(MapUtil.getStr(fieldIdNameMap, fieldData.getFieldId()), fieldData.getValue());
            fieldIds.add(fieldData.getFieldId());
        }
        // 先删除要填写字段的已有的数据
        fieldDataService.lambdaUpdate()
                .eq(ModuleFieldData::getDataId, saveBO.getDataId())
                .in(ModuleFieldData::getFieldId, fieldIds).remove();
        fieldDataService.saveOrUpdateBatch(fieldDataList);
        savePage(saveBO.getDataId(), saveBO.getModuleId(), saveBO.getVersion());
    }
}
