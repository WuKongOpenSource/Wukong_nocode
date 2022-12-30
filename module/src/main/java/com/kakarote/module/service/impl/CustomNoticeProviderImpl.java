package com.kakarote.module.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.kakarote.common.servlet.ApplicationContextHolder;
import com.kakarote.common.utils.UserUtil;
import com.kakarote.module.common.ModuleCacheUtil;
import com.kakarote.module.common.ModuleFieldCacheUtil;
import com.kakarote.module.common.TimeValueUtil;
import com.kakarote.module.entity.BO.*;
import com.kakarote.module.entity.PO.CustomNoticeRecord;
import com.kakarote.module.entity.PO.ModuleEntity;
import com.kakarote.module.entity.PO.ModuleField;
import com.kakarote.module.entity.PO.ModuleFieldDataCommon;
import com.kakarote.module.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * @author : zjj
 * @since : 2022/12/28
 */
@Service
public class CustomNoticeProviderImpl implements IFlowCommonService, ICustomNoticeProvider {

    @Autowired
    private IModuleFieldDataService fieldDataService;

    @Autowired
    private IModuleFieldDataProvider fieldDataProvider;

    @Autowired
    private ICustomNoticeReceiverService receiverService;

    @Autowired
    private IModuleFieldDataCommonService dataCommonService;

    @Autowired
    private ICustomNoticeService noticeService;

    @Autowired
    private ICustomNoticeRecordService recordService;

    /**
     * 处理消息记录
     *
     * @param record
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void dealNoticeRecord(CustomNoticeRecord record) {
        ModuleEntity module = ModuleCacheUtil.getActiveById(record.getModuleId());
        CustomNoticeSaveBO notice = noticeService.queryByNoticeId(record.getNoticeId(), record.getVersion());
        List<ModuleFieldValueBO> currentData = fieldDataProvider.queryValueMap(module.getModuleId(), module.getVersion(), record.getDataId(), null);
        // 字段值转换
        List<ModuleFieldValueBO> fieldValueBOS = fieldDataService.transFieldValue(record.getModuleId(), record.getVersion(), currentData);
        List<CommonConditionBO> conditionBOS = notice.getEffectConfig();
        Boolean isPass = commonConditionPass(conditionBOS, currentData, module.getModuleId(), module.getVersion());
        // 条件判断通过
        if (isPass) {
            // 接收人
            CustomNoticeReceiverSaveBO receiver = notice.getReceiveConfig();
            ModuleFieldDataCommon dataCommon = dataCommonService.getByDataId(record.getDataId());
            if (ObjectUtil.isNull(dataCommon)) {
                record.setStatus(2);
                recordService.saveOrUpdate(record);
                return;
            }
            Set<Long> receivers = new HashSet<>();
            try {
                UserUtil.setUser(dataCommon.getOwnerUserId());
                receivers = receiverService.getReceivers(receiver, record.getDataId());
            } finally {
                UserUtil.removeUser();
            }

            // 无接收人，则标记已处理
            if (CollUtil.isEmpty(receivers)) {
                record.setStatus(2);
                recordService.saveOrUpdate(record);
                return;
            }
            // 根据模块时间字段
            if (ObjectUtil.equal(3, notice.getEffectType())) {
                CustomNoticeSaveBO.CustomNoticeTimeFieldConfig timeFieldConfig = notice.getTimeFieldConfig();
                Long fieldId = timeFieldConfig.getFieldId();
                ModuleField field = ModuleFieldCacheUtil.getByIdAndVersion(module.getModuleId(), fieldId, module.getVersion());
                // 找不到字段，则标记已处理
                if (ObjectUtil.isNull(field)) {
                    record.setStatus(2);
                    recordService.saveOrUpdate(record);
                    return;
                }
                String value = fieldDataProvider.queryValue(record.getDataId(), fieldId);
                if (StrUtil.isEmpty(value)) {
                    record.setStatus(2);
                    recordService.saveOrUpdate(record);
                    return;
                }
                DateTime dataValue = DateUtil.parse(value);
                Calendar calendar = dataValue.toCalendar();
                calendar.set(Calendar.HOUR, timeFieldConfig.getHour());
                calendar.set(Calendar.MINUTE, timeFieldConfig.getMinute());
                Date effectTime = calendar.getTime();
                // 当前时间未到指定时间，跳过
                if (DateUtil.date().before(effectTime)) {
                    return;
                }
            }
            // 自定义时间
            if (ObjectUtil.equal(4, notice.getEffectType())) {
                Date effectTime = notice.getEffectTime();
                // 当前时间未到指定时间，跳过
                if (DateUtil.date().before(effectTime)) {
                    return;
                }
            }

            CustomNoticeSaveBO.CustomNoticeRepeatPeriod repeatPeriod = notice.getRepeatPeriod();
            // 当前重复次数
            Integer currentRepeatCount = record.getRepeatCount();
            // 不重复
            if (ObjectUtil.isNull(repeatPeriod)) {
                record.setStatus(1);
            } else {
                LocalDateTime lastDealTime = record.getLastDealTime();
                // 执行过
                if (ObjectUtil.isNotNull(lastDealTime)) {
                    Date effectTime = TimeValueUtil.addTime(lastDealTime, repeatPeriod.getValue(), repeatPeriod.getUnit());
                    // 当前时间未到指定时间，跳过
                    if (DateUtil.date().before(effectTime)) {
                        return;
                    }
                }
                // 指定重复次数
                if (ObjectUtil.equal(1, repeatPeriod.getStopType())) {
                    Integer repeatCount = repeatPeriod.getRepeatCount();
                    if (currentRepeatCount++ < repeatCount) {
                        record.setRepeatCount(currentRepeatCount);
                        if (ObjectUtil.equal(repeatCount, currentRepeatCount)) {
                            record.setLastDealTime(LocalDateTime.now());
                            record.setStatus(1);
                        }
                    }
                } else if (ObjectUtil.equal(2, repeatPeriod.getStopType())) {
                    // 根据表单内时间段
                    Long fieldId = repeatPeriod.getFieldId();
                    ModuleField field = ModuleFieldCacheUtil.getByIdAndVersion(module.getModuleId(), fieldId, module.getVersion());
                    // 找不到字段，则标记已处理
                    if (ObjectUtil.isNull(field)) {
                        record.setLastDealTime(LocalDateTime.now());
                        record.setStatus(2);
                        recordService.saveOrUpdate(record);
                        return;
                    }
                    String value = fieldDataProvider.queryValue(record.getDataId(), fieldId);
                    if (StrUtil.isEmpty(value)) {
                        record.setLastDealTime(LocalDateTime.now());
                        record.setStatus(2);
                        recordService.saveOrUpdate(record);
                        return;
                    }
                    DateTime dataValue = DateUtil.parse(value);
                    // 当前时间已过指定字段时间，则标记已处理
                    if (LocalDateTime.now().isAfter(dataValue.toLocalDateTime())) {
                        record.setLastDealTime(LocalDateTime.now());
                        record.setStatus(1);
                        recordService.saveOrUpdate(record);
                        return;
                    }
                } else {
                    // 永不结束
                    currentRepeatCount++;
                    record.setLastDealTime(LocalDateTime.now());
                    record.setRepeatCount(currentRepeatCount);
                }
            }
            // 发送通知
            this.sendMessage(module, notice, record.getDataId(), record.getBatchId(),
                    receiver.getContent(), fieldValueBOS, receivers);
        } else {
            record.setStatus(2);
        }
        recordService.saveOrUpdate(record);
    }

    /**
     * 发送通知
     *
     * @param module
     * @param notice
     * @param dataId
     * @param batchId
     * @param content
     * @param fieldValueBOS
     * @param receivers
     */
    private void sendMessage(ModuleEntity module, CustomNoticeSaveBO notice, Long dataId, String batchId,
                             String content, List<ModuleFieldValueBO> fieldValueBOS, Set<Long> receivers) {
        // 发送消息
        MessageBO messageBO = new MessageBO();
        messageBO.setDataId(dataId);
        messageBO.setBatchId(batchId);
        messageBO.setValue(content);
        messageBO.setModuleId(module.getModuleId());
        messageBO.setModuleName(module.getName());
        messageBO.setTypeId(notice.getNoticeId());
        messageBO.setTypeName(notice.getNoticeName());
        messageBO.setType(2);
        messageBO.setReceivers(receivers);
        messageBO.setCreateUserId(0L);
        messageBO.setExtData(new JSONObject().fluentPut("entity", fieldValueBOS).toJSONString());
        IMessageService messageService = ApplicationContextHolder.getBean(IMessageService.class);
        messageService.sendMessage(messageBO);
    }
}
